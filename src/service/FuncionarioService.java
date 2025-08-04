package service;

import model.Funcionario;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioService implements Gerenciavel<Funcionario> {
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private Long proximoId = 1L;

    public FuncionarioService() {
        adicionar(new Funcionario(0L, "João Silva", "Gerente"));
        adicionar(new Funcionario(0L, "Maria Oliveira", "Atendente de Bilheteria"));
        adicionar(new Funcionario(0L, "Carlos Pereira", "Projecionista"));
        adicionar(new Funcionario(0L, "Ana Costa", "Atendente da Bomboniere"));
        adicionar(new Funcionario(0L, "Pedro Souza", "Limpeza"));
    }

    @Override
    public void adicionar(Funcionario funcionario) {
        if (funcionario.getNome() == null || funcionario.getNome().isBlank() || funcionario.getFuncao() == null || funcionario.getFuncao().isBlank()) {
            throw new RuntimeException("Nome e função são obrigatórios.");
        }
        funcionario.setId(proximoId++);
        funcionarios.add(funcionario);
    }

    @Override
    public List<Funcionario> listar() {
        return funcionarios;
    }

    @Override
    public Funcionario buscarPorId(Long id) {
        return funcionarios.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));
    }

    @Override
    public void excluir(Long id) {
        Funcionario funcionario = buscarPorId(id);
        funcionarios.remove(funcionario);
    }

    public void elegerFuncionarioDoMes(Long id) {
        Funcionario eleito = buscarPorId(id);
        funcionarios.forEach(f -> f.setEhFuncionarioDoMes(false));
        eleito.setEhFuncionarioDoMes(true);
    }
}