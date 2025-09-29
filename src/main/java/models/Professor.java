
package models;

/**
 *
 * @author Paulo
 */
public class Professor {
    private String nome, telefone, cpf, endereco, nascimento, email, disciplina;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }
    
    public Professor(String nome, String telefone, String cpf, String endereco, String nascimento, String email, String disciplina) {
        this.nome = nome;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.nascimento = nascimento;
        this.email = email;
        this.disciplina = disciplina;
    }
    
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public String getNascimento() { return nascimento; }
    public String getEmail() { return email; }
    public String getDisciplina() { return disciplina; }
    
    
}
