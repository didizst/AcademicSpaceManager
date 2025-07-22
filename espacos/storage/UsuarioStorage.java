package br.edu.espacos.storage;

import br.edu.espacos.model.Usuario;
import br.edu.espacos.model.TipoUsuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela persistência de usuários em arquivo TXT
 */
public class UsuarioStorage {
    private static final String ARQUIVO_USUARIOS = "data/usuarios.txt";

    // Bloco estático para garantir que o diretório 'data' exista e o admin seja criado na inicialização
    static {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Cria o diretório 'data' se não existir
        }
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile(); // Cria o arquivo se não existir
                criarUsuarioAdminPadrao(); // Cria o admin apenas se o arquivo foi recém-criado
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de usuários: " + e.getMessage());
            }
        }
    }

    /**
     * Salva um usuário no arquivo
     */
    public static void salvar(Usuario usuario) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS, true))) {
            writer.write(usuario.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    /**
     * Carrega todos os usuários do arquivo
     */
    public static List<Usuario> carregarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_USUARIOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Usuario usuario = Usuario.fromFileString(linha);
                    if (usuario != null) {
                        usuarios.add(usuario);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Busca um usuário por email
     */
    public static Usuario buscarPorEmail(String email) {
        List<Usuario> usuarios = carregarTodos();
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email) && u.isAtivo())
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca um usuário por ID
     */
    public static Usuario buscarPorId(String id) {
        List<Usuario> usuarios = carregarTodos();
        return usuarios.stream()
                .filter(u -> u.getId().equals(id) && u.isAtivo())
                .findFirst()
                .orElse(null);
    }

    /**
     * Atualiza um usuário no arquivo
     */
    public static void atualizar(Usuario usuarioAtualizado) {
        List<Usuario> usuarios = carregarTodos();
        
        // Remove o usuário antigo e adiciona o atualizado
        usuarios.removeIf(u -> u.getId().equals(usuarioAtualizado.getId()));
        usuarios.add(usuarioAtualizado);
        
        salvarTodos(usuarios);
    }

    /**
     * Remove um usuário (marca como inativo)
     */
    public static void remover(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setAtivo(false);
            atualizar(usuario);
        }
    }

    /**
     * Salva todos os usuários no arquivo (sobrescreve)
     */
    private static void salvarTodos(List<Usuario> usuarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS))) {
            for (Usuario usuario : usuarios) {
                writer.write(usuario.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar todos os usuários: " + e.getMessage());
        }
    }

    /**
     * Cria o usuário administrador padrão
     */
    private static void criarUsuarioAdminPadrao() {
        // Verifica se o admin já existe antes de criar
        if (buscarPorEmail("admin@sistema.com") == null) {
            Usuario admin = new Usuario("admin", "Administrador", "admin@sistema.com", "admin123", TipoUsuario.ADMIN);
            salvar(admin);
            System.out.println("Usuário administrador padrão criado.");
        }
    }

    /**
     * Verifica se um email já está em uso
     */
    public static boolean emailJaExiste(String email) {
        return buscarPorEmail(email) != null;
    }

    /**
     * Gera um novo ID único para usuário
     */
    public static String gerarNovoId() {
        List<Usuario> usuarios = carregarTodos();
        int maiorId = 0;
        
        for (Usuario usuario : usuarios) {
            try {
                int id = Integer.parseInt(usuario.getId());
                if (id > maiorId) {
                    maiorId = id;
                }
            } catch (NumberFormatException e) {
                // Ignora IDs não numéricos
            }
        }
        
        return String.valueOf(maiorId + 1);
    }
}


