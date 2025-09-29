package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Aluno;
import org.springframework.stereotype.Service; // NOVA LINHA

/**
 *
 * @author Paulo
 */
@Service // NOVA LINHA
public class AlunoService {

    public static void adicionarAluno(Aluno aluno) throws SQLException {
        String sql = "INSERT INTO alunos (nome, nascimento, turma, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getNascimento());
            stmt.setString(3, aluno.getTurma());
            stmt.setString(4, aluno.getEmail());
            stmt.executeUpdate();
        }
    }

    public static void removerAluno(String nomeAluno) throws SQLException {
        String sql = "DELETE FROM alunos WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeAluno);
            stmt.executeUpdate();
        }
    }
    
    public static List<Aluno> carregarAlunos() throws SQLException {
        List<Aluno> listaAlunos = new ArrayList<>();
        String sql = "SELECT * FROM alunos";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                String nascimento = rs.getString("nascimento");
                String turma = rs.getString("turma");
                String email = rs.getString("email");
                listaAlunos.add(new Aluno(nome, nascimento, turma, email));
            }
        }
        return listaAlunos;
    }
    
    public static void atualizarAluno(Aluno aluno) throws SQLException {
        String sql = "UPDATE alunos SET nascimento = ?, turma = ?, email = ? WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, aluno.getNascimento());
            pstmt.setString(2, aluno.getTurma());
            pstmt.setString(3, aluno.getEmail());
            pstmt.setString(4, aluno.getNome());
            pstmt.executeUpdate();
        }
    }
}