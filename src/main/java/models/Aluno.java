
package models;

/**
 *
 * @author Paulo
 */
public class Aluno {
    private String nome, nascimento, turma, email;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public Aluno(String nome, String nascimento, String turma, String email) {
        this.nome = nome;
        this.nascimento = nascimento;
        this.turma = turma;
        this.email = email;
    }
    
    public String getNome() { return nome; }
    public String getNascimento() { return nascimento; }
    public String getTurma() { return turma; }
    public String getEmail() { return email; }
}
