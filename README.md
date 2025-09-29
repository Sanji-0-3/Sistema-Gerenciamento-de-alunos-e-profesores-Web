WebEscolar
Sistema de Gerenciamento Escolar desenvolvido em Spring Boot, utilizando MySQL como banco de dados. O projeto possui uma arquitetura híbrida, oferecendo endpoints RESTful para uma interface web (SPA com HTML/CSS/JS) e também interfaces Desktop em Java Swing (legacy).

🚀 Tecnologias
Linguagem: Java 17
Framework: Spring Boot (WebMVC)
Banco de Dados: MySQL
Build Tool: Apache Maven
Interface Web (Frontend): HTML, CSS, JavaScript (Localizada em src/main/resources/static/)
Interface Desktop (Legacy): Java Swing (Classes no pacote views)
✨ Funcionalidades
O sistema oferece gerenciamento completo para Alunos e Professores, incluindo as seguintes operações via API REST e interface Desktop:

Login/Autenticação: Credenciais fixas (usuário: admin, senha: 1234).
Gerenciamento de Professores: Listar, Cadastrar, Excluir e Atualizar dados (CRUD).
Gerenciamento de Alunos: Listar, Cadastrar, Excluir e Atualizar dados (CRUD).
⚙️ Configuração e Execução
1. Banco de Dados MySQL
Certifique-se de ter o MySQL instalado e crie um esquema (database) chamado escola.

As credenciais de conexão estão definidas nos arquivos de configuração:

Parâmetro	Valor

URL	jdbc:mysql://localhost/seu banco
Usuário	seu usuario
Senha	sua senha
Nota: As tabelas professores e alunos devem ser criadas manualmente com as colunas correspondentes aos campos dos Models (Professor.java e Aluno.java).

2. Execução do Spring Boot
A aplicação pode ser executada diretamente com o Maven (na pasta raiz do projeto):

./mvnw spring-boot:run
