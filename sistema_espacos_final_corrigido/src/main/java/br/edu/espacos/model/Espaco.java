package br.edu.espacos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe abstrata que representa um espaço genérico
 */
public abstract class Espaco {
    protected String id;
    protected String nome;
    protected String localizacao;
    protected int capacidade;
    protected String descricao;
    protected boolean ativo;
    protected LocalDateTime dataCriacao;

    public Espaco() {
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }

    public Espaco(String id, String nome, String localizacao, int capacidade, String descricao) {
        this();
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.capacidade = capacidade;
        this.descricao = descricao;
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

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Método abstrato para obter o tipo do espaço
    public abstract TipoEspaco getTipo();

    // Método abstrato para obter atributos específicos
    public abstract String getAtributosEspecificos();

    // Método para converter para string para persistência
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.join("|",
            id != null ? id : "",
            nome != null ? nome : "",
            localizacao != null ? localizacao : "",
            String.valueOf(capacidade),
            descricao != null ? descricao : "",
            String.valueOf(ativo),
            dataCriacao.format(formatter),
            getTipo().name(),
            getAtributosEspecificos()
        );
    }

    @Override
    public String toString() {
        return "Espaco{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", capacidade=" + capacidade +
                ", tipo=" + getTipo() +
                ", ativo=" + ativo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Espaco espaco = (Espaco) obj;
        return id != null ? id.equals(espaco.id) : espaco.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

