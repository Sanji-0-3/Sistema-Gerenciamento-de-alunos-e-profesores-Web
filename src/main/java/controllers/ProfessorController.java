package controllers;

import controllers.ProfessorService;
import models.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = "*")
public class ProfessorController {

    private static final Logger LOGGER = Logger.getLogger(ProfessorController.class.getName());

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<Professor>> listarProfessores() {
        try {
            List<Professor> professores = professorService.carregarProfessores();
            return ResponseEntity.ok(professores);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar professores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarProfessor(@RequestBody Professor professor) {
        try {
            // Conversão de data do formato web (DD/MM/AAAA) para o formato DB (YYYY-MM-DD) esperado pelo Service
            String nascimentoWeb = professor.getNascimento();
            if (nascimentoWeb != null && nascimentoWeb.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = nascimentoWeb.split("/");
                String nascimentoDB = parts[2] + "-" + parts[1] + "-" + parts[0];
                professor.setNascimento(nascimentoDB);
            }
            
            professorService.adicionarProfessor(professor);
            return ResponseEntity.status(HttpStatus.CREATED).body("Professor cadastrado com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar professor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar professor: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos para cadastro: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{nome}")
    public ResponseEntity<String> excluirProfessor(@PathVariable String nome) {
        try {
            professorService.removerProfessor(nome);
            return ResponseEntity.ok("Professor removido com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao remover professor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover professor: " + e.getMessage());
        }
    }
    
    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarProfessor(@RequestBody Professor professor) {
        try {
            // Conversão de data do formato web (DD/MM/AAAA) para o formato DB (YYYY-MM-DD) esperado pelo Service
            String nascimentoWeb = professor.getNascimento();
            if (nascimentoWeb != null && nascimentoWeb.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = nascimentoWeb.split("/");
                String nascimentoDB = parts[2] + "-" + parts[1] + "-" + parts[0];
                professor.setNascimento(nascimentoDB);
            }
            
            professorService.atualizarProfessor(professor); // Chama o Service com o novo objeto Professor
            return ResponseEntity.ok("Professor atualizado com sucesso!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar professor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar professor: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos para atualização: " + e.getMessage());
        }
    }
}
