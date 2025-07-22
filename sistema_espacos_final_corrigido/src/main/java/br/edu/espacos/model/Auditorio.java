package br.edu.espacos.model;

/**
 * Classe que representa um Auditório
 */
public class Auditorio extends Espaco {
    private boolean temProjetor;
    private boolean temSomProfissional;
    private boolean temPalco;
    private boolean temArCondicionado;
    private int numeroMicrofones;
    private String tipoAssentos; // Ex: Fixos, Móveis

    public Auditorio() {
        super();
    }

    public Auditorio(String id, String nome, String localizacao, int capacidade, String descricao,
                     boolean temProjetor, boolean temSomProfissional, boolean temPalco, 
                     boolean temArCondicionado, int numeroMicrofones, String tipoAssentos) {
        super(id, nome, localizacao, capacidade, descricao);
        this.temProjetor = temProjetor;
        this.temSomProfissional = temSomProfissional;
        this.temPalco = temPalco;
        this.temArCondicionado = temArCondicionado;
        this.numeroMicrofones = numeroMicrofones;
        this.tipoAssentos = tipoAssentos;
    }

    // Getters e Setters específicos
    public boolean isTemProjetor() {
        return temProjetor;
    }

    public void setTemProjetor(boolean temProjetor) {
        this.temProjetor = temProjetor;
    }

    public boolean isTemSomProfissional() {
        return temSomProfissional;
    }

    public void setTemSomProfissional(boolean temSomProfissional) {
        this.temSomProfissional = temSomProfissional;
    }

    public boolean isTemPalco() {
        return temPalco;
    }

    public void setTemPalco(boolean temPalco) {
        this.temPalco = temPalco;
    }

    public boolean isTemArCondicionado() {
        return temArCondicionado;
    }

    public void setTemArCondicionado(boolean temArCondicionado) {
        this.temArCondicionado = temArCondicionado;
    }

    public int getNumeroMicrofones() {
        return numeroMicrofones;
    }

    public void setNumeroMicrofones(int numeroMicrofones) {
        this.numeroMicrofones = numeroMicrofones;
    }

    public String getTipoAssentos() {
        return tipoAssentos;
    }

    public void setTipoAssentos(String tipoAssentos) {
        this.tipoAssentos = tipoAssentos;
    }

    @Override
    public TipoEspaco getTipo() {
        return TipoEspaco.AUDITORIO;
    }

    @Override
    public String getAtributosEspecificos() {
        return String.join(";",
            String.valueOf(temProjetor),
            String.valueOf(temSomProfissional),
            String.valueOf(temPalco),
            String.valueOf(temArCondicionado),
            String.valueOf(numeroMicrofones),
            tipoAssentos != null ? tipoAssentos : ""
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static Auditorio fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            Auditorio auditorio = new Auditorio();
            auditorio.setId(parts[0]);
            auditorio.setNome(parts[1]);
            auditorio.setLocalizacao(parts[2]);
            auditorio.setCapacidade(Integer.parseInt(parts[3]));
            auditorio.setDescricao(parts[4]);
            auditorio.setAtivo(Boolean.parseBoolean(parts[5]));
            auditorio.setDataCriacao(java.time.LocalDateTime.parse(parts[6], 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Atributos específicos
            if (parts.length > 8 && !parts[8].isEmpty()) {
                String[] atributos = parts[8].split(";");
                if (atributos.length >= 6) {
                    auditorio.setTemProjetor(Boolean.parseBoolean(atributos[0]));
                    auditorio.setTemSomProfissional(Boolean.parseBoolean(atributos[1]));
                    auditorio.setTemPalco(Boolean.parseBoolean(atributos[2]));
                    auditorio.setTemArCondicionado(Boolean.parseBoolean(atributos[3]));
                    auditorio.setNumeroMicrofones(Integer.parseInt(atributos[4]));
                    auditorio.setTipoAssentos(atributos[5]);
                }
            }
            return auditorio;
        }
        return null;
    }
}

