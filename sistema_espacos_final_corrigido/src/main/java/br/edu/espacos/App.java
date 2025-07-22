package br.edu.espacos;

import br.edu.espacos.client.EspacosClient;
import br.edu.espacos.view.LoginFrame;
import br.edu.espacos.view.MainFrame;
import br.edu.espacos.model.Usuario;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Definir o Look and Feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            EspacosClient client = new EspacosClient();
            // Tenta conectar ao servidor antes de exibir a tela de login
            if (!client.conectar("localhost", 12346)) {
                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. Verifique se o servidor está em execução.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            }

            LoginFrame loginFrame = new LoginFrame(null, client); // Passa null para o owner, já que é a primeira janela
            loginFrame.setVisible(true);

            // Após o LoginFrame ser fechado (dispose()), verifica o resultado
            if (loginFrame.isLoginSucesso()) {
                Usuario usuario = loginFrame.getUsuarioLogado();
                
                if (usuario != null) {
                    MainFrame mainFrame = new MainFrame(client, usuario.getNome(), usuario.getTipo().name());
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Erro: Dados do usuário logado não encontrados.", "Erro", JOptionPane.ERROR_MESSAGE);
                    client.desconectar();
                    System.exit(1);
                }
            } else {
                // Se o login não foi bem-sucedido (cancelado ou erro), desconecta e sai
                client.desconectar();
                System.exit(0);
            }
        });
    }
}

