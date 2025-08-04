import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import model.Administrador;
import model.Cliente;
import model.Filme;
import model.Funcionario;
import model.IImprimivel;
import model.Ingresso;
import model.Sessao;
import model.Usuario;
import model.Venda;
import service.AuthService;
import service.ClienteService;
import service.FilmeService;
import service.FuncionarioService;
import service.IngressoService;
import service.ProdutoService;
import service.RelatorioService;
import service.SalaService;
import service.VendaService;

public class Main {

    private final ClienteService clienteService;
    private final FilmeService filmeService;
    private final SalaService salaService;
    private final IngressoService ingressoService;
    private final ProdutoService produtoService;
    private final VendaService vendaService;
    private final RelatorioService relatorioService;
    private final AuthService authService;
    private final FuncionarioService funcionarioService;
    private final Scanner scanner;
    private Usuario usuarioLogado;

    public static void main(String[] args) {
        Main app = new Main();
        app.executar();
    }

    public Main() {
        this.clienteService = new ClienteService();
        this.filmeService = new FilmeService();
        this.salaService = new SalaService();
        this.ingressoService = new IngressoService(filmeService, salaService);
        this.produtoService = new ProdutoService();
        this.vendaService = new VendaService(ingressoService, produtoService, clienteService);
        this.relatorioService = new RelatorioService(vendaService, ingressoService);
        this.authService = new AuthService(clienteService);
        this.funcionarioService = new FuncionarioService();
        this.scanner = new Scanner(System.in);
        
        popularDadosIniciais();
    }

    private void popularDadosIniciais() {
        filmeService.adicionarInicial("Divertidamente 2", "Novas emoções chegam à mente da adolescente Riley.", 96, "Animação", 0);
        filmeService.adicionarInicial("O Auto da Compadecida 2", "As aventuras de João Grilo e Chicó continuam.", 120, "Comédia", 12);
        filmeService.adicionarInicial("Bad Boys: Até o Fim", "Os detetives Mike Lowrey e Marcus Burnett investigam a corrupção.", 115, "Ação", 16);
        filmeService.adicionarInicial("Planeta dos Macacos: O Reinado", "Muitas gerações após o reinado de César, os macacos são a espécie dominante.", 145, "Ficção Científica", 14);
        
        ingressoService.cadastrarSessao(1L, 1L, LocalDateTime.now().plusHours(2));
        ingressoService.cadastrarSessao(1L, 2L, LocalDateTime.now().plusHours(4));
        ingressoService.cadastrarSessao(2L, 3L, LocalDateTime.now().plusHours(3));
        ingressoService.cadastrarSessao(3L, 4L, LocalDateTime.now().plusHours(1));
        ingressoService.cadastrarSessao(4L, 1L, LocalDateTime.now().plusHours(5));
    }

    public void executar() {
        System.out.println("Bem-vindo ao CINE-SYSTEM!");

        while (true) {
            if (usuarioLogado == null) {
                telaInicialLogin();
            } else {
                System.out.println("\n✅ Login realizado com sucesso! Bem-vindo, " + usuarioLogado.getLogin());
                if (usuarioLogado instanceof Administrador) {
                    menuAdministrador();
                } else {
                    menuCliente();
                }
                if (usuarioLogado == null) {
                    System.out.println("✅ Logout realizado com sucesso.");
                }
            }
        }
    }
    
