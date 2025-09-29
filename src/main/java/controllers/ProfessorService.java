package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
// ... imports
import java.util.List;
import models.Professor;
import org.springframework.stereotype.Service; // NOVA LINHA

/**
 *
 * @author Paulo
 */
@Service // NOVA LINHA
public class ProfessorService {

    public static void adicionarProfessor(Professor professor) throws SQLException {
        String sql = "INSERT INTO professores (nome, telefone, cpf, endereco, nascimento, email, disciplina) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, professor.getTelefone());
            stmt.setString(3, professor.getCpf());
            stmt.setString(4, professor.getEndereco());
            stmt.setString(5, professor.getNascimento());
            stmt.setString(6, professor.getEmail());
            stmt.setString(7, professor.getDisciplina());
            stmt.executeUpdate();
        }
    }
    
    public static void removerProfessor(String nomeProfessor) throws SQLException {
        String sql = "DELETE FROM professores WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProfessor);
            stmt.executeUpdate();
        }
    }
    
    public static List<Professor> carregarProfessores() throws SQLException {
        List<Professor> listaProfessores = new ArrayList<>();
        String sql = "SELECT * FROM professores";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                String endereco = rs.getString("endereco");
                String nascimento = rs.getString("nascimento");
                String email = rs.getString("email");
                String disciplina = rs.getString("disciplina");
                listaProfessores.add(new Professor(nome, telefone, cpf, endereco, nascimento, email, disciplina));
            }
        }
        return listaProfessores;
    }
    
    public static void atualizarProfessor(Professor professor) throws SQLException {
        String sql = "UPDATE professores SET telefone = ?, cpf = ?, endereco = ?, nascimento = ?, email = ?, disciplina = ? WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, professor.getTelefone());
                pstmt.setString(2, professor.getCpf());
                pstmt.setString(3, professor.getEndereco());
                pstmt.setString(4, professor.getNascimento());
                pstmt.setString(5, professor.getEmail());
                pstmt.setString(6, professor.getDisciplina());
                pstmt.setString(7, professor.getNome());
                pstmt.executeUpdate();
        }
    }
}
