package br.edu.espacos.view;

import br.edu.espacos.client.EspacosClient;
import br.edu.espacos.model.TipoUsuario;
import br.edu.espacos.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela de login do sistema
 */
public class LoginFrame extends JDialog {
    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton registrarButton;

    private boolean loginSucesso = false;
    private Usuario usuarioLogado = null;
    private EspacosClient client;

    public LoginFrame(Frame owner, EspacosClient client) {
        super(owner, "Sistema de Gestão de Espaços Acadêmicos - Login", true);
        this.client = client;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 520); // Aumentado o tamanho para acomodar melhor os elementos e botões
        setLocationRelativeTo(owner);
        setResizable(false);

        initComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initComponents() {
        emailField = new JTextField(25);
        senhaField = new JPasswordField(25);
        loginButton = new JButton("Entrar");
        registrarButton = new JButton("Registrar Novo Usuário");
        
        // Estilização dos botões
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        registrarButton.setBackground(new Color(40, 167, 69));
        registrarButton.setForeground(Color.WHITE);
        registrarButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 249, 250));

        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titleLabel = new JLabel("Sistema de Gestão de Espaços Acadêmicos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 58, 64));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Faça login para acessar o sistema");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Espaçamento
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(Box.createVerticalStrut(10), gbc);

        // Campo de email
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(emailLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        mainPanel.add(emailField, gbc);

        // Campo de senha
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(senhaLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        senhaField.setFont(new Font("Arial", Font.PLAIN, 14));
        senhaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        mainPanel.add(senhaField, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        loginButton.setPreferredSize(new Dimension(120, 35));
        registrarButton.setPreferredSize(new Dimension(180, 35));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registrarButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10); // Aumentado o espaçamento para os botões
        mainPanel.add(buttonPanel, gbc);

        // Informações de login padrão
        JLabel infoLabel = new JLabel("<html><center>Login padrão:<br>Email: admin@sistema.com<br>Senha: admin123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(infoLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoRegistro();
            }
        });

        // Enter no campo de senha faz login
        senhaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, preencha todos os campos.", 
                "Campos Obrigatórios", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resposta = client.login(email, senha);
        String[] partes = resposta.split("\\|");
        
        if (partes[0].equals("LOGIN_OK")) {
            loginSucesso = true;
            // Cria um objeto Usuario com os dados retornados
            usuarioLogado = new Usuario("", partes[1], email, "", TipoUsuario.valueOf(partes[2])); // ID e senha vazios, pois não são necessários aqui
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Email ou senha incorretos.", 
                "Erro de Login", 
                JOptionPane.ERROR_MESSAGE);
            senhaField.setText("");
        }
    }

    private void mostrarDialogoRegistro() {
        JDialog registroDialog = new JDialog(this, "Registrar Novo Usuário", true);
        registroDialog.setSize(400, 350);
        registroDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField nomeField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField senhaField = new JPasswordField(20);
        JPasswordField confirmSenhaField = new JPasswordField(20);
        JComboBox<TipoUsuario> tipoCombo = new JComboBox<>(TipoUsuario.values());

        // Layout do diálogo
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add(senhaField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add(confirmSenhaField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        panel.add(tipoCombo, gbc);

        JButton registrarBtn = new JButton("Registrar");
        JButton cancelarBtn = new JButton("Cancelar");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.add(registrarBtn);
        buttonPanel.add(cancelarBtn);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        registrarBtn.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String senha = new String(senhaField.getPassword());
            String confirmSenha = new String(confirmSenhaField.getPassword());
            TipoUsuario tipo = (TipoUsuario) tipoCombo.getSelectedItem();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(registroDialog, 
                    "Por favor, preencha todos os campos.", 
                    "Campos Obrigatórios", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(registroDialog, 
                    "Por favor, insira um email válido.", 
                    "Email Inválido", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!senha.equals(confirmSenha)) {
                JOptionPane.showMessageDialog(registroDialog, 
                    "As senhas não coincidem.", 
                    "Senhas Diferentes", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (senha.length() < 4) {
                JOptionPane.showMessageDialog(registroDialog, 
                    "A senha deve ter pelo menos 4 caracteres.", 
                    "Senha Inválida", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String resposta = client.registrar(nome, email, senha, tipo.name());
            String[] partes = resposta.split("\\|");

            if (partes[0].equals("REGISTRO_OK")) {
                JOptionPane.showMessageDialog(registroDialog, 
                    "Usuário registrado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                registroDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registroDialog, 
                    "Erro ao registrar: " + partes[1], 
                    "Erro de Registro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarBtn.addActionListener(e -> registroDialog.dispose());

        registroDialog.add(panel);
        registroDialog.setVisible(true);
    }

    public boolean isLoginSucesso() {
        return loginSucesso;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}