    private void telaInicialLogin() {
        System.out.println("\n--- TELA INICIAL ---");
        System.out.println("1 - Fazer Login");
        System.out.println("2 - Criar Conta de Cliente");
        System.out.println("0 - Sair do Sistema");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                fazerLogin();
                break;
            case 2:
                criarContaCliente();
                break;
            case 0:
                System.out.println("Saindo...");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void fazerLogin() {
        System.out.println("\n--- TELA DE LOGIN ---");
        System.out.print("Login (use 'admin' ou email do cliente): ");
        String login = scanner.nextLine();
        System.out.print("Senha (use 'admin' ou CPF do cliente): ");
        String senha = scanner.nextLine();

        usuarioLogado = authService.login(login, senha);

        if (usuarioLogado == null) {
            System.out.println("❌ Login ou senha inválidos. Tente novamente.");
        }
    }

    private void criarContaCliente() {
        try {
            System.out.println("\n--- CRIAR CONTA DE CLIENTE ---");
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            System.out.print("CPF (será a senha): ");
            String cpf = scanner.nextLine();
            System.out.print("Email (será o login): ");
            String email = scanner.nextLine();
            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();
            System.out.print("Idade: ");
            int idade = scanner.nextInt(); scanner.nextLine();
            clienteService.adicionar(nome, cpf, email, telefone, idade);
            System.out.println("✅ Conta criada com sucesso! Por favor, faça o login.");
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar conta: " + e.getMessage());
        }
    }

    private void menuCliente() {
        int opcao = -1;
        do {
            System.out.println("\n--- MENU DO CLIENTE ---");
            System.out.println("1 - Ver Filmes em Cartaz");
            System.out.println("2 - Cancelar Ingresso");
            System.out.println("0 - Deslogar");
            System.out.print("Escolha uma opção: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        menuCartaz();
                        break;
                    case 2:
                        cancelarIngresso();
                        break;
                    case 0:
                        usuarioLogado = null;
                        break;
                    default:
                        System.out.println("❌ Opção inválida.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("❌ Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }
        } while (opcao != 0);
    }
    
    private void menuCartaz() {
        System.out.println("\n--- FILMES EM CARTAZ ---");
        filmeService.listar().forEach(filme -> {
            System.out.println("ID: " + filme.getId() + " - " + filme.getTitulo());
        });
        System.out.print("\nDigite o ID do filme para ver detalhes ou 'V' para voltar: ");
        String escolha = scanner.nextLine().toUpperCase();

        if (escolha.equals("V")) {
            return;
        }

        try {
            Long filmeId = Long.parseLong(escolha);
            detalhesFilmeECompra(filmeId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Opção inválida.");
        }
    }

    private void detalhesFilmeECompra(Long filmeId) {
        try {
            Filme filme = filmeService.buscarPorId(filmeId);
            System.out.println("\n--- DETALHES DO FILME ---");
            System.out.println("Título: " + filme.getTitulo());
            System.out.println("Sinopse: " + filme.getSinopse());
            System.out.println("Classificação: " + filme.getClassificacaoIndicativa() + " anos");

            List<Sessao> sessoes = ingressoService.buscarSessoesPorFilmeId(filmeId);
            if (sessoes.isEmpty()) {
                System.out.println("Não há sessões disponíveis para este filme no momento.");
                return;
            }
            
            System.out.println("\n--- Horários Disponíveis ---");
            sessoes.forEach(sessao -> {
                System.out.println("Sessão ID: " + sessao.getId() + " | Horário: " + sessao.getHorario().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) + " | Sala: " + sessao.getSala().getNome());
            });

            System.out.print("\nDeseja comprar ingresso para uma dessas sessões? (S/N): ");
            if (scanner.nextLine().equalsIgnoreCase("S")) {
                realizarVenda();
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar detalhes: " + e.getMessage());
        }
    }

    private void realizarVenda() {
        try {
            Venda novaVenda = vendaService.criarVenda(((Cliente) usuarioLogado).getId());
            System.out.println("Iniciando nova venda para o cliente: " + ((Cliente) usuarioLogado).getNome());

            System.out.print("\nDigite o ID da sessão desejada: ");
            Long sessaoId = scanner.nextLong(); scanner.nextLine();
            Sessao sessaoEscolhida = ingressoService.buscarSessaoPorId(sessaoId);

            System.out.print("Quantos ingressos deseja comprar? ");
            int quantidadeIngressos = scanner.nextInt(); scanner.nextLine();

            for (int i = 1; i <= quantidadeIngressos; i++) {
                System.out.println("\n--- INGRESSO " + i + " de " + quantidadeIngressos + " ---");
                String assento;
                while (true) {
                    System.out.println("\n--- MAPA DE ASSENTOS ---");
                    System.out.println(sessaoEscolhida.getSala().getMapaDeAssentos());
                    System.out.print("Escolha um assento vago (Ex: A1): ");
                    assento = scanner.nextLine().toUpperCase();

                    if (sessaoEscolhida.getSala().verificarDisponibilidade(assento)) {
                        break;
                    } else {
                        System.out.println("❌ Assento ocupado ou inválido. Por favor, escolha outro.");
                    }
                }

                System.out.print("Meia entrada? (s/n): ");
                boolean meia = scanner.nextLine().equalsIgnoreCase("s");
                String doc = null;
                if(meia) {
                    System.out.print("Documento de meia-entrada: ");
                    doc = scanner.nextLine();
                }

                vendaService.adicionarIngressoNaVenda(novaVenda, sessaoId, assento, meia, doc);
                System.out.println("✅ Ingresso adicionado!");
            }

            System.out.print("\nDeseja comprar produtos de conveniência? (S/N): ");
            if (scanner.nextLine().equalsIgnoreCase("S")) {
                String continuar;
                do {
                    System.out.println("\n--- Produtos Disponíveis ---");
                    produtoService.listarProdutos().forEach(System.out::println);
                    System.out.print("ID do produto: ");
                    Long produtoId = scanner.nextLong(); scanner.nextLine();
                    System.out.print("Quantidade: ");
                    int quantidade = scanner.nextInt(); scanner.nextLine();

                    vendaService.adicionarProdutoNaVenda(novaVenda, produtoId, quantidade);
                    System.out.println("✅ Produto(s) adicionado(s)!");

                    System.out.print("\nDeseja adicionar mais algum produto? (s/n): ");
                    continuar = scanner.nextLine();
                } while (continuar.equalsIgnoreCase("s"));
            }

            System.out.println("\n--- PAGAMENTO ---");
            System.out.println("Valor total da compra: R$ " + String.format("%.2f", novaVenda.getValorTotal()));
            vendaService.finalizarVenda(novaVenda);

        } catch (Exception e) {
            System.out.println("❌ Erro durante a venda: " + e.getMessage());
        }
    }

    private void menuAdministrador() {
        int opcao = -1;
        do {
            System.out.println("\n--- PAINEL DO ADMINISTRADOR ---");
            System.out.println("1 - Gerenciar Clientes");
            System.out.println("2 - Gerenciar Filmes");
            System.out.println("3 - Gerenciar Sessões");
            System.out.println("4 - Gerenciar Funcionários");
            System.out.println("5 - Gerar Relatórios");
            System.out.println("6 - Validar QR Code de Ingresso");
            System.out.println("7 - Testar Impressão (Interface)");
            System.out.println("0 - Deslogar");
            System.out.print("Escolha uma opção: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1: gerenciarClientes(); break;
                    case 2: gerenciarFilmes(); break;
                    case 3: gerenciarSessoes(); break;
                    case 4: gerenciarFuncionarios(); break;
                    case 5: gerarRelatorios(); break;
                    case 6: validarIngresso(); break;
                    case 7: testarImpressao(); break;
                    case 0:
                        usuarioLogado = null;
                        break;
                    default: System.out.println("❌ Opção inválida."); break;
                }
            } catch (Exception e) {
                System.out.println("❌ Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }
        } while (opcao != 0);
    }
    
    private void gerenciarFuncionarios() {
        System.out.println("\n-- Gerenciar Funcionários --");
        System.out.println("1 - Adicionar Funcionário");
        System.out.println("2 - Listar Funcionários");
        System.out.println("3 - Demitir Funcionário");
        System.out.println("4 - Eleger Funcionário do Mês");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1: {
                System.out.print("Nome do funcionário: ");
                String nome = scanner.nextLine();
                System.out.print("Função do funcionário: ");
                String funcao = scanner.nextLine();
                funcionarioService.adicionar(nome, funcao);
                break;
            }
            case 2: {
                System.out.println("\n--- Lista de Funcionários ---");
                funcionarioService.listar().forEach(System.out::println);
                break;
            }
            case 3: {
                System.out.print("ID do funcionário para demitir: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                funcionarioService.demitir(id);
                break;
            }
            case 4: {
                System.out.println("\n--- Lista de Funcionários ---");
                funcionarioService.listar().forEach(System.out::println);
                System.out.print("Digite o ID do funcionário a ser eleito: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                funcionarioService.elegerFuncionarioDoMes(id);
                break;
            }
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void cancelarIngresso() {
        try {
            System.out.println("\n-- Cancelar Ingresso --");
            System.out.print("Digite o ID do ingresso para cancelar: ");
            Long ingressoId = scanner.nextLong();
            scanner.nextLine();
            ingressoService.cancelarIngresso(ingressoId);
        } catch (Exception e) {
            System.out.println("❌ Erro ao cancelar ingresso: " + e.getMessage());
        }
    }
    
    private void validarIngresso() {
        System.out.println("\n-- Validação de Ingresso --");
        System.out.print("Digite o QR Code do ingresso: ");
        String qrCode = scanner.nextLine();

        Ingresso ingresso = ingressoService.validarQrCode(qrCode);

        if (ingresso != null) {
            System.out.println("✅ INGRESSO VÁLIDO!");
            System.out.println("Filme: " + ingresso.getSessao().getFilme().getTitulo());
            System.out.println("Assento: " + ingresso.getAssento());
            System.out.println("Acesso Liberado.");
        } else {
            System.out.println("❌ INGRESSO INVÁLIDO OU JÁ UTILIZADO.");
        }
    }

    private void gerenciarClientes() {
        System.out.println("\n-- Gerenciar Clientes --");
        System.out.println("1 - Adicionar Cliente");
        System.out.println("2 - Listar Clientes");
        System.out.println("3 - Atualizar Cliente");
        System.out.println("4 - Excluir Cliente");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1: {
                criarContaCliente();
                break;
            }
            case 2: {
                System.out.println("\n--- Clientes Cadastrados ---");
                clienteService.listar().forEach(System.out::println);
                break;
            }
            case 3: {
                System.out.print("ID do cliente para atualizar: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                System.out.print("Novo nome: ");
                String nome = scanner.nextLine();
                System.out.print("Novo CPF: ");
                String cpf = scanner.nextLine();
                System.out.print("Novo Email: ");
                String email = scanner.nextLine();
                System.out.print("Novo Telefone: ");
                String telefone = scanner.nextLine();
                clienteService.atualizar(id, nome, cpf, email, telefone);
                break;
            }
            case 4: {
                System.out.print("ID do cliente para excluir: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                clienteService.excluir(id);
                break;
            }
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void gerenciarFilmes() {
        System.out.println("\n-- Gerenciar Filmes --");
        System.out.println("1 - Adicionar Filme");
        System.out.println("2 - Listar Filmes");
        System.out.println("3 - Atualizar Filme");
        System.out.println("4 - Excluir Filme");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1: {
                System.out.print("Título: ");
                String titulo = scanner.nextLine();
                System.out.print("Sinopse: ");
                String sinopse = scanner.nextLine();
                System.out.print("Duração (minutos): ");
                int duracao = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Gênero: ");
                String genero = scanner.nextLine();
                System.out.print("Classificação Indicativa (idade): ");
                int classificacao = scanner.nextInt();
                scanner.nextLine();
                filmeService.adicionar(titulo, sinopse, duracao, genero, classificacao);
                break;
            }
            case 2: {
                System.out.println("\n--- Catálogo de Filmes ---");
                filmeService.listar().forEach(System.out::println);
                break;
            }
            case 3: {
                System.out.print("ID do filme para atualizar: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                System.out.print("Novo Título: ");
                String titulo = scanner.nextLine();
                System.out.print("Nova Sinopse: ");
                String sinopse = scanner.nextLine();
                System.out.print("Nova Duração (minutos): ");
                int duracao = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Novo Gênero: ");
                String genero = scanner.nextLine();
                System.out.print("Nova Classificação Indicativa (idade): ");
                int classificacao = scanner.nextInt();
                scanner.nextLine();
                filmeService.atualizar(id, titulo, sinopse, duracao, genero, classificacao);
                break;
            }
            case 4: {
                System.out.print("ID do filme para excluir: ");
                Long id = scanner.nextLong();
                scanner.nextLine();
                filmeService.excluir(id);
                break;
            }
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void gerenciarSessoes() {
        System.out.println("\n-- Gerenciar Sessões --");
        System.out.println("1 - Cadastrar Nova Sessão");
        System.out.println("2 - Listar Sessões Cadastradas");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch(opcao) {
            case 1: {
                try {
                    System.out.println("\n--- Salas Disponíveis ---");
                    salaService.listarSalas().forEach(System.out::println);
                    System.out.print("ID da sala para a sessão: ");
                    Long salaId = scanner.nextLong();
                    scanner.nextLine();

                    System.out.println("\n--- Filmes em Cartaz ---");
                    filmeService.listar().forEach(System.out::println);
                    System.out.print("ID do filme para a sessão: ");
                    Long filmeId = scanner.nextLong();
                    scanner.nextLine();

                    System.out.print("Data e hora da sessão (yyyy-MM-dd HH:mm): ");
                    String dataHoraStr = scanner.nextLine();
                    LocalDateTime horario = LocalDateTime.parse(dataHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    Sessao sessao = ingressoService.cadastrarSessao(filmeId, salaId, horario);
                    System.out.println("✅ Sessão cadastrada: " + sessao);
                } catch (Exception e) {
                    System.out.println("❌ Erro: " + e.getMessage());
                }
                break;
            }
            case 2: {
                System.out.println("\n--- Sessões Cadastradas ---");
                ingressoService.listarSessoes().forEach(System.out::println);
                break;
            }
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void gerarRelatorios() {
        System.out.println("\n-- Menu de Relatórios --");
        System.out.println("1 - Relatório de Vendas por Filme");
        System.out.println("2 - Relatório de Vendas de Produtos");
        System.out.println("3 - Relatório de Receita Total");
        System.out.println("4 - Relatório de Ocupação das Salas");
        System.out.println("5 - Relatório de Clientes Frequentes");
        System.out.print("Escolha um relatório para gerar: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch(opcao) {
            case 1:
                relatorioService.gerarRelatorioVendasPorFilme();
                break;
            case 2:
                relatorioService.gerarRelatorioVendasDeProdutos();
                break;
            case 3:
                relatorioService.gerarRelatorioReceitaTotal();
                break;
            case 4:
                relatorioService.gerarRelatorioOcupacaoDasSalas();
                break;
            case 5:
                relatorioService.gerarRelatorioClientesFrequentes();
                break;
            default:
                System.out.println("❌ Opção inválida.");
                break;
        }
    }

    private void imprimir(IImprimivel objeto) {
        objeto.imprimirDetalhes();
    }

    private void testarImpressao() {
        System.out.println("\n--- Testando Impressão com Interface IImprimivel ---");
        
        try {
            if (!filmeService.listar().isEmpty()) {
                Filme filme = filmeService.buscarPorId(1L);
                imprimir(filme);
            } else {
                 System.out.println("\nNenhum filme cadastrado para teste.");
            }

            if (!funcionarioService.listar().isEmpty()) {
                Funcionario funcionario = funcionarioService.buscarPorId(1L);
                imprimir(funcionario);
            } else {
                System.out.println("\nNenhum funcionário cadastrado para teste.");
            }
            
            if (!clienteService.listar().isEmpty()) {
                Cliente cliente = clienteService.listar().get(0);
                imprimir(cliente);
            } else {
                System.out.println("\nCadastre um cliente primeiro para testar a impressão de cliente.");
            }
        } catch(Exception e) {
            System.out.println("Ocorreu um erro ao testar a impressão: " + e.getMessage());
        }
    }
}