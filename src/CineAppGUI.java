import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import model.Administrador;
import model.Cliente;
import model.Filme;
import model.Funcionario;
import model.Ingresso;
import model.Produto;
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

public class CineAppGUI {

    private final ClienteService clienteService;
    private final FilmeService filmeService;
    private final SalaService salaService;
    private final IngressoService ingressoService;
    private final ProdutoService produtoService;
    private final VendaService vendaService;
    private final AuthService authService;
    private final FuncionarioService funcionarioService;
    private final RelatorioService relatorioService;

    private JFrame frame;
    private Usuario usuarioLogado;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                CineAppGUI window = new CineAppGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CineAppGUI() {
        this.clienteService = new ClienteService();
        this.filmeService = new FilmeService();
        this.salaService = new SalaService();
        this.ingressoService = new IngressoService(filmeService, salaService);
        this.produtoService = new ProdutoService();
        this.vendaService = new VendaService(ingressoService, produtoService, clienteService);
        this.authService = new AuthService(clienteService);
        this.funcionarioService = new FuncionarioService();
        this.relatorioService = new RelatorioService(vendaService, ingressoService);
        
        popularDadosIniciais();
        initialize();
    }
    
    private void popularDadosIniciais() {
        filmeService.adicionarInicial("Divertidamente 2", "Novas emoções chegam à mente da adolescente Riley.", 96, "Animação", 0);
        filmeService.adicionarInicial("O Auto da Compadecida 2", "As aventuras de João Grilo e Chicó continuam.", 120, "Comédia", 12);
        filmeService.adicionarInicial("Bad Boys: Até o Fim", "Os detetives Mike Lowrey e Marcus Burnett investigam a corrupção.", 115, "Ação", 16);
        filmeService.adicionarInicial("Planeta dos Macacos: O Reinado", "Muitas gerações após o reinado de César.", 145, "Ficção Científica", 14);
        
        ingressoService.cadastrarSessao(1L, 1L, LocalDateTime.now().plusHours(2));
        ingressoService.cadastrarSessao(1L, 2L, LocalDateTime.now().plusHours(4));
        ingressoService.cadastrarSessao(2L, 3L, LocalDateTime.now().plusHours(3));
        ingressoService.cadastrarSessao(3L, 4L, LocalDateTime.now().plusHours(1));
        ingressoService.cadastrarSessao(4L, 1L, LocalDateTime.now().plusHours(5));
    }

