package br.edu.espacos.model;

/**
 * Classe que representa um Laboratório
 */
public class Laboratorio extends Espaco {
    private int numeroComputadores;
    private String tipoLaboratorio; // Ex: Informática, Química, Física
    private boolean temProjetor;
    private boolean temArCondicionado;
    private String equipamentosEspeciais;

    public Laboratorio() {
        super();
    }

    public Laboratorio(String id, String nome, String localizacao, int capacidade, String descricao,
                       int numeroComputadores, String tipoLaboratorio, boolean temProjetor, 
                       boolean temArCondicionado, String equipamentosEspeciais) {
        super(id, nome, localizacao, capacidade, descricao);
        this.numeroComputadores = numeroComputadores;
        this.tipoLaboratorio = tipoLaboratorio;
        this.temProjetor = temProjetor;
        this.temArCondicionado = temArCondicionado;
        this.equipamentosEspeciais = equipamentosEspeciais;
    }

    // Getters e Setters específicos
    public int getNumeroComputadores() {
        return numeroComputadores;
    }

    public void setNumeroComputadores(int numeroComputadores) {
        this.numeroComputadores = numeroComputadores;
    }

    public String getTipoLaboratorio() {
        return tipoLaboratorio;
    }

    public void setTipoLaboratorio(String tipoLaboratorio) {
        this.tipoLaboratorio = tipoLaboratorio;
    }

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

    public String getEquipamentosEspeciais() {
        return equipamentosEspeciais;
    }

    public void setEquipamentosEspeciais(String equipamentosEspeciais) {
        this.equipamentosEspeciais = equipamentosEspeciais;
    }

    @Override
    public TipoEspaco getTipo() {
        return TipoEspaco.LABORATORIO;
    }

    @Override
    public String getAtributosEspecificos() {
        return String.join(";",
            String.valueOf(numeroComputadores),
            tipoLaboratorio != null ? tipoLaboratorio : "",
            String.valueOf(temProjetor),
            String.valueOf(temArCondicionado),
            equipamentosEspeciais != null ? equipamentosEspeciais : ""
        );
    }

    // Método para criar objeto a partir de string do arquivo
    public static Laboratorio fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            Laboratorio lab = new Laboratorio();
            lab.setId(parts[0]);
            lab.setNome(parts[1]);
            lab.setLocalizacao(parts[2]);
            lab.setCapacidade(Integer.parseInt(parts[3]));
            lab.setDescricao(parts[4]);
            lab.setAtivo(Boolean.parseBoolean(parts[5]));
            lab.setDataCriacao(java.time.LocalDateTime.parse(parts[6], 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Atributos específicos
            if (parts.length > 8 && !parts[8].isEmpty()) {
                String[] atributos = parts[8].split(";");
                if (atributos.length >= 5) {
                    lab.setNumeroComputadores(Integer.parseInt(atributos[0]));
                    lab.setTipoLaboratorio(atributos[1]);
                    lab.setTemProjetor(Boolean.parseBoolean(atributos[2]));
                    lab.setTemArCondicionado(Boolean.parseBoolean(atributos[3]));
                    lab.setEquipamentosEspeciais(atributos[4]);
                }
            }
            return lab;
        }
        return null;
    }
}

