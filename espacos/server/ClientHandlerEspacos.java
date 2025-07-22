package br.edu.espacos.server;

import br.edu.espacos.model.*;
import br.edu.espacos.storage.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientHandlerEspacos implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String usuarioLogado = null;

    public ClientHandlerEspacos(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // Envia mensagem de boas-vindas
            enviarMensagem("CONECTADO|Bem-vindo ao Sistema de Gestão de Espaços Acadêmicos");

            String linha;
            while ((linha = reader.readLine()) != null) {

                processarComando(linha);
            }

        } catch (IOException e) {

        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
    
            }
        }
    }

    private void processarComando(String comando) throws IOException {
        String[] partes = comando.split("\\|");
        String acao = partes[0];

        try {
            switch (acao) {
                case "LOGIN":
                    processarLogin(partes);
                    break;
                case "REGISTRAR":
                    processarRegistro(partes);
                    break;
                case "LISTAR_ESPACOS":
                    listarEspacos();
                    break;
                case "CADASTRAR_ESPACO":
                    cadastrarEspaco(partes);
                    break;
                case "REMOVER_ESPACO":
                    removerEspaco(partes);
                    break;
                case "LISTAR_RESERVAS":
                    listarReservas();
                    break;
                case "FAZER_RESERVA":
                    fazerReserva(partes);
                    break;
                case "CANCELAR_RESERVA":
                    cancelarReserva(partes);
                    break;
                case "VERIFICAR_DISPONIBILIDADE":
                    verificarDisponibilidade(partes);
                    break;
                case "GERAR_RELATORIO":
                    gerarRelatorio();
                    break;
                case "LOGOUT":
                    usuarioLogado = null;
                    enviarMensagem("LOGOUT_OK|Logout realizado com sucesso");
                    break;
                default:
                    enviarMensagem("ERRO|Comando não reconhecido: " + acao);
            }
        } catch (Exception e) {

            enviarMensagem("ERRO|Erro interno do servidor: " + e.getMessage());
        }
    }

    private void processarLogin(String[] partes) throws IOException {
        if (partes.length < 3) {
            enviarMensagem("ERRO|Formato inválido para login");
            return;
        }

        String email = partes[1];
        String senha = partes[2];

        Usuario usuario = UsuarioStorage.buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha) && usuario.isAtivo()) {
            usuarioLogado = usuario.getId();
            enviarMensagem("LOGIN_OK|" + usuario.getNome() + "|" + usuario.getTipo().name());

        } else {
            enviarMensagem("LOGIN_ERRO|Email ou senha incorretos");

        }
    }

    private void processarRegistro(String[] partes) throws IOException {
        if (partes.length < 5) {
            enviarMensagem("ERRO|Formato inválido para registro");
            return;
        }

        String nome = partes[1];
        String email = partes[2];
        String senha = partes[3];
        String tipoStr = partes[4];

        if (UsuarioStorage.emailJaExiste(email)) {
            enviarMensagem("REGISTRO_ERRO|Email já existe");
            return;
        }

        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoStr);
            String novoId = UsuarioStorage.gerarNovoId();
            Usuario novoUsuario = new Usuario(novoId, nome, email, senha, tipo);
            UsuarioStorage.salvar(novoUsuario);
            enviarMensagem("REGISTRO_OK|Usuário registrado com sucesso");

        } catch (IllegalArgumentException e) {
            enviarMensagem("REGISTRO_ERRO|Tipo de usuário inválido");
        }
    }

    private void listarEspacos() throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        List<Espaco> espacos = EspacoStorage.carregarTodos();
        StringBuilder sb = new StringBuilder("ESPACOS");
        
        if (!espacos.isEmpty()) {
            sb.append("|");
            boolean primeiro = true;
            for (Espaco espaco : espacos) {
                if (espaco.isAtivo()) {
                    if (!primeiro) {
                        sb.append(":");
                    }
                    sb.append(espaco.getId()).append(";")
                      .append(espaco.getNome()).append(";")
                      .append(espaco.getTipo().name()).append(";")
                      .append(espaco.getLocalizacao()).append(";")
                      .append(espaco.getCapacidade());
                    primeiro = false;
                }
            }
        } else {
            sb.append("|");
        }
        
        enviarMensagem(sb.toString());
    }

    private void cadastrarEspaco(String[] partes) throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        // Verificar se é admin
        Usuario usuario = UsuarioStorage.buscarPorId(usuarioLogado);
        if (usuario == null || usuario.getTipo() != TipoUsuario.ADMIN) {
            enviarMensagem("ERRO|Apenas administradores podem cadastrar espaços");
            return;
        }

        if (partes.length < 6) {
            enviarMensagem("CADASTRO_ESPACO_ERRO|Formato inválido para cadastro de espaço");
            return;
        }

        try {
            String nome = partes[1];
            String localizacao = partes[2];
            int capacidade = Integer.parseInt(partes[3]);
            String descricao = partes[4];
            TipoEspaco tipo = TipoEspaco.valueOf(partes[5]);

            if (EspacoStorage.nomeJaExiste(nome)) {
                enviarMensagem("CADASTRO_ESPACO_ERRO|Nome já existe");
                return;
            }

            String novoId = EspacoStorage.gerarNovoId();
            Espaco novoEspaco;

            // Criar o tipo específico de espaço baseado no tipo
            switch (tipo) {
                case SALA_AULA:
                    novoEspaco = new SalaAula(novoId, nome, localizacao, capacidade, descricao, false, false, true, 0);
                    break;
                case LABORATORIO:
                    novoEspaco = new Laboratorio(novoId, nome, localizacao, capacidade, descricao, 0, "Geral", false, false, "");
                    break;
                case SALA_REUNIAO:
                    novoEspaco = new SalaReuniao(novoId, nome, localizacao, capacidade, descricao, false, false, false, false, "Retangular");
                    break;
                case QUADRA_ESPORTIVA:
                    novoEspaco = new QuadraEsportiva(novoId, nome, localizacao, capacidade, descricao, "Futebol", false, false, false, "Grama");
                    break;
                case AUDITORIO:
                    novoEspaco = new Auditorio(novoId, nome, localizacao, capacidade, descricao, false, false, true, false, 0, "Fixos");
                    break;
                default:
                    enviarMensagem("CADASTRO_ESPACO_ERRO|Tipo de espaço não suportado");
                    return;
            }

            EspacoStorage.salvar(novoEspaco);
            enviarMensagem("CADASTRO_ESPACO_OK|Espaço cadastrado com sucesso");


        } catch (NumberFormatException e) {
            enviarMensagem("CADASTRO_ESPACO_ERRO|Capacidade deve ser um número");
        } catch (IllegalArgumentException e) {
            enviarMensagem("CADASTRO_ESPACO_ERRO|Tipo de espaço inválido: " + e.getMessage());
        }
    }

    private void removerEspaco(String[] partes) throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        // Verificar se é admin
        Usuario usuario = UsuarioStorage.buscarPorId(usuarioLogado);
        if (usuario == null || usuario.getTipo() != TipoUsuario.ADMIN) {
            enviarMensagem("ERRO|Apenas administradores podem remover espaços");
            return;
        }

        if (partes.length < 2) {
            enviarMensagem("REMOVER_ESPACO_ERRO|ID do espaço não informado");
            return;
        }

        String espacoId = partes[1];
        Espaco espaco = EspacoStorage.buscarPorId(espacoId);

        if (espaco == null) {
            enviarMensagem("REMOVER_ESPACO_ERRO|Espaço não encontrado");
            return;
        }

        // Verificar se há reservas ativas para este espaço
        if (ReservaStorage.existeReservaAtivaParaEspaco(espacoId)) {
            enviarMensagem("REMOVER_ESPACO_ERRO|Não é possível remover espaço com reservas ativas.");
            return;
        }

        EspacoStorage.remover(espacoId);
        enviarMensagem("REMOVER_ESPACO_OK|Espaço removido com sucesso");

    }

    private void listarReservas() throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }


        List<Reserva> reservas = ReservaStorage.buscarPorUsuario(usuarioLogado);

        StringBuilder sb = new StringBuilder("RESERVAS");
        
        if (!reservas.isEmpty()) {
            sb.append("|");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            boolean primeiro = true;
            for (Reserva reserva : reservas) {
                Espaco espaco = EspacoStorage.buscarPorId(reserva.getEspacoId());
                
                if (!primeiro) {
                    sb.append(":");
                }
                sb.append(reserva.getId()).append(";")
                  .append(espaco != null ? espaco.getNome() : "Espaço [ID: " + reserva.getEspacoId() + "] não encontrado").append(";")
                  .append(reserva.getDataHoraInicio().format(formatter)).append(";")
                  .append(reserva.getDataHoraFim().format(formatter)).append(";")
                  .append(reserva.getFinalidade()).append(";")
                  .append(reserva.getStatus().name());
                     primeiro = false;
            }
        } else {
            sb.append("|");
        }

        enviarMensagem(sb.toString());
    }

    private void fazerReserva(String[] partes) throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        if (partes.length < 6) {
            enviarMensagem("RESERVA_ERRO|Formato inválido para reserva");
            return;
        }

        try {
            String espacoId = partes[1];
            String dataHoraInicioStr = partes[2];
            String dataHoraFimStr = partes[3];
            String finalidade = partes[4];
            String observacoes = partes[5];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dataHoraInicio = LocalDateTime.parse(dataHoraInicioStr, formatter);
            LocalDateTime dataHoraFim = LocalDateTime.parse(dataHoraFimStr, formatter);

            // Verificar se o espaço existe
            Espaco espaco = EspacoStorage.buscarPorId(espacoId);
            if (espaco == null) {
                enviarMensagem("RESERVA_ERRO|Espaço não encontrado");
                return;
            }

            // Criar nova reserva
            String novoId = ReservaStorage.gerarNovoId();
            Reserva novaReserva = new Reserva(novoId, espacoId, usuarioLogado, dataHoraInicio, dataHoraFim, finalidade, observacoes);

            // Verificar conflitos
            if (ReservaStorage.temConflito(novaReserva)) {
                enviarMensagem("RESERVA_ERRO|Horário não disponível");
                return;
            }

            ReservaStorage.salvar(novaReserva);
            enviarMensagem("RESERVA_OK|Reserva realizada com sucesso");


        } catch (Exception e) {
            enviarMensagem("RESERVA_ERRO|Erro ao processar reserva: " + e.getMessage());
        }
    }

    private void cancelarReserva(String[] partes) throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        if (partes.length < 2) {
            enviarMensagem("CANCELAR_ERRO|ID da reserva não informado");
            return;
        }

        String reservaId = partes[1];

        
        Reserva reserva = ReservaStorage.buscarPorId(reservaId);

        if (reserva == null) {
            enviarMensagem("CANCELAR_ERRO|Reserva não encontrada");

            return;
        }

        if (!reserva.getUsuarioId().equals(usuarioLogado)) {
            enviarMensagem("CANCELAR_ERRO|Você só pode cancelar suas próprias reservas");

            return;
        }

        ReservaStorage.cancelar(reservaId);
        enviarMensagem("CANCELAR_OK|Reserva cancelada com sucesso");

    }

    private void verificarDisponibilidade(String[] partes) throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        if (partes.length < 4) {
            enviarMensagem("ERRO|Formato inválido para verificação de disponibilidade");
            return;
        }

        try {
            String espacoId = partes[1];
            String dataHoraInicioStr = partes[2];
            String dataHoraFimStr = partes[3];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dataHoraInicio = LocalDateTime.parse(dataHoraInicioStr, formatter);
            LocalDateTime dataHoraFim = LocalDateTime.parse(dataHoraFimStr, formatter);

            List<Reserva> reservasConflitantes = ReservaStorage.buscarReservasAtivasPorEspacoEPeriodo(
                espacoId, dataHoraInicio, dataHoraFim);

            if (reservasConflitantes.isEmpty()) {
                enviarMensagem("DISPONIBILIDADE|DISPONIVEL");
            } else {
                enviarMensagem("DISPONIBILIDADE|OCUPADO");
            }

        } catch (Exception e) {
            enviarMensagem("ERRO|Erro ao verificar disponibilidade: " + e.getMessage());
        }
    }

    private void gerarRelatorio() throws IOException {
        if (usuarioLogado == null) {
            enviarMensagem("ERRO|Usuário não logado");
            return;
        }

        // Verificar se é admin
        Usuario usuario = UsuarioStorage.buscarPorId(usuarioLogado);
        if (usuario == null || usuario.getTipo() != TipoUsuario.ADMIN) {
            enviarMensagem("ERRO|Apenas administradores podem gerar relatórios");
            return;
        }

        List<Reserva> todasReservas = ReservaStorage.carregarTodas();
        List<Espaco> todosEspacos = EspacoStorage.carregarTodos();

        StringBuilder relatorio = new StringBuilder("RELATORIO|");
        relatorio.append("Total de Espaços: ").append(todosEspacos.size()).append(";");
        relatorio.append("Total de Reservas: ").append(todasReservas.size()).append(";");
        
        long reservasAtivas = todasReservas.stream()
            .filter(r -> r.getStatus() == StatusReserva.ATIVA)
            .count();
        relatorio.append("Reservas Ativas: ").append(reservasAtivas);

        enviarMensagem(relatorio.toString());
    }

    private void enviarMensagem(String mensagem) throws IOException {

        writer.write(mensagem + "\n");
        writer.flush();
    }
}

