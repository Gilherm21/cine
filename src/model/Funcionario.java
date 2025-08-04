package model;

public class Funcionario implements IImprimivel {
    private Long id;
    private String nome;
    private String funcao;
    private boolean ehFuncionarioDoMes;

    public Funcionario(Long id, String nome, String funcao) {
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.ehFuncionarioDoMes = false;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public boolean isEhFuncionarioDoMes() {
        return ehFuncionarioDoMes;
    }

    public void setEhFuncionarioDoMes(boolean ehFuncionarioDoMes) {
        this.ehFuncionarioDoMes = ehFuncionarioDoMes;
    }

    @Override
    public String toString() {
        String status = ehFuncionarioDoMes ? "⭐ FUNCIONÁRIO DO MÊS ⭐" : "";
        return "Funcionario [ID=" + id + ", Nome='" + nome + "', Função='" + funcao + "'] " + status;
    }

    @Override
    public String getDetalhesFormatados() {
        String status = ehFuncionarioDoMes ? "\n⭐ FUNCIONÁRIO DO MÊS ⭐" : "";
        return "--- Detalhes do Funcionário ---\n" +
               "ID: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Função: " + this.funcao +
               status + "\n" +
               "-------------------------------";
    }
}