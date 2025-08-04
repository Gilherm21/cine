package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Administrador;
import model.Usuario;

public class AuthService {

    private final List<Administrador> administradores = new ArrayList<>();
    private final ClienteService clienteService;

    public AuthService(ClienteService clienteService) {
        this.clienteService = clienteService;
        this.administradores.add(new Administrador("admin", "admin"));
    }

    public Usuario login(String login, String senha) {
        Optional<Administrador> admin = administradores.stream()
                .filter(a -> a.getLogin().equals(login) && a.realizarLogin(login, senha))
                .findFirst();

        if (admin.isPresent()) {
            return admin.get();
        }

        Optional<Usuario> cliente = clienteService.listar().stream()
                .filter(c -> c.getLogin().equals(login) && c.realizarLogin(login, senha))
                .map(c -> (Usuario) c)
                .findFirst();
        
        return cliente.orElse(null);
    }
}