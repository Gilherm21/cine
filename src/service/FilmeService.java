package service;

import java.util.ArrayList;
import java.util.List;

import exception.FilmeException;
import model.Filme;

public class FilmeService {

    private final List<Filme> filmes = new ArrayList<>();
    private Long proximoId = 1L;

    public void adicionar(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        Filme filme = criarFilme(titulo, sinopse, duracao, genero, classificacao);
        System.out.println("✅ Filme '" + titulo + "' adicionado com sucesso!");
    }

    public void adicionarInicial(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        criarFilme(titulo, sinopse, duracao, genero, classificacao);
    }
    
    private Filme criarFilme(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        if (titulo == null || titulo.isBlank()) {
            throw new FilmeException("O título do filme é obrigatório.");
        }
        Filme filme = new Filme(proximoId++, titulo, sinopse, duracao, genero, classificacao);
        filmes.add(filme);
        return filme;
    }

    public List<Filme> listar() {
        if (filmes.isEmpty()) {
            System.out.println("Nenhum filme cadastrado.");
        }
        return filmes;
    }

    public Filme buscarPorId(Long id) {
        return filmes.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new FilmeException("Filme com ID " + id + " não encontrado."));
    }

    public void atualizar(Long id, String titulo, String sinopse, int duracao, String genero, int classificacao) {
        Filme filme = buscarPorId(id);

        filme.setTitulo(titulo);
        filme.setSinopse(sinopse);
        filme.setDuracao(duracao);
        filme.setGenero(genero);
        filme.setClassificacaoIndicativa(classificacao);
        
        System.out.println("✅ Filme atualizado com sucesso!");
    }

    public void excluir(Long id) {
        Filme filme = buscarPorId(id);
        filmes.remove(filme);
        System.out.println("✅ Filme excluído com sucesso!");
    }
}