package service;

import java.util.List;

public interface Gerenciavel<T> {
    
    void adicionar(T item);
    
    List<T> listar();
    
    T buscarPorId(Long id);
    
    void excluir(Long id);
}