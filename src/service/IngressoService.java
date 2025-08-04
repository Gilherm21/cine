package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import exception.IngressoException;
import model.Filme;
import model.Ingresso;
import model.Sala;
import model.Sessao;

public class IngressoService {

    private static final double PRECO_BASE_INTEIRA = 30.00;

    private final List<Ingresso> ingressos = new ArrayList<>();
    private Long proximoIdIngresso = 1L;
    private final List<Sessao> sessoes = new ArrayList<>();
    private Long proximaSessaoId = 1L;

    private final FilmeService filmeService;
    private final SalaService salaService;

    public IngressoService(FilmeService filmeService, SalaService salaService) {
        this.filmeService = filmeService;
        this.salaService = salaService;
    }

    private double calcularValorIngresso(Sessao sessao, boolean meiaEntrada) {
        double valor = PRECO_BASE_INTEIRA;
        valor = valor * sessao.getSala().getFatorPreco();

        if (meiaEntrada) {
            valor = valor / 2;
        }
        return valor;
    }
    
    public Ingresso comprarIngresso(Sessao sessao, String assento, boolean meiaEntrada, String documento) {
        Sala sala = sessao.getSala();

        if (!sala.verificarDisponibilidade(assento)) {
            throw new IngressoException("Assento " + assento + " já está ocupado para esta sessão.");
        }

        sala.ocuparAssento(assento);
        double valorFinal = calcularValorIngresso(sessao, meiaEntrada);

        Ingresso ingresso = new Ingresso();
        ingresso.setId(proximoIdIngresso++);
        ingresso.setSessao(sessao);
        ingresso.setAssento(assento);
        ingresso.setMeiaEntrada(meiaEntrada);
        ingresso.setDocumentoMeiaEntrada(documento);
        ingresso.setValor(valorFinal);
        ingresso.setQrCode(UUID.randomUUID().toString().substring(0, 8));

        ingressos.add(ingresso);
        return ingresso;
    }

    public void cancelarIngresso(Long ingressoId) {
        Ingresso ingresso = ingressos.stream()
                .filter(i -> i.getId().equals(ingressoId))
                .findFirst()
                .orElseThrow(() -> new IngressoException("Ingresso não encontrado."));

        if (ingresso.isCancelado()) {
            throw new IngressoException("Ingresso já está cancelado.");
        }

        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(ingresso.getSessao().getHorario().minusHours(2))) {
            throw new IngressoException("Cancelamento só é permitido até 2 horas antes da sessão.");
        }

        ingresso.getSessao().getSala().liberarAssento(ingresso.getAssento());
        ingresso.setCancelado(true);
        System.out.println("✅ Ingresso cancelado. Reembolso de R$ " + String.format("%.2f", ingresso.getValor()) + " processado.");
    }
    
    public Ingresso validarQrCode(String qrCode) {
        return ingressos.stream()
            .filter(i -> i.getQrCode().equals(qrCode) && !i.isCancelado())
            .findFirst()
            .orElse(null);
    }

    public Sessao cadastrarSessao(Long filmeId, Long salaId, LocalDateTime horario) {
        Filme filme = filmeService.buscarPorId(filmeId);
        Sala sala = salaService.buscarPorId(salaId);

        Sessao sessao = new Sessao(proximaSessaoId++, filme, sala, horario);
        sessoes.add(sessao);
        return sessao;
    }

    public List<Sessao> listarSessoes() {
        return sessoes;
    }

    public Sessao buscarSessaoPorId(Long id) {
        return sessoes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada."));
    }
    
    public List<Sessao> buscarSessoesPorFilmeId(Long filmeId) {
        return sessoes.stream()
                .filter(s -> s.getFilme().getId().equals(filmeId))
                .collect(Collectors.toList());
    }
}