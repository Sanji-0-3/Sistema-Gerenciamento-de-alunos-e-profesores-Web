document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api';

    // --- FUNÇÕES DE UTILIDADE ---

    // Utilitário para forçar o cursor no elemento editável
    function setCursorAtEnd(element) {
        element.focus(); 
        if (typeof window.getSelection != "undefined" && typeof document.createRange != "undefined") {
            const range = document.createRange();
            range.selectNodeContents(element);
            range.collapse(false); // Move o cursor para o final
            const selection = window.getSelection();
            selection.removeAllRanges();
            selection.addRange(range);
        } else if (typeof document.body.createTextRange != "undefined") {
            const textRange = document.body.createTextRange();
            textRange.moveToElementText(element);
            textRange.collapse(false);
            textRange.select();
        }
    }

    // NOVO Handler de clique para a célula (só ativo no modo edição)
    function cellEditClickHandler(event) {
        event.stopPropagation(); // <--- CHAVE DA CORREÇÃO: Impede o clique de subir para a linha (<tr>)
        setCursorAtEnd(this); // Garante que o cursor é posicionado
    }

    function formatNascimento(dateString) {
        if (!dateString || dateString.length < 10) return '';
        const parts = dateString.split('-');
        if (parts.length === 3) {
            return `${parts[2]}/${parts[1]}/${parts[0]}`;
        }
        return dateString;
    }

    async function fetchData(url) {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erro ao buscar dados: ${response.statusText}`);
        }
        return response.json();
    }

    async function sendData(url, method, data = null) {
        const headers = { 'Content-Type': 'application/json' };
        const config = { method: method, headers: headers, body: data ? JSON.stringify(data) : null };
        const response = await fetch(url, config);
        const responseText = await response.text().catch(() => null);

        if (!response.ok) {
            alert(responseText || `Erro na operação: ${response.statusText}`);
            throw new Error(responseText);
        }
        return responseText;
    }

    // --- VARIÁVEIS GLOBAIS DE ESTADO ---
    let selectedAlunoName = null;
    let selectedProfessorName = null;
    let editingOriginalName = null; 

    // --- FUNÇÕES GERAIS DE TABELA ---

    // Função para limpar seleção e desativar edição em todas as linhas da tabela
    function disableEditing(tableBody) {
        tableBody.querySelectorAll('tr').forEach(r => {
            r.classList.remove('selected-row'); 
            r.querySelectorAll('td').forEach(cell => {
                cell.setAttribute('contentEditable', 'false');
                cell.style.border = '1px solid transparent'; 
                
                // Remove o listener de clique que força o cursor
                cell.removeEventListener('click', cellEditClickHandler);
            });
        });
        editingOriginalName = null; 
    }

    // Função para tratar o clique na linha (somente seleciona)
    function handleRowClick(event, tableBody, isProfessor) {
        const row = event.currentTarget;
        
        // Se já estiver em modo de edição (e o stopPropagation falhar por algum motivo), ignora.
        if (editingOriginalName) {
            return;
        }

        // 1. Limpa todas as seleções e modo de edição
        disableEditing(tableBody); 
        
        // 2. Define a linha como "selecionada"
        row.classList.add('selected-row');
        
        // 3. Armazena a chave de nome (primeira célula)
        const name = row.cells[0].textContent.trim();
        if (isProfessor) {
            selectedProfessorName = name;
            selectedAlunoName = null; 
        } else {
            selectedAlunoName = name;
            selectedProfessorName = null; 
        }
    }

    // Função para ativar o modo de edição (CORREÇÃO B03 APLICADA)
    function activateEditing(tableBody, isProfessor) {
        const selectedRow = tableBody.querySelector('.selected-row');
        if (!selectedRow) {
            alert('Selecione uma linha para editar primeiro.');
            return;
        }
        
        const cells = selectedRow.querySelectorAll('td');
        
        editingOriginalName = cells[0].textContent.trim(); 

        // Torna todas as células editáveis e adiciona um visual de edição
        cells.forEach((cell, index) => { // Adicionado 'index' para identificar a primeira coluna
            // CORREÇÃO B03: A célula 0 (Nome/Chave) não deve ser editável.
            if (index === 0) {
                cell.setAttribute('contentEditable', 'false');
                cell.style.border = '1px solid transparent'; 
            } else {
                cell.setAttribute('contentEditable', 'true');
                cell.style.border = '1px dashed #ffc107'; 
                
                // <--- CHAVE DA CORREÇÃO: Adiciona o manipulador de clique para forçar o cursor
                cell.addEventListener('click', cellEditClickHandler);
            }
        });

        alert('Modo de edição ativado. Pressione "Atualizar" para salvar as alterações.');
        
        // Foca na primeira célula editável (índice 1) e coloca o cursor no final do conteúdo
        setCursorAtEnd(cells[1] || cells[0]); 
    }

    // --- LÓGICA DE SALVAMENTO (PUT) (CORREÇÕES B02 e B03 APLICADAS) ---
    async function handleUpdate(tableBody, isProfessor) {
        const selectedRow = tableBody.querySelector('.selected-row');
        
        if (!editingOriginalName) {
            alert('Você deve clicar em "Editar" e fazer alterações antes de Atualizar.');
            return;
        }
        
        const cells = selectedRow.querySelectorAll('td');
        const nomeChave = editingOriginalName; // CORREÇÃO B03: Usar o nome original (chave)
        
        let updatedData = {};
        let apiUrl = '';

        if (isProfessor) {
            updatedData = {
                // CORREÇÃO B03: Nome original usado para a cláusula WHERE do Service
                nome: nomeChave, 
                telefone: cells[1].textContent.trim(),
                cpf: cells[2].textContent.trim(),
                endereco: cells[3].textContent.trim(),
                nascimento: cells[4].textContent.trim(), // DD/MM/AAAA
                email: cells[5].textContent.trim(),
                disciplina: cells[6].textContent.trim()
            };
            apiUrl = `${API_BASE_URL}/professores/atualizar`;
        } else {
            updatedData = {
                // CORREÇÃO B03: Nome original usado para a cláusula WHERE do Service
                nome: nomeChave, 
                nascimento: cells[1].textContent.trim(),
                turma: cells[2].textContent.trim(),
                email: cells[3].textContent.trim()
            };
            apiUrl = `${API_BASE_URL}/alunos/atualizar`;
        }
        
        // A linha anterior 'updatedData.nome = nomeEditado;' foi removida (Correção B03).

        try {
            const message = await sendData(apiUrl, 'PUT', updatedData);
            alert("Alterações salvas com sucesso!"); // CORREÇÃO B02: Mensagem corrigida
            
            disableEditing(tableBody);
            if (isProfessor) await carregarProfessores();
            else await carregarAlunos();
            
        } catch (error) {
            console.error(error);
        }
    }

    // --- LÓGICA DE LOGIN (index.html) ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            if (username.trim() === '' || password.trim() === '') {
                alert('Todos os campos devem ser preenchidos!');
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    const data = await response.json();
                    if (data.success) {
                        alert('Login bem-sucedido!');
                        window.location.href = 'menu.html';
                    } else {
                        alert(data.message || 'Usuário ou senha incorretos.');
                    }
                } else {
                    const errorText = await response.text().catch(() => 'Erro desconhecido do servidor.');
                    let message = `Falha no Login (Status: ${response.status}).`;
                    
                    try {
                        const errorData = JSON.parse(errorText);
                        message = errorData.message || message;
                    } catch (e) {
                        message += ` ${errorText.substring(0, 100)}...`; 
                    }
                    alert(message);
                }
            } catch (error) {
                console.error("Erro na comunicação:", error);
                alert('Erro de conexão com o servidor (Verifique se o Spring Boot está rodando na porta 8080).');
            }
        });
    }

    // --- LÓGICA DE ALUNOS (alunos.html) ---
    const alunosTableBody = document.querySelector('#alunosTable tbody');
    const btnCadastrar = document.getElementById('btnCadastrar');
    const btnExcluir = document.getElementById('btnExcluir');
    const btnEditarAluno = document.getElementById('btnEditarAluno'); 
    const btnSalvarLista = document.getElementById('btnSalvar'); 
    const cadastroFormContainer = document.getElementById('cadastroFormContainer');
    const cadastroForm = document.getElementById('cadastroForm');
    
    async function carregarAlunos() {
        if (!alunosTableBody) return;
        try {
            const alunos = await fetchData(`${API_BASE_URL}/alunos`);
            alunosTableBody.innerHTML = '';
            alunos.forEach(aluno => {
                const row = alunosTableBody.insertRow();
                row.innerHTML = `
                    <td>${aluno.nome}</td>
                    <td>${formatNascimento(aluno.nascimento)}</td>
                    <td>${aluno.turma}</td>
                    <td>${aluno.email}</td>
                `;
                row.addEventListener('click', (e) => handleRowClick(e, alunosTableBody, false));
            });
        } catch (error) { console.error(error); }
    }

    // Botoes de Ação para Alunos
    if (btnEditarAluno) {
        btnEditarAluno.addEventListener('click', () => activateEditing(alunosTableBody, false));
    }
    if (btnSalvarLista) {
        btnSalvarLista.addEventListener('click', () => handleUpdate(alunosTableBody, false));
    }
    if (btnCadastrar) {
        btnCadastrar.addEventListener('click', () => {
            cadastroFormContainer.style.display = 'block';
            btnCadastrar.style.display = 'none'; 
            cadastroForm.reset();
            disableEditing(alunosTableBody);
        });
    }
    if (btnExcluir) {
        btnExcluir.addEventListener('click', async () => {
            if (!selectedAlunoName) {
                alert('Selecione um aluno para excluir.');
                return;
            }
            if (confirm(`Tem certeza que deseja excluir o aluno: ${selectedAlunoName}?`)) {
                try {
                    await sendData(`${API_BASE_URL}/alunos/excluir/${selectedAlunoName}`, 'DELETE');
                    alert('Aluno excluído com sucesso.');
                    disableEditing(alunosTableBody);
                    await carregarAlunos();
                } catch (error) { console.error(error); }
            }
        });
    }
    
    // Submissão do Formulário de Cadastro (POST)
    if (cadastroForm) {
        cadastroForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const alunoData = {
                nome: document.getElementById('nome').value, nascimento: document.getElementById('nascimento').value, 
                turma: document.getElementById('turma').value, email: document.getElementById('email').value
            };
            if (Object.values(alunoData).some(val => val.trim() === '')) {
                alert('Todos os campos devem ser preenchidos!'); return;
            }
            try {
                const message = await sendData(`${API_BASE_URL}/alunos/cadastrar`, 'POST', alunoData);
                alert(message);
                cadastroFormContainer.style.display = 'none';
                btnCadastrar.style.display = 'block';
                await carregarAlunos();
            } catch (error) { console.error(error); }
        });
    }

    // --- LÓGICA DE PROFESSORES (professores.html) ---
    const professoresTableBody = document.querySelector('#professoresTable tbody');
    const btnCadastrarProfessor = document.getElementById('btnCadastrarProfessor');
    const btnExcluirProfessor = document.getElementById('btnExcluirProfessor');
    const btnEditarProfessor = document.getElementById('btnEditarProfessor');
    const btnSalvarProfessor = document.getElementById('btnSalvarProfessor');
    // Adicionando variáveis do formulário de Professor (necessário para o B01)
    const cadastroProfessorFormContainer = document.getElementById('cadastroProfessorFormContainer'); 
    const cadastroProfessorForm = document.getElementById('cadastroProfessorForm'); 

    async function carregarProfessores() {
        if (!professoresTableBody) return; 
        try {
            const professores = await fetchData(`${API_BASE_URL}/professores`);
            professoresTableBody.innerHTML = '';
            professores.forEach(professor => {
                const row = professoresTableBody.insertRow();
                row.innerHTML = `
                    <td>${professor.nome}</td><td>${professor.telefone}</td><td>${professor.cpf}</td>
                    <td>${professor.endereco}</td><td>${formatNascimento(professor.nascimento)}</td>
                    <td>${professor.email}</td><td>${professor.disciplina}</td>
                `;
                row.addEventListener('click', (e) => handleRowClick(e, professoresTableBody, true));
            });
        } catch (error) { console.error(error); }
    }

    // Botoes de Ação para Professores
    if (btnEditarProfessor) {
        btnEditarProfessor.addEventListener('click', () => activateEditing(professoresTableBody, true));
    }
    if (btnSalvarProfessor) {
        btnSalvarProfessor.addEventListener('click', () => handleUpdate(professoresTableBody, true));
    }
    if (btnCadastrarProfessor) {
        btnCadastrarProfessor.addEventListener('click', () => {
            // Presume que o ID do container do formulário de professor seja 'cadastroProfessorFormContainer'
            if(cadastroProfessorFormContainer) cadastroProfessorFormContainer.style.display = 'block'; 
            btnCadastrarProfessor.style.display = 'none';
            if(cadastroProfessorForm) cadastroProfessorForm.reset();
            disableEditing(professoresTableBody);
        });
    }
    if (btnExcluirProfessor) {
        btnExcluirProfessor.addEventListener('click', async () => {
            if (!selectedProfessorName) {
                alert('Selecione um professor para excluir.');
                return;
            }
            if (confirm(`Tem certeza que deseja excluir o professor: ${selectedProfessorName}?`)) {
                try {
                    await sendData(`${API_BASE_URL}/professores/excluir/${selectedProfessorName}`, 'DELETE');
                    alert('Professor excluído com sucesso.');
                    disableEditing(professoresTableBody);
                    await carregarProfessores();
                } catch (error) { console.error(error); }
            }
        });
    }
    
    // Submissão do Formulário de Cadastro de Professor (POST) - CORREÇÃO B01: Adicionado o bloco
    if (cadastroProfessorForm) {
        cadastroProfessorForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            // Presume que os IDs dos campos do formulário de professor sejam: 
            // nomeProfessor, telefoneProfessor, cpfProfessor, enderecoProfessor, nascimentoProfessor, emailProfessor, disciplinaProfessor
            const professorData = {
                nome: document.getElementById('nomeProfessor').value, 
                telefone: document.getElementById('telefoneProfessor').value, 
                cpf: document.getElementById('cpfProfessor').value,
                endereco: document.getElementById('enderecoProfessor').value,
                nascimento: document.getElementById('nascimentoProfessor').value, // DD/MM/AAAA
                email: document.getElementById('emailProfessor').value,
                disciplina: document.getElementById('disciplinaProfessor').value
            };
            
            // Validação de campos vazios (Requisito CT07)
            if (Object.values(professorData).some(val => val.trim() === '')) {
                alert('Todos os campos devem ser preenchidos!'); return;
            }
            
            try {
                const message = await sendData(`${API_BASE_URL}/professores/cadastrar`, 'POST', professorData);
                alert(message); // Esperado: "Professor cadastrado com sucesso!" (Requisito CT06)
                if(cadastroProfessorFormContainer) cadastroProfessorFormContainer.style.display = 'none';
                btnCadastrarProfessor.style.display = 'block';
                await carregarProfessores(); // Atualiza a lista (Requisito CT06)
            } catch (error) { console.error(error); }
        });
    }


    // --- INICIALIZAÇÃO ---
    if (alunosTableBody) carregarAlunos();
    if (professoresTableBody) carregarProfessores();
});