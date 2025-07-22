package br.edu.espacos.model;

/**
 * Classe que representa uma Sala de Aula
 */
public class SalaAula extends Espaco {
    private boolean temProjetor;
    private boolean temArCondicionado;
    private boolean temQuadro;
    private int numeroComputadores;

    public SalaAula() {
        super();
    }

    public SalaAula(String id, String nome, String localizacao, int capacidade, String descricao,
                    boolean temProjetor, boolean temArCondicionado, boolean temQuadro, int numeroComputadores) {
        super(id, nome, localizacao, capacidade, descricao);
        this.temProjetor = temProjetor;
        this.temArCondicionado = temArCondicionado;
        this.temQuadro = temQuadro;
        this.numeroComputadores = numeroComputadores;
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

    public boolean isTemQuadro() {
        return temQuadro;
    }

    public void setTemQuadro(boolean temQuadro) {
        this.temQuadro = temQuadro;
    }

    public int getNumeroComputadores() {
        return numeroComputadores;
    }

    public void setNumeroComputadores(int numeroComputadores) {
        this.numeroComputadores = numeroComputadores;
    }

    @Override
    public TipoEspaco getTipo() {
        return TipoEspaco.SALA_AULA;
    }

    @Override
    public String getAtributosEspecificos() {
        return String.join(";",
            String.valueOf(temProjetor),
            String.valueOf(temArCondicionado),
            String.valueOf(temQuadro),
            String.valueOf(numeroComputadores)
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static SalaAula fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            SalaAula sala = new SalaAula();
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
                if (atributos.length >= 4) {
                    sala.setTemProjetor(Boolean.parseBoolean(atributos[0]));
                    sala.setTemArCondicionado(Boolean.parseBoolean(atributos[1]));
                    sala.setTemQuadro(Boolean.parseBoolean(atributos[2]));
                    sala.setNumeroComputadores(Integer.parseInt(atributos[3]));
                }
            }
            return sala;
        }
        return null;
    }
}

