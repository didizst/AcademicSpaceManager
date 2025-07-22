package br.edu.espacos.model;

/**
 * Enum que define os status possíveis de uma reserva
 */
public enum StatusReserva {
    ATIVA("Ativa"),
    CANCELADA("Cancelada"),
    FINALIZADA("Finalizada");

    private final String descricao;

    StatusReserva(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

