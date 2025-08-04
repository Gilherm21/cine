package service;

import java.util.ArrayList;
import java.util.List;

import exception.ClienteException;
import model.Cliente;

public class ClienteService {
    private final List<Cliente> clientes = new ArrayList<>();
    private Long proximoId = 1L;

    public void adicionar(String nome, String cpf, String email, String telefone, int idade) {
        validarCamposObrigatorios(nome, cpf, email, telefone);
        verificarCpfDuplicado(cpf);
        Cliente cliente = new Cliente(proximoId++, nome, cpf, email, telefone, idade);
        clientes.add(cliente);
        System.out.println("✅ Cliente adicionado com sucesso.");
    }

    public List<Cliente> listar() {
        return clientes;
    }

    public Cliente buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ClienteException("Cliente não encontrado."));
    }

    public void atualizar(Long id, String nome, String cpf, String email, String telefone) {
        validarCamposObrigatorios(nome, cpf, email, telefone);
        Cliente cliente = buscarPorId(id);
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        System.out.println("✅ Cliente atualizado com sucesso.");
    }
    public void excluir(Long id) {
        clientes.remove(buscarPorId(id));
        System.out.println("✅ Cliente excluído com sucesso.");
    }

    private void validarCamposObrigatorios(String n, String c, String e, String t) {
        if (n == null || n.isBlank() || c == null || c.isBlank() || e == null || e.isBlank() || t == null || t.isBlank()) {
            throw new ClienteException("Todos os campos são obrigatórios.");
        }
    }
    private void verificarCpfDuplicado(String cpf) {
        if (clientes.stream().anyMatch(c -> c.getCpf().equals(cpf))) {
            throw new ClienteException("Já existe um cliente com este CPF.");
        }
    }
}