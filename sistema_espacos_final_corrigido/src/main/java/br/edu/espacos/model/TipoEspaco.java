package br.edu.espacos.model;

/**
 * Enum que define os tipos de espaço do sistema
 */
public enum TipoEspaco {
    SALA_AULA("Sala de Aula"),
    LABORATORIO("Laboratório"),
    SALA_REUNIAO("Sala de Reunião"),
    QUADRA_ESPORTIVA("Quadra/Campo Esportivo"),
    AUDITORIO("Auditório");

    private final String descricao;

    TipoEspaco(String descricao) {
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

