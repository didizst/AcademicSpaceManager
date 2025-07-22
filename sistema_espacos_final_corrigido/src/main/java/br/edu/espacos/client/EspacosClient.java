package br.edu.espacos.client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class EspacosClient {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean conectado = false;
    private String host;
    private int port;

    public EspacosClient() {
    }

    public boolean conectar(String host, int port) {
        this.host = host;
        this.port = port;
        
        // Tenta conectar várias vezes com um pequeno atraso
        for (int i = 0; i < 5; i++) { // 5 tentativas
            try {
                // Fecha conexão anterior se existir
                if (socket != null && !socket.isClosed()) {
                    desconectar();
                }
                
                socket = new Socket(host, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                conectado = true;
                
                // Aguarda mensagem de boas-vindas do servidor
                String boasVindas = reader.readLine();
                System.out.println("Cliente conectado ao servidor: " + host + ":" + port);
                System.out.println("Servidor: " + boasVindas);
                
                return true;
            } catch (IOException e) {
                System.err.println("Tentativa " + (i + 1) + " de conexão falhou: " + e.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(500); // Espera 500ms antes de tentar novamente
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        conectado = false;
        return false;
    }

    public void desconectar() {
        try {
            conectado = false;
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Cliente desconectado do servidor.");
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    public String enviarComando(String comando) {
        // Verifica se está conectado antes de enviar o comando
        if (!conectado || socket == null || socket.isClosed()) {
            System.out.println("Conexão perdida ou não estabelecida. Tentando reconectar...");
            if (!conectar(this.host, this.port)) {
                return "ERRO|Não foi possível conectar ao servidor.";
            }
        }

        try {
            writer.write(comando + "\n");
            writer.flush();
            
            String resposta = reader.readLine();
            if (resposta == null) {
                System.err.println("Conexão com o servidor perdida durante a leitura da resposta.");
                desconectar();
                return "ERRO|Conexão com o servidor perdida.";
            }
            
            return resposta;
        } catch (IOException e) {
            System.err.println("Erro ao enviar comando ou ler resposta: " + e.getMessage());
            desconectar();
            return "ERRO|Erro de comunicação com o servidor.";
        }
    }

    public boolean isConectado() {
        return conectado && socket != null && !socket.isClosed();
    }

    public String login(String email, String senha) {
        return enviarComando("LOGIN|" + email + "|" + senha);
    }

    public String registrar(String nome, String email, String senha, String tipo) {
        return enviarComando("REGISTRAR|" + nome + "|" + email + "|" + senha + "|" + tipo);
    }

    public String listarEspacos() {
        return enviarComando("LISTAR_ESPACOS");
    }

    public String cadastrarEspaco(String nome, String localizacao, int capacidade, String descricao, String tipo) {
        return enviarComando("CADASTRAR_ESPACO|" + nome + "|" + localizacao + "|" + capacidade + "|" + descricao + "|" + tipo);
    }

    public String removerEspaco(String espacoId) {
        return enviarComando("REMOVER_ESPACO|" + espacoId);
    }

    public String listarReservas() {
        return enviarComando("LISTAR_RESERVAS");
    }

    public String fazerReserva(String espacoId, String dataHoraInicio, String dataHoraFim, String finalidade, String observacoes) {
        return enviarComando("FAZER_RESERVA|" + espacoId + "|" + dataHoraInicio + "|" + dataHoraFim + "|" + finalidade + "|" + observacoes);
    }

    public String cancelarReserva(String reservaId) {
        return enviarComando("CANCELAR_RESERVA|" + reservaId);
    }

    public String verificarDisponibilidade(String espacoId, String dataHoraInicio, String dataHoraFim) {
        return enviarComando("VERIFICAR_DISPONIBILIDADE|" + espacoId + "|" + dataHoraInicio + "|" + dataHoraFim);
    }

    public String gerarRelatorio() {
        return enviarComando("GERAR_RELATORIO");
    }
}


