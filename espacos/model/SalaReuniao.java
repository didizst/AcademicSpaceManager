package br.edu.espacos.model;

/**
 * Classe que representa uma Sala de Reunião
 */
public class SalaReuniao extends Espaco {
    private boolean temProjetor;
    private boolean temArCondicionado;
    private boolean temTelefone;
    private boolean temVideoconferencia;
    private String tipoMesa; // Ex: Redonda, Retangular, U

    public SalaReuniao() {
        super();
    }

    public SalaReuniao(String id, String nome, String localizacao, int capacidade, String descricao,
                       boolean temProjetor, boolean temArCondicionado, boolean temTelefone, 
                       boolean temVideoconferencia, String tipoMesa) {
        super(id, nome, localizacao, capacidade, descricao);
        this.temProjetor = temProjetor;
        this.temArCondicionado = temArCondicionado;
        this.temTelefone = temTelefone;
        this.temVideoconferencia = temVideoconferencia;
        this.tipoMesa = tipoMesa;
    }

    // Getters e Setters específicos
    public boolean isTemProjetor() {
        return temProjetor;
    }

    public void setTemProjetor(boolean temProjetor) {
        this.temProjetor = temProjetor;
    }

    public boolean isTemArCondicionado() {
        return temArCondicionado;
    }

    public void setTemArCondicionado(boolean temArCondicionado) {
        this.temArCondicionado = temArCondicionado;
    }

    public boolean isTemTelefone() {
        return temTelefone;
    }

    public void setTemTelefone(boolean temTelefone) {
        this.temTelefone = temTelefone;
    }

    public boolean isTemVideoconferencia() {
        return temVideoconferencia;
    }

    public void setTemVideoconferencia(boolean temVideoconferencia) {
        this.temVideoconferencia = temVideoconferencia;
    }

    public String getTipoMesa() {
        return tipoMesa;
    }

    public void setTipoMesa(String tipoMesa) {
        this.tipoMesa = tipoMesa;
    }

    @Override
    public TipoEspaco getTipo() {
        return TipoEspaco.SALA_REUNIAO;
    }

    @Override
    public String getAtributosEspecificos() {
        return String.join(";",
            String.valueOf(temProjetor),
            String.valueOf(temArCondicionado),
            String.valueOf(temTelefone),
            String.valueOf(temVideoconferencia),
            tipoMesa != null ? tipoMesa : ""
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static SalaReuniao fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            SalaReuniao sala = new SalaReuniao();
            sala.setId(parts[0]);
            sala.setNome(parts[1]);
            sala.setLocalizacao(parts[2]);
            sala.setCapacidade(Integer.parseInt(parts[3]));
            sala.setDescricao(parts[4]);
            sala.setAtivo(Boolean.parseBoolean(parts[5]));
            sala.setDataCriacao(java.time.LocalDateTime.parse(parts[6], 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Atributos específicos
            if (parts.length > 8 && !parts[8].isEmpty()) {
                String[] atributos = parts[8].split(";");
                if (atributos.length >= 5) {
                    sala.setTemProjetor(Boolean.parseBoolean(atributos[0]));
                    sala.setTemArCondicionado(Boolean.parseBoolean(atributos[1]));
                    sala.setTemTelefone(Boolean.parseBoolean(atributos[2]));
                    sala.setTemVideoconferencia(Boolean.parseBoolean(atributos[3]));
                    sala.setTipoMesa(atributos[4]);
                }
            }
            return sala;
        }
        return null;
    }
}

