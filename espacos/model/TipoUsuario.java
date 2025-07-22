package br.edu.espacos.model;

/**
 * Enum que define os tipos de usuário do sistema
 */
public enum TipoUsuario {
    ADMIN("Administrador"),
    COMUM("Usuário Comum");

    private final String descricao;

    TipoUsuario(String descricao) {
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

