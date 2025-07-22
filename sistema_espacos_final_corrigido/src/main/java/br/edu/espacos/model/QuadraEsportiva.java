package br.edu.espacos.model;

/**
 * Classe que representa uma Quadra/Campo Esportivo
 */
public class QuadraEsportiva extends Espaco {
    private String tipoEsporte; // Ex: Futebol, Basquete, Vôlei, Tênis
    private boolean temCoberta;
    private boolean temIluminacao;
    private boolean temVestiario;
    private String tipoPiso; // Ex: Grama, Sintético, Madeira, Concreto

    public QuadraEsportiva() {
        super();
    }

    public QuadraEsportiva(String id, String nome, String localizacao, int capacidade, String descricao,
                           String tipoEsporte, boolean temCoberta, boolean temIluminacao, 
                           boolean temVestiario, String tipoPiso) {
        super(id, nome, localizacao, capacidade, descricao);
        this.tipoEsporte = tipoEsporte;
        this.temCoberta = temCoberta;
        this.temIluminacao = temIluminacao;
        this.temVestiario = temVestiario;
        this.tipoPiso = tipoPiso;
    }

    // Getters e Setters específicos
    public String getTipoEsporte() {
        return tipoEsporte;
    }

    public void setTipoEsporte(String tipoEsporte) {
        this.tipoEsporte = tipoEsporte;
    }

    public boolean isTemCoberta() {
        return temCoberta;
    }

    public void setTemCoberta(boolean temCoberta) {
        this.temCoberta = temCoberta;
    }

    public boolean isTemIluminacao() {
        return temIluminacao;
    }

    public void setTemIluminacao(boolean temIluminacao) {
        this.temIluminacao = temIluminacao;
    }

    public boolean isTemVestiario() {
        return temVestiario;
    }

    public void setTemVestiario(boolean temVestiario) {
        this.temVestiario = temVestiario;
    }

    public String getTipoPiso() {
        return tipoPiso;
    }

    public void setTipoPiso(String tipoPiso) {
        this.tipoPiso = tipoPiso;
    }

    @Override
    public TipoEspaco getTipo() {
        return TipoEspaco.QUADRA_ESPORTIVA;
    }

    @Override
    public String getAtributosEspecificos() {
        return String.join(";",
            tipoEsporte != null ? tipoEsporte : "",
            String.valueOf(temCoberta),
            String.valueOf(temIluminacao),
            String.valueOf(temVestiario),
            tipoPiso != null ? tipoPiso : ""
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static QuadraEsportiva fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            QuadraEsportiva quadra = new QuadraEsportiva();
            quadra.setId(parts[0]);
            quadra.setNome(parts[1]);
            quadra.setLocalizacao(parts[2]);
            quadra.setCapacidade(Integer.parseInt(parts[3]));
            quadra.setDescricao(parts[4]);
            quadra.setAtivo(Boolean.parseBoolean(parts[5]));
            quadra.setDataCriacao(java.time.LocalDateTime.parse(parts[6], 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Atributos específicos
            if (parts.length > 8 && !parts[8].isEmpty()) {
                String[] atributos = parts[8].split(";");
                if (atributos.length >= 5) {
                    quadra.setTipoEsporte(atributos[0]);
                    quadra.setTemCoberta(Boolean.parseBoolean(atributos[1]));
                    quadra.setTemIluminacao(Boolean.parseBoolean(atributos[2]));
                    quadra.setTemVestiario(Boolean.parseBoolean(atributos[3]));
                    quadra.setTipoPiso(atributos[4]);
                }
            }
            return quadra;
        }
        return null;
    }
}

