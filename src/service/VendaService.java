package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import exception.IngressoException;
import model.Cliente;
import model.Ingresso;
import model.Produto;
import model.Sessao;
import model.Venda;

public class VendaService {
    private final List<Venda> vendas = new ArrayList<>();
    private Long proximoId = 1L;

    private final IngressoService ingressoService;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    public VendaService(IngressoService ingressoService, ProdutoService produtoService, ClienteService clienteService) {
        this.ingressoService = ingressoService;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
    }

    public Venda criarVenda(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        return new Venda(proximoId++, cliente);
    }

    public void adicionarIngressoNaVenda(Venda venda, Long sessaoId, String assento, boolean meia, String doc) {
        Sessao sessao = ingressoService.buscarSessaoPorId(sessaoId);
        Cliente cliente = venda.getCliente();

        if (cliente.getIdade() < sessao.getFilme().getClassificacaoIndicativa()) {
            throw new IngressoException("Venda bloqueada. Cliente não tem idade para assistir a este filme.");
        }

        if (LocalDateTime.now().isAfter(sessao.getHorario())) {
            throw new IngressoException("Venda bloqueada. Esta sessão já foi iniciada ou finalizada.");
        }

        Ingresso ingresso = ingressoService.comprarIngresso(sessao, assento, meia, doc);
        venda.adicionarIngresso(ingresso);
    }

    public void adicionarProdutoNaVenda(Venda venda, Long produtoId, int quantidade) {
        Produto produto = produtoService.buscarPorId(produtoId);
        produtoService.verificarEstoque(produto, quantidade);
        venda.adicionarProduto(produto, quantidade);
    }

    public void finalizarVenda(Venda venda) {
        this.vendas.add(venda);
        System.out.println("✅ Pagamento de R$ " + String.format("%.2f", venda.getValorTotal()) + " aprovado!");
        System.out.println("✅ Venda finalizada com sucesso: " + venda);
    }

    public List<Venda> listarVendas() {
        return vendas;
    }
}