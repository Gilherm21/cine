package model;

public class Cliente extends Usuario implements IImprimivel {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private int idade;

    public Cliente(Long id, String nome, String cpf, String email, String telefone, int idade) {
        super(email, cpf);
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.idade = idade;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }

    @Override
    public String toString() {
        return "Cliente [ID=" + id + ", Nome='" + nome + "', CPF='" + cpf + "']";
    }

    @Override
    public String getDetalhesFormatados() {
        return "--- Detalhes do Cliente ---\n" +
               "ID: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Email: " + this.email + "\n" +
               "---------------------------";
    }
}