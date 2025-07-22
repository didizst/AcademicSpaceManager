package br.edu.espacos.storage;

import br.edu.espacos.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela persistência de espaços em arquivo TXT
 */
public class EspacoStorage {
    private static final String ARQUIVO_ESPACOS = "data/espacos.txt";

    // Bloco estático para garantir que o diretório 'data' exista e o arquivo seja criado na inicialização
    static {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Cria o diretório 'data' se não existir
        }
        File arquivo = new File(ARQUIVO_ESPACOS);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile(); // Cria o arquivo se não existir
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de espaços: " + e.getMessage());
            }
        }
    }

    /**
     * Salva um espaço no arquivo
     */
    public static void salvar(Espaco espaco) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_ESPACOS, true))) {
            writer.write(espaco.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar espaço: " + e.getMessage());
        }
    }

    /**
     * Carrega todos os espaços do arquivo
     */
    public static List<Espaco> carregarTodos() {
        List<Espaco> espacos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_ESPACOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Espaco espaco = criarEspacoFromString(linha);
                    if (espaco != null) {
                        espacos.add(espaco);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar espaços: " + e.getMessage());
        }

        return espacos;
    }

    /**
     * Cria um objeto Espaco a partir de uma string do arquivo
     */
    private static Espaco criarEspacoFromString(String linha) {
        String[] parts = linha.split("\\|");
        if (parts.length >= 8) {
            TipoEspaco tipo = TipoEspaco.valueOf(parts[7]);
            
            switch (tipo) {
                case SALA_AULA:
                    return SalaAula.fromFileString(linha);
                case LABORATORIO:
                    return Laboratorio.fromFileString(linha);
                case SALA_REUNIAO:
                    return SalaReuniao.fromFileString(linha);
                case QUADRA_ESPORTIVA:
                    return QuadraEsportiva.fromFileString(linha);
                case AUDITORIO:
                    return Auditorio.fromFileString(linha);
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Busca um espaço por ID
     */
    public static Espaco buscarPorId(String id) {
        List<Espaco> espacos = carregarTodos();
        return espacos.stream()
                .filter(e -> e.getId().equals(id) && e.isAtivo())
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca espaços por tipo
     */
    public static List<Espaco> buscarPorTipo(TipoEspaco tipo) {
        List<Espaco> espacos = carregarTodos();
        return espacos.stream()
                .filter(e -> e.getTipo() == tipo && e.isAtivo())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Busca espaços por nome (busca parcial)
     */
    public static List<Espaco> buscarPorNome(String nome) {
        List<Espaco> espacos = carregarTodos();
        return espacos.stream()
                .filter(e -> e.getNome().toLowerCase().contains(nome.toLowerCase()) && e.isAtivo())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Atualiza um espaço no arquivo
     */
    public static void atualizar(Espaco espacoAtualizado) {
        List<Espaco> espacos = carregarTodos();
        
        // Remove o espaço antigo e adiciona o atualizado
        espacos.removeIf(e -> e.getId().equals(espacoAtualizado.getId()));
        espacos.add(espacoAtualizado);
        
        salvarTodos(espacos);
    }

    /**
     * Remove um espaço (marca como inativo)
     */
    public static void remover(String id) {
        Espaco espaco = buscarPorId(id);
        if (espaco != null) {
            espaco.setAtivo(false);
            atualizar(espaco);
        }
    }

    /**
     * Salva todos os espaços no arquivo (sobrescreve)
     */
    private static void salvarTodos(List<Espaco> espacos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_ESPACOS))) {
            for (Espaco espaco : espacos) {
                writer.write(espaco.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar todos os espaços: " + e.getMessage());
        }
    }

    /**
     * Gera um novo ID único para espaço
     */
    public static String gerarNovoId() {
        List<Espaco> espacos = carregarTodos();
        int maiorId = 0;
        
        for (Espaco espaco : espacos) {
            try {
                int id = Integer.parseInt(espaco.getId());
                if (id > maiorId) {
                    maiorId = id;
                }
            } catch (NumberFormatException e) {
                // Ignora IDs não numéricos
            }
        }
        
        return String.valueOf(maiorId + 1);
    }

    /**
     * Verifica se um nome de espaço já existe
     */
    public static boolean nomeJaExiste(String nome) {
        List<Espaco> espacos = carregarTodos();
        return espacos.stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(nome) && e.isAtivo());
    }
}


