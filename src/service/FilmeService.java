package service;

import exception.FilmeException;
import model.Filme;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilmeService {

    public FilmeService() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS filmes (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                     "titulo VARCHAR(255) NOT NULL," +
                     "sinopse TEXT," +
                     "duracao INT," +
                     "genero VARCHAR(100)," +
                     "classificacaoIndicativa INT" +
                     ");";
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar a tabela de filmes.", e);
        }
    }

    public void adicionar(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        criarFilme(titulo, sinopse, duracao, genero, classificacao);
        System.out.println("✅ Filme '" + titulo + "' adicionado com sucesso!");
    }

    public void adicionarInicial(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        if (buscarPorTitulo(titulo) == null) {
            criarFilme(titulo, sinopse, duracao, genero, classificacao);
        }
    }
    
    private void criarFilme(String titulo, String sinopse, int duracao, String genero, int classificacao) {
        if (titulo == null || titulo.isBlank()) {
            throw new FilmeException("O título do filme é obrigatório.");
        }
        String sql = "INSERT INTO filmes(titulo, sinopse, duracao, genero, classificacaoIndicativa) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, sinopse);
            pstmt.setInt(3, duracao);
            pstmt.setString(4, genero);
            pstmt.setInt(5, classificacao);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir filme no banco de dados.", e);
        }
    }

    public List<Filme> listar() {
        List<Filme> filmes = new ArrayList<>();
        String sql = "SELECT * FROM filmes";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Filme filme = new Filme(
                    rs.getLong("id"),
                    rs.getString("titulo"),
                    rs.getString("sinopse"),
                    rs.getInt("duracao"),
                    rs.getString("genero"),
                    rs.getInt("classificacaoIndicativa")
                );
                filmes.add(filme);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar filmes do banco de dados.", e);
        }
        
        if (filmes.isEmpty()) {
            System.out.println("Nenhum filme cadastrado.");
        }
        return filmes;
    }

    public Filme buscarPorId(Long id) {
        String sql = "SELECT * FROM filmes WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Filme(
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getString("sinopse"),
                        rs.getInt("duracao"),
                        rs.getString("genero"),
                        rs.getInt("classificacaoIndicativa")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar filme por ID.", e);
        }
        throw new FilmeException("Filme com ID " + id + " não encontrado.");
    }
    
    private Filme buscarPorTitulo(String titulo) {
        String sql = "SELECT * FROM filmes WHERE titulo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Filme(rs.getLong("id"), rs.getString("titulo"), rs.getString("sinopse"), rs.getInt("duracao"), rs.getString("genero"), rs.getInt("classificacaoIndicativa"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar filme por título.", e);
        }
        return null;
    }

    public void atualizar(Long id, String titulo, String sinopse, int duracao, String genero, int classificacao) {
        String sql = "UPDATE filmes SET titulo = ?, sinopse = ?, duracao = ?, genero = ?, classificacaoIndicativa = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, sinopse);
            pstmt.setInt(3, duracao);
            pstmt.setString(4, genero);
            pstmt.setInt(5, classificacao);
            pstmt.setLong(6, id);
            pstmt.executeUpdate();
            System.out.println("✅ Filme atualizado com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar filme.", e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM filmes WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("✅ Filme excluído com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir filme.", e);
        }
    }
}