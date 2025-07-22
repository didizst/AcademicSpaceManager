package br.edu.espacos.view;

import br.edu.espacos.client.EspacosClient;
import br.edu.espacos.model.TipoEspaco;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainFrame extends JFrame {
    private EspacosClient client;
    private String nomeUsuario;
    private String tipoUsuario;
    
    private JTabbedPane tabbedPane;
    private JTable tabelaEspacos;
    private JTable tabelaReservas;
    private DefaultTableModel modeloEspacos;
    private DefaultTableModel modeloReservas;
    
    private JLabel statusLabel;

    // Formato padrão para exibição e entrada de data/hora
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter PERSISTENCE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MainFrame(EspacosClient client, String nomeUsuario, String tipoUsuario) {
        this.client = client;
        this.nomeUsuario = nomeUsuario;
        this.tipoUsuario = tipoUsuario;
        
        setTitle("Sistema de Gestão de Espaços Acadêmicos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        setupLayout();
        setupEventListeners();
        
        atualizarDados();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Tabela de espaços
        String[] colunasEspacos = {"ID", "Nome", "Tipo", "Localização", "Capacidade"};
        modeloEspacos = new DefaultTableModel(colunasEspacos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEspacos = new JTable(modeloEspacos);
        
        // Tabela de reservas
        String[] colunasReservas = {"ID", "Espaço", "Início", "Fim", "Finalidade", "Status"};
        modeloReservas = new DefaultTableModel(colunasReservas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaReservas = new JTable(modeloReservas);
        
        statusLabel = new JLabel("Conectado como: " + nomeUsuario + " (" + tipoUsuario + ")");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel de espaços
        JPanel painelEspacos = new JPanel(new BorderLayout());
        painelEspacos.add(new JScrollPane(tabelaEspacos), BorderLayout.CENTER);
        
        JPanel botoesEspacos = new JPanel(new FlowLayout());
        JButton btnAtualizarEspacos = new JButton("Atualizar");
        JButton btnNovaReserva = new JButton("Nova Reserva"); // Movido para cá
        botoesEspacos.add(btnAtualizarEspacos);
        botoesEspacos.add(btnNovaReserva);
        
        if ("ADMIN".equals(tipoUsuario)) {
            JButton btnCadastrarEspaco = new JButton("Cadastrar Espaço");
            JButton btnRemoverEspaco = new JButton("Remover Espaço"); // Novo botão
            botoesEspacos.add(btnCadastrarEspaco);
            botoesEspacos.add(btnRemoverEspaco);
            
            btnCadastrarEspaco.addActionListener(e -> mostrarCadastroEspaco());
            btnRemoverEspaco.addActionListener(e -> removerEspacoSelecionado());
        }
        
        painelEspacos.add(botoesEspacos, BorderLayout.SOUTH);
        
        // Painel de reservas
        JPanel painelReservas = new JPanel(new BorderLayout());
        painelReservas.add(new JScrollPane(tabelaReservas), BorderLayout.CENTER);
        
        JPanel botoesReservas = new JPanel(new FlowLayout());
        JButton btnAtualizarReservas = new JButton("Atualizar");
        JButton btnCancelarReserva = new JButton("Cancelar Reserva");
        
        botoesReservas.add(btnAtualizarReservas);
        botoesReservas.add(btnCancelarReserva);
        
        painelReservas.add(botoesReservas, BorderLayout.SOUTH);
        
        // Adicionar abas
        tabbedPane.addTab("Espaços", painelEspacos);
        tabbedPane.addTab("Minhas Reservas", painelReservas);
        
        if ("ADMIN".equals(tipoUsuario)) {
            JPanel painelRelatorios = criarPainelRelatorios();
            tabbedPane.addTab("Relatórios", painelRelatorios);
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Painel inferior com status e botão de logout
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(statusLabel, BorderLayout.WEST);
        
        JButton btnDeslogar = new JButton("Deslogar");
        btnDeslogar.setBackground(new Color(220, 53, 69));
        btnDeslogar.setForeground(Color.WHITE);
        btnDeslogar.setFocusPainted(false);
        btnDeslogar.addActionListener(e -> deslogar());
        painelInferior.add(btnDeslogar, BorderLayout.EAST);
        
        add(painelInferior, BorderLayout.SOUTH);
        
        // Event listeners
        btnAtualizarEspacos.addActionListener(e -> atualizarEspacos());
        btnAtualizarReservas.addActionListener(e -> atualizarReservas());
        btnNovaReserva.addActionListener(e -> mostrarNovaReserva());
        btnCancelarReserva.addActionListener(e -> cancelarReservaSelecionada());
    }

    private JPanel criarPainelRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JButton btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.addActionListener(e -> {
            String resposta = client.gerarRelatorio();
            String[] partes = resposta.split("\\|");
            
            if (partes[0].equals("RELATORIO")) {
                StringBuilder relatorio = new StringBuilder();
                relatorio.append("=== RELATÓRIO DO SISTEMA ===\n\n");
                
                for (int i = 1; i < partes.length; i++) {
                    relatorio.append(partes[i]).append("\n");
                }
                
                relatorio.append("\nGerado em: ").append(LocalDateTime.now().format(
                    DISPLAY_FORMATTER));
                
                areaRelatorio.setText(relatorio.toString());
            } else {
                areaRelatorio.setText("Erro ao gerar relatório: " + resposta);
            }
        });
        
        panel.add(new JScrollPane(areaRelatorio), BorderLayout.CENTER);
        panel.add(btnGerarRelatorio, BorderLayout.SOUTH);
        
        return panel;
    }

    private void setupEventListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                deslogar();
            }
        });
    }

    private void deslogar() {
        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja sair do sistema?", 
            "Confirmar Logout", 
            JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            client.desconectar();
            dispose();
            System.exit(0);
        }
    }

    private void atualizarDados() {
        atualizarEspacos();
        atualizarReservas();
    }

    private void atualizarEspacos() {
        String resposta = client.listarEspacos();
        String[] partes = resposta.split("\\|");
        
        modeloEspacos.setRowCount(0);
        
        if (partes[0].equals("ESPACOS") && partes.length > 1 && !partes[1].trim().isEmpty()) {
            String[] espacos = partes[1].split(":");
            for (String espaco : espacos) {
                if (!espaco.trim().isEmpty()) {
                    String[] dados = espaco.split(";");
                    if (dados.length >= 5) {
                        modeloEspacos.addRow(new Object[]{
                            dados[0], dados[1], dados[2], dados[3], dados[4]
                        });
                    }
                }
            }
        }
    }

    private void atualizarReservas() {
        String resposta = client.listarReservas();
        
        modeloReservas.setRowCount(0);
        
        if (resposta.startsWith("RESERVAS|") && resposta.length() > "RESERVAS|".length()) {
            String dadosReservas = resposta.substring("RESERVAS|".length());
            String[] reservasStr = dadosReservas.split(":");
            for (String reservaStr : reservasStr) {
                if (!reservaStr.trim().isEmpty()) {
                    String[] dados = reservaStr.split(";");
                    if (dados.length >= 6) {
                        try {
                            // Formata as datas para exibição
                            String dataInicioFormatada = LocalDateTime.parse(dados[2], PERSISTENCE_FORMATTER).format(DISPLAY_FORMATTER);
                            String dataFimFormatada = LocalDateTime.parse(dados[3], PERSISTENCE_FORMATTER).format(DISPLAY_FORMATTER);
                            modeloReservas.addRow(new Object[]{
                                dados[0], dados[1], dataInicioFormatada, dataFimFormatada, dados[4], dados[5]
                            });
                        } catch (DateTimeParseException e) {
                            // Adiciona sem formatação se houver erro
                            modeloReservas.addRow(new Object[]{
                                dados[0], dados[1], dados[2], dados[3], dados[4], dados[5]
                            });
                        } catch (Exception e) {
                            modeloReservas.addRow(new Object[]{
                                dados[0], dados[1], dados[2], dados[3], dados[4], dados[5]
                            });
                        }
                    }
                }
            }
        }
    }

    private void mostrarCadastroEspaco() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nomeField = new JTextField(20);
        JTextField localizacaoField = new JTextField(20);
        JTextField capacidadeField = new JTextField(20);
        JTextArea descricaoArea = new JTextArea(3, 20);
        JComboBox<TipoEspaco> tipoCombo = new JComboBox<>(TipoEspaco.values());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Localização:"), gbc);
        gbc.gridx = 1;
        panel.add(localizacaoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Capacidade:"), gbc);
        gbc.gridx = 1;
        panel.add(capacidadeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        panel.add(tipoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descricaoArea), gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Cadastrar Espaço", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText().trim();
                String localizacao = localizacaoField.getText().trim();
                int capacidade = Integer.parseInt(capacidadeField.getText().trim());
                String descricao = descricaoArea.getText().trim();
                String tipo = ((TipoEspaco) tipoCombo.getSelectedItem()).name();

                String resposta = client.cadastrarEspaco(nome, localizacao, capacidade, descricao, tipo);
                String[] partes = resposta.split("\\|");
                
                if (partes[0].equals("CADASTRO_ESPACO_OK")) {
                    JOptionPane.showMessageDialog(this, "Espaço cadastrado com sucesso!");
                    atualizarEspacos();
                } else if (partes[0].equals("CADASTRO_ESPACO_ERRO") && partes.length > 1) {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + partes[1]);
                } else if (partes[0].equals("ERRO") && partes.length > 1) {
                    JOptionPane.showMessageDialog(this, "Erro: " + partes[1]);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro desconhecido ao cadastrar espaço: " + resposta);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacidade deve ser um número válido.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void removerEspacoSelecionado() {
        int linhaSelecionada = tabelaEspacos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um espaço para remover.");
            return;
        }

        String espacoId = (String) modeloEspacos.getValueAt(linhaSelecionada, 0);
        String nomeEspaco = (String) modeloEspacos.getValueAt(linhaSelecionada, 1);
        
        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja remover o espaço \"" + nomeEspaco + "\"?", 
            "Confirmar Remoção", 
            JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            String resposta = client.removerEspaco(espacoId);
            String[] partes = resposta.split("\\|");
            
            if (partes[0].equals("REMOVER_ESPACO_OK")) {
                JOptionPane.showMessageDialog(this, "Espaço removido com sucesso!");
                atualizarEspacos();
            } else if (partes.length > 1) {
                JOptionPane.showMessageDialog(this, "Erro ao remover: " + partes[1]);
            } else {
                JOptionPane.showMessageDialog(this, "Erro desconhecido ao remover espaço: " + resposta);
            }
        }
    }

    private void mostrarNovaReserva() {
        int linhaSelecionada = tabelaEspacos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um espaço primeiro.");
            return;
        }

        String espacoId = (String) modeloEspacos.getValueAt(linhaSelecionada, 0);
        String nomeEspaco = (String) modeloEspacos.getValueAt(linhaSelecionada, 1);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Data atual + 1 dia como padrão
        LocalDateTime agora = LocalDateTime.now().plusDays(1);
        String dataInicioPadrao = agora.format(PERSISTENCE_FORMATTER);
        String dataFimPadrao = agora.plusHours(1).format(PERSISTENCE_FORMATTER);

        JTextField dataInicioField = new JTextField(dataInicioPadrao, 15);
        JTextField dataFimField = new JTextField(dataFimPadrao, 15);
        JTextField finalidadeField = new JTextField(20);
        JTextArea observacoesArea = new JTextArea(3, 20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Espaço:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(nomeEspaco), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Data/Hora Início:"), gbc);
        gbc.gridx = 1;
        panel.add(dataInicioField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Data/Hora Fim:"), gbc);
        gbc.gridx = 1;
        panel.add(dataFimField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Finalidade:"), gbc);
        gbc.gridx = 1;
        panel.add(finalidadeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(observacoesArea), gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(new JLabel("Formato: AAAA-MM-DD HH:MM"), gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nova Reserva", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dataInicio = dataInicioField.getText().trim();
            String dataFim = dataFimField.getText().trim();
            String finalidade = finalidadeField.getText().trim();
            String observacoes = observacoesArea.getText().trim();

            if (finalidade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe a finalidade da reserva.");
                return;
            }

            String resposta = client.fazerReserva(espacoId, dataInicio, dataFim, finalidade, observacoes);
            String[] partes = resposta.split("\\|");
            
            if (partes[0].equals("RESERVA_OK")) {
                JOptionPane.showMessageDialog(this, "Reserva realizada com sucesso!");
                atualizarReservas(); // Atualiza a aba de reservas
                tabbedPane.setSelectedIndex(1); // Vai para a aba de reservas para mostrar a nova reserva
            } else if (partes.length > 1) {
                JOptionPane.showMessageDialog(this, "Erro ao fazer reserva: " + partes[1]);
            } else {
                JOptionPane.showMessageDialog(this, "Erro desconhecido ao fazer reserva: " + resposta);
            }
        }
    }

    private void cancelarReservaSelecionada() {
        int linhaSelecionada = tabelaReservas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.");
            return;
        }

        String reservaId = (String) modeloReservas.getValueAt(linhaSelecionada, 0);
        
        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja cancelar esta reserva?", 
            "Confirmar Cancelamento", 
            JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            String resposta = client.cancelarReserva(reservaId);
            String[] partes = resposta.split("\\|");
            
            if (partes[0].equals("CANCELAR_OK")) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!");
                atualizarReservas();
            } else if (partes.length > 1) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + partes[1]);
            } else {
                JOptionPane.showMessageDialog(this, "Erro desconhecido ao cancelar reserva: " + resposta);
            }
        }
    }
}


