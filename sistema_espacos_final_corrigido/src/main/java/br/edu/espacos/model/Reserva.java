package br.edu.espacos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa uma reserva de espaço
 */
public class Reserva {
    private String id;
    private String espacoId;
    private String usuarioId;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String finalidade;
    private String observacoes;
    private StatusReserva status;
    private LocalDateTime dataCriacao;

    public Reserva() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusReserva.ATIVA;
    }

    public Reserva(String id, String espacoId, String usuarioId, LocalDateTime dataHoraInicio, 
                   LocalDateTime dataHoraFim, String finalidade, String observacoes) {
        this();
        this.id = id;
        this.espacoId = espacoId;
        this.usuarioId = usuarioId;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.finalidade = finalidade;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEspacoId() {
        return espacoId;
    }

    public void setEspacoId(String espacoId) {
        this.espacoId = espacoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Método para verificar se há conflito com outra reserva
    public boolean temConflitoCom(Reserva outraReserva) {
        if (!this.espacoId.equals(outraReserva.espacoId)) {
            return false; // Espaços diferentes, sem conflito
        }
        
        if (this.status == StatusReserva.CANCELADA || outraReserva.status == StatusReserva.CANCELADA) {
            return false; // Reservas canceladas não geram conflito
        }

        // Verifica sobreposição de horários
        return this.dataHoraInicio.isBefore(outraReserva.dataHoraFim) && 
               this.dataHoraFim.isAfter(outraReserva.dataHoraInicio);
    }

    // Método para converter para string para persistência
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.join("|",
            id != null ? id : "",
            espacoId != null ? espacoId : "",
            usuarioId != null ? usuarioId : "",
            dataHoraInicio != null ? dataHoraInicio.format(formatter) : "",
            dataHoraFim != null ? dataHoraFim.format(formatter) : "",
            finalidade != null ? finalidade : "",
            observacoes != null ? observacoes : "",
            status != null ? status.name() : StatusReserva.ATIVA.name(),
            dataCriacao.format(formatter)
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static Reserva fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            Reserva reserva = new Reserva();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            reserva.setId(parts[0]);
            reserva.setEspacoId(parts[1]);
            reserva.setUsuarioId(parts[2]);
            reserva.setDataHoraInicio(LocalDateTime.parse(parts[3], formatter));
            reserva.setDataHoraFim(LocalDateTime.parse(parts[4], formatter));
            reserva.setFinalidade(parts[5]);
            reserva.setObservacoes(parts[6]);
            reserva.setStatus(StatusReserva.valueOf(parts[7]));
            reserva.setDataCriacao(LocalDateTime.parse(parts[8], formatter));
            
            return reserva;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + id + '\'' +
                ", espacoId='" + espacoId + '\'' +
                ", usuarioId='" + usuarioId + '\'' +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                ", finalidade='" + finalidade + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reserva reserva = (Reserva) obj;
        return id != null ? id.equals(reserva.id) : reserva.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

