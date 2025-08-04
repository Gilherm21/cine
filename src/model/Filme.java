package model;

public class Filme implements IImprimivel {
    private Long id;
    private String titulo;
    private String sinopse;
    private int duracao;
    private String genero;
    private int classificacaoIndicativa;

    public Filme(Long id, String titulo, String sinopse, int duracao, String genero, int classificacaoIndicativa) {
        this.id = id;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.duracao = duracao;
        this.genero = genero;
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public int getClassificacaoIndicativa() { return classificacaoIndicativa; }
    public void setClassificacaoIndicativa(int classificacaoIndicativa) { this.classificacaoIndicativa = classificacaoIndicativa; }

    @Override
    public String toString() {
        return "Filme [ID=" + id + ", Título='" + titulo + "']";
    }

    @Override
    public String getDetalhesFormatados() {
        return "--- Detalhes do Filme ---\n" +
               "ID: " + this.id + "\n" +
               "Título: " + this.titulo + "\n" +
               "Sinopse: " + this.sinopse + "\n" +
               "Gênero: " + this.genero + "\n" +
               "Classificação: " + this.classificacaoIndicativa + " anos\n" +
               "-------------------------";
    }
}