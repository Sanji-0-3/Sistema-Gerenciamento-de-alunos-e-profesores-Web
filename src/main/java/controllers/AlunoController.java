package controllers;

import controllers.AlunoService;
import models.Aluno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunoController {

    private static final Logger LOGGER = Logger.getLogger(AlunoController.class.getName());

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    public ResponseEntity<List<Aluno>> listarAlunos() {
        try {
            List<Aluno> alunos = alunoService.carregarAlunos();
            return ResponseEntity.ok(alunos);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar alunos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarAluno(@RequestBody Aluno aluno) {
        try {
            // Conversão de data do formato web (DD/MM/AAAA) para o formato DB (YYYY-MM-DD) esperado pelo Service
            String nascimentoWeb = aluno.getNascimento();
            if (nascimentoWeb != null && nascimentoWeb.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = nascimentoWeb.split("/");
                String nascimentoDB = parts[2] + "-" + parts[1] + "-" + parts[0];
                aluno.setNascimento(nascimentoDB);
            }

            alunoService.adicionarAluno(aluno);
            return ResponseEntity.status(HttpStatus.CREATED).body("Aluno cadastrado com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar aluno", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar aluno: " + e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos para cadastro: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{nome}")
    public ResponseEntity<String> excluirAluno(@PathVariable String nome) {
        try {
            alunoService.removerAluno(nome);
            return ResponseEntity.ok("Aluno removido com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao remover aluno", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover aluno: " + e.getMessage());
        }
    }
    
    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarAluno(@RequestBody Aluno aluno) {
        try {
            // Conversão de data do formato web (DD/MM/AAAA) para o formato DB (YYYY-MM-DD) esperado pelo Service
            String nascimentoWeb = aluno.getNascimento();
            if (nascimentoWeb != null && nascimentoWeb.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = nascimentoWeb.split("/");
                String nascimentoDB = parts[2] + "-" + parts[1] + "-" + parts[0];
                aluno.setNascimento(nascimentoDB);
            }
            
            alunoService.atualizarAluno(aluno); // Chama o Service com o novo objeto Aluno
            return ResponseEntity.ok("Aluno atualizado com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar aluno", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar aluno: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos para atualização: " + e.getMessage());
        }
    }
    
}