    private void initialize() {
        frame = new JFrame("Cine-System");
        frame.setBounds(100, 100, 500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mostrarPainelLogin();
    }

    private void mostrarPainel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void mostrarPainelLogin() {
        frame.setTitle("Cine-System - Login");
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblLogin = new JLabel("Login (Email ou 'admin'):");
        JTextField loginField = new JTextField();
        JLabel lblSenha = new JLabel("Senha (CPF ou 'admin'):");
        JPasswordField senhaField = new JPasswordField();
        JButton btnLogin = new JButton("Login");
        JButton btnCriarConta = new JButton("Criar Conta");

        btnLogin.addActionListener(e -> tentarLogin(loginField.getText(), new String(senhaField.getPassword())));
        btnCriarConta.addActionListener(e -> mostrarDialogoCriarConta());

        panel.add(lblLogin);
        panel.add(loginField);
        panel.add(lblSenha);
        panel.add(senhaField);
        panel.add(btnLogin);
        panel.add(btnCriarConta);

        mostrarPainel(panel);
    }

    private void tentarLogin(String login, String senha) {
        usuarioLogado = authService.login(login, senha);
        if (usuarioLogado != null) {
            JOptionPane.showMessageDialog(frame, "Login bem-sucedido!");
            if (usuarioLogado instanceof Administrador) {
                mostrarPainelAdmin();
            } else {
                mostrarPainelCartaz();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Login ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoCriarConta() {
        JTextField nomeField = new JTextField();
        JTextField cpfField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField idadeField = new JTextField();
        
        Object[] message = {
            "Nome:", nomeField,
            "CPF (será a senha):", cpfField,
            "Email (será o login):", emailField,
            "Telefone:", telefoneField,
            "Idade:", idadeField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Criar Conta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText();
                String cpf = cpfField.getText();
                String email = emailField.getText();
                String telefone = telefoneField.getText();
                int idade = Integer.parseInt(idadeField.getText());
                clienteService.adicionar(nome, cpf, email, telefone, idade);
                JOptionPane.showMessageDialog(frame, "Conta criada com sucesso! Faça o login.");
                mostrarPainelLogin();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao criar conta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarPainelAdmin() {
        frame.setTitle("Painel do Administrador");
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnGerenciarFilmes = new JButton("Gerenciar Filmes");
        btnGerenciarFilmes.addActionListener(e -> mostrarDialogoGerenciarFilmes());

        JButton btnGerenciarSessoes = new JButton("Gerenciar Sessões");
        btnGerenciarSessoes.addActionListener(e -> mostrarDialogoGerenciarSessoes());

        JButton btnGerarRelatorios = new JButton("Gerar Relatórios");
        btnGerarRelatorios.addActionListener(e -> mostrarDialogoGerarRelatorios());

        JButton btnValidarIngresso = new JButton("Validar Ingresso");
        btnValidarIngresso.addActionListener(e -> mostrarDialogoValidarIngresso());
        
        JButton btnGerenciarFunc = new JButton("Gerenciar Funcionários");
        btnGerenciarFunc.addActionListener(e -> mostrarDialogoGerenciarFuncionarios());

        JButton btnTestarInterface = new JButton("Ver Detalhes (Interface)");
        btnTestarInterface.addActionListener(e -> mostrarDialogoDetalhesInterface());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        
        panel.add(btnGerenciarFilmes);
        panel.add(btnGerenciarSessoes);
        panel.add(btnGerenciarFunc);
        panel.add(btnGerarRelatorios);
        panel.add(btnValidarIngresso);
        panel.add(btnTestarInterface);
        panel.add(new JLabel());
        panel.add(btnLogout);
        
        mostrarPainel(panel);
    }

    private void mostrarDialogoDetalhesInterface() {
        try {
            Filme filme = filmeService.buscarPorId(1L);
            Funcionario funcionario = funcionarioService.buscarPorId(1L);

            String detalhesFilme = filme.getDetalhesFormatados();
            String detalhesFuncionario = funcionario.getDetalhesFormatados();

            String mensagemFinal = detalhesFilme + "\n\n" + detalhesFuncionario;

            JTextArea textArea = new JTextArea(mensagemFinal);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(frame, scrollPane, "Detalhes (via Interface IImprimivel)", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
             JOptionPane.showMessageDialog(frame, "Erro ao buscar dados para exibição: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarPainelCartaz() {
        frame.setTitle("Filmes em Cartaz");
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Bem-vindo, " + ((Cliente)usuarioLogado).getNome() + "!"));
        panel.add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        List<Filme> filmes = filmeService.listar();
        for (Filme filme : filmes) {
            JButton btnFilme = new JButton(filme.getTitulo());
            btnFilme.addActionListener(e -> mostrarDialogoDetalhesFilme(filme));
            centerPanel.add(btnFilme);
        }
        panel.add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        panel.add(btnLogout, BorderLayout.SOUTH);
        
        mostrarPainel(panel);
    }
    
    private void mostrarDialogoDetalhesFilme(Filme filme) {
        JDialog dialog = new JDialog(frame, "Detalhes de " + filme.getTitulo(), true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(frame);

        JTextArea infoArea = new JTextArea(filme.getDetalhesFormatados());
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        dialog.add(new JScrollPane(infoArea), BorderLayout.NORTH);

        DefaultListModel<Sessao> listModel = new DefaultListModel<>();
        List<Sessao> sessoes = ingressoService.buscarSessoesPorFilmeId(filme.getId());
        sessoes.forEach(listModel::addElement);
        JList<Sessao> sessoesList = new JList<>(listModel);
        sessoesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dialog.add(new JScrollPane(sessoesList), BorderLayout.CENTER);

        JButton btnComprar = new JButton("Comprar Ingresso para Sessão Selecionada");
        btnComprar.addActionListener(e -> {
            Sessao sessaoSelecionada = sessoesList.getSelectedValue();
            if (sessaoSelecionada != null) {
                dialog.dispose();
                mostrarDialogoCompra(sessaoSelecionada);
            } else {
                JOptionPane.showMessageDialog(dialog, "Por favor, selecione uma sessão.");
            }
        });
        dialog.add(btnComprar, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoCompra(Sessao sessao) {
        try {
            Venda novaVenda = vendaService.criarVenda(((Cliente)usuarioLogado).getId());
            
            String qtdStr = JOptionPane.showInputDialog(frame, "Quantos ingressos deseja comprar?");
            int quantidadeIngressos = Integer.parseInt(qtdStr);

            for (int i = 1; i <= quantidadeIngressos; i++) {
                String assento;
                while (true) {
                    JTextArea assentosArea = new JTextArea(10, 30);
                    assentosArea.setText(sessao.getSala().getMapaDeAssentos());
                    assentosArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(assentosArea);
                    
                    assento = (String) JOptionPane.showInputDialog(frame, new Object[]{"MAPA DE ASSENTOS - INGRESSO " + i, scrollPane}, "Escolha um assento vago (Ex: A1):", JOptionPane.PLAIN_MESSAGE, null, null, "");
                    
                    if (assento == null) return;
                    
                    if (sessao.getSala().verificarDisponibilidade(assento.toUpperCase())) {
                        break;
                    } else {
                        JOptionPane.showMessageDialog(frame, "❌ Assento ocupado ou inválido. Por favor, escolha outro.");
                    }
                }

                int meiaOpcao = JOptionPane.showConfirmDialog(frame, "O ingresso " + i + " é meia-entrada?", "Tipo de Ingresso", JOptionPane.YES_NO_OPTION);
                boolean meia = (meiaOpcao == JOptionPane.YES_OPTION);
                String doc = null;
                if (meia) {
                    doc = JOptionPane.showInputDialog(frame, "Digite o documento para meia-entrada:");
                }

                vendaService.adicionarIngressoNaVenda(novaVenda, sessao.getId(), assento.toUpperCase(), meia, doc);
            }
            JOptionPane.showMessageDialog(frame, "Ingresso(s) adicionado(s) com sucesso!");

            int produtosOpcao = JOptionPane.showConfirmDialog(frame, "Deseja comprar produtos (Bomboniere)?", "Conveniência", JOptionPane.YES_NO_OPTION);
            if (produtosOpcao == JOptionPane.YES_OPTION) {
                while(true) {
                    boolean adicionou = mostrarDialogoProdutos(novaVenda);
                    if (!adicionou) break;

                    int continuarOpcao = JOptionPane.showConfirmDialog(frame, "Deseja adicionar mais produtos?", "Conveniência", JOptionPane.YES_NO_OPTION);
                    if (continuarOpcao == JOptionPane.NO_OPTION) break;
                }
            }

            vendaService.finalizarVenda(novaVenda);
            JOptionPane.showMessageDialog(frame, "Compra finalizada com sucesso!\n" + novaVenda);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Erro na compra: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean mostrarDialogoProdutos(Venda venda) {
        List<Produto> produtos = produtoService.listarProdutos();
        String[] nomesProdutos = produtos.stream().map(Produto::toString).toArray(String[]::new);

        String produtoSelecionadoStr = (String) JOptionPane.showInputDialog(frame, "Escolha um produto:",
                "Bomboniere", JOptionPane.QUESTION_MESSAGE, null, nomesProdutos, nomesProdutos[0]);

        if (produtoSelecionadoStr != null) {
            try {
                Long produtoId = Long.parseLong(produtoSelecionadoStr.split("ID=")[1].split(",")[0]);
                String qtdStr = JOptionPane.showInputDialog(frame, "Digite a quantidade:");
                int quantidade = Integer.parseInt(qtdStr);
                
                vendaService.adicionarProdutoNaVenda(venda, produtoId, quantidade);
                JOptionPane.showMessageDialog(frame, "Produto(s) adicionado(s)!");
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Erro ao adicionar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }
    
    private void mostrarDialogoGerenciarFilmes() {
        JDialog dialog = new JDialog(frame, "Gerenciar Filmes", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(frame);

        DefaultListModel<Filme> listModel = new DefaultListModel<>();
        filmeService.listar().forEach(listModel::addElement);
        JList<Filme> filmeList = new JList<>(listModel);
        dialog.add(new JScrollPane(filmeList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnExcluir = new JButton("Excluir");
        
        btnAdicionar.addActionListener(e -> {
            String titulo = JOptionPane.showInputDialog(dialog, "Título do novo filme:");
            if (titulo != null && !titulo.isBlank()) {
                filmeService.adicionar(titulo, "Sinopse a definir", 120, "Gênero a definir", 0);
                listModel.clear();
                filmeService.listar().forEach(listModel::addElement);
            }
        });
        
        btnExcluir.addActionListener(e -> {
            Filme filmeSelecionado = filmeList.getSelectedValue();
            if (filmeSelecionado != null) {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Tem a certeza que quer excluir o filme '" + filmeSelecionado.getTitulo() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    filmeService.excluir(filmeSelecionado.getId());
                    listModel.removeElement(filmeSelecionado);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Selecione um filme para excluir.");
            }
        });

        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnExcluir);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void mostrarDialogoGerenciarSessoes() {
        String filmeIdStr = JOptionPane.showInputDialog(frame, "Digite o ID do Filme para adicionar uma sessão:");
        String salaIdStr = JOptionPane.showInputDialog(frame, "Digite o ID da Sala:");
        String horarioStr = JOptionPane.showInputDialog(frame, "Digite o horário (yyyy-MM-dd HH:mm):");
        
        try {
            Long filmeId = Long.parseLong(filmeIdStr);
            Long salaId = Long.parseLong(salaIdStr);
            LocalDateTime horario = LocalDateTime.parse(horarioStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            ingressoService.cadastrarSessao(filmeId, salaId, horario);
            JOptionPane.showMessageDialog(frame, "Sessão cadastrada com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Erro ao cadastrar sessão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoGerenciarFuncionarios() {
        JDialog dialog = new JDialog(frame, "Gerenciar Funcionários", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(frame);

        DefaultListModel<Funcionario> listModel = new DefaultListModel<>();
        funcionarioService.listar().forEach(listModel::addElement);
        JList<Funcionario> funcList = new JList<>(listModel);
        dialog.add(new JScrollPane(funcList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnDemitir = new JButton("Demitir");
        
        btnAdicionar.addActionListener(e -> {
            String nome = JOptionPane.showInputDialog(dialog, "Nome do funcionário:");
            String funcao = JOptionPane.showInputDialog(dialog, "Função do funcionário:");
            if (nome != null && !nome.isBlank() && funcao != null && !funcao.isBlank()) {
                funcionarioService.adicionar(nome, funcao);
                listModel.clear();
                funcionarioService.listar().forEach(listModel::addElement);
            }
        });
        
        btnDemitir.addActionListener(e -> {
            Funcionario funcSelecionado = funcList.getSelectedValue();
            if (funcSelecionado != null) {
                funcionarioService.demitir(funcSelecionado.getId());
                listModel.removeElement(funcSelecionado);
            } else {
                JOptionPane.showMessageDialog(dialog, "Selecione um funcionário para demitir.");
            }
        });

        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnDemitir);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoGerarRelatorios() {
        String[] opcoes = {"Vendas por Filme", "Ocupação das Salas", "Clientes Frequentes", "Vendas de Produtos", "Receita Total"};
        String escolha = (String) JOptionPane.showInputDialog(frame, "Escolha um relatório para gerar:",
                "Gerar Relatórios", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == null) return;
        
        System.out.println("\n--- O RELATÓRIO A SEGUIR SERÁ EXIBIDO APENAS NO CONSOLE ---");
        
        switch (escolha) {
            case "Vendas por Filme": relatorioService.gerarRelatorioVendasPorFilme(); break;
            case "Ocupação das Salas": relatorioService.gerarRelatorioOcupacaoDasSalas(); break;
            case "Clientes Frequentes": relatorioService.gerarRelatorioClientesFrequentes(); break;
            case "Vendas de Produtos": relatorioService.gerarRelatorioVendasDeProdutos(); break;
            case "Receita Total": relatorioService.gerarRelatorioReceitaTotal(); break;
        }
        
        JOptionPane.showMessageDialog(frame, "Relatório gerado no console do seu editor/terminal.", "Relatório Gerado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarDialogoValidarIngresso() {
        String qrCode = JOptionPane.showInputDialog(frame, "Digite o QR Code do ingresso:");
        if (qrCode != null && !qrCode.isBlank()) {
            Ingresso ingresso = ingressoService.validarQrCode(qrCode);
            if (ingresso != null) {
                JOptionPane.showMessageDialog(frame, "INGRESSO VÁLIDO!\nFilme: " + ingresso.getSessao().getFilme().getTitulo() + "\nAssento: " + ingresso.getAssento(), "Acesso Liberado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "INGRESSO INVÁLIDO OU JÁ CANCELADO!", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        usuarioLogado = null;
        mostrarPainelLogin();
    }
}