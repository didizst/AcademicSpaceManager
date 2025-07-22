package br.edu.espacos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa um usuário do sistema
 */
public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private LocalDateTime dataCriacao;
    private boolean ativo;

    public Usuario() {
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }

    public Usuario(String id, String nome, String email, String senha, TipoUsuario tipo) {
        this();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Método para converter para string para persistência
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.join("|", 
            id != null ? id : "",
            nome != null ? nome : "",
            email != null ? email : "",
            senha != null ? senha : "",
            tipo != null ? tipo.name() : TipoUsuario.COMUM.name(),
            dataCriacao.format(formatter),
            String.valueOf(ativo)
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static Usuario fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 7) {
            Usuario usuario = new Usuario();
            usuario.setId(parts[0]);
            usuario.setNome(parts[1]);
            usuario.setEmail(parts[2]);
            usuario.setSenha(parts[3]);
            usuario.setTipo(TipoUsuario.valueOf(parts[4]));
            usuario.setDataCriacao(LocalDateTime.parse(parts[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            usuario.setAtivo(Boolean.parseBoolean(parts[6]));
            return usuario;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                ", ativo=" + ativo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id != null ? id.equals(usuario.id) : usuario.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

