package br.edu.espacos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EspacosServer {
    private static final int PORT = 12346;

    public static void main(String[] args) {
        System.out.println("Servidor de Gestão de Espaços Acadêmicos iniciado na porta " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandlerEspacos(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


