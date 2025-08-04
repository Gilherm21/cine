package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Funcionario;

public class FuncionarioService {
    
    public FuncionarioService() {
        criarTabelaSeNaoExistir();
        popularDadosIniciais();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                     "nome VARCHAR(255) NOT NULL," +
                     "funcao VARCHAR(255) NOT NULL," +
                     "ehFuncionarioDoMes BOOLEAN DEFAULT FALSE" +
                     ");";
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar a tabela de funcionários.", e);
        }
    }

    private void popularDadosIniciais() {
        if (listar().isEmpty()) {
            adicionar("João Silva", "Gerente");
            adicionar("Maria Oliveira", "Atendente de Bilheteria");
            adicionar("Carlos Pereira", "Projecionista");
            adicionar("Ana Costa", "Atendente da Bomboniere");
            adicionar("Pedro Souza", "Limpeza");
        }
    }

    public void adicionar(String nome, String funcao) {
        if (nome == null || nome.isBlank() || funcao == null || funcao.isBlank()) {
            throw new RuntimeException("Nome e função são obrigatórios.");
        }
        String sql = "INSERT INTO funcionarios(nome, funcao) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, funcao);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir funcionário.", e);
        }
    }

    public List<Funcionario> listar() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Funcionario funcionario = new Funcionario(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("funcao")
                );
                funcionario.setEhFuncionarioDoMes(rs.getBoolean("ehFuncionarioDoMes"));
                funcionarios.add(funcionario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar funcionários.", e);
        }
        return funcionarios;
    }

    public Funcionario buscarPorId(Long id) {
        String sql = "SELECT * FROM funcionarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("funcao")
                    );
                    funcionario.setEhFuncionarioDoMes(rs.getBoolean("ehFuncionarioDoMes"));
                    return funcionario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionário por ID.", e);
        }
        throw new RuntimeException("Funcionário não encontrado.");
    }

    public void demitir(Long id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            buscarPorId(id); // Verifica se existe antes de deletar
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("✅ Funcionário demitido com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao demitir funcionário.", e);
        }
    }

    public void elegerFuncionarioDoMes(Long id) {
        String sqlReset = "UPDATE funcionarios SET ehFuncionarioDoMes = FALSE";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlReset);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao resetar funcionário do mês.", e);
        }

        String sqlElege = "UPDATE funcionarios SET ehFuncionarioDoMes = TRUE WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlElege)) {
            Funcionario eleito = buscarPorId(id);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("✅ " + eleito.getNome() + " foi eleito(a) o(a) funcionário(a) do mês!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eleger funcionário do mês.", e);
        }
    }
}