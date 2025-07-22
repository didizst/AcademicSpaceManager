package br.edu.espacos.auth;

import br.edu.espacos.model.Usuario;
import br.edu.espacos.model.TipoUsuario;
import br.edu.espacos.storage.UsuarioStorage;

/**
 * Classe responsável pela autenticação de usuários
 */
public class AuthenticationManager {
    private static Usuario usuarioLogado = null;

    /**
     * Realiza o login do usuário
     */
    public static boolean login(String email, String senha) {
        Usuario usuario = UsuarioStorage.buscarPorEmail(email);
        
        if (usuario != null && usuario.getSenha().equals(senha) && usuario.isAtivo()) {
            usuarioLogado = usuario;
            return true;
        }
        
        return false;
    }

    /**
     * Realiza o logout do usuário
     */
    public static void logout() {
        usuarioLogado = null;
    }

    /**
     * Retorna o usuário atualmente logado
     */
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Verifica se há um usuário logado
     */
    public static boolean isLogado() {
        return usuarioLogado != null;
    }

    /**
     * Verifica se o usuário logado é administrador
     */
    public static boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.getTipo() == TipoUsuario.ADMIN;
    }

    /**
     * Registra um novo usuário
     */
    public static boolean registrar(String nome, String email, String senha, TipoUsuario tipo) {
        // Verifica se o email já existe
        if (UsuarioStorage.emailJaExiste(email)) {
            return false;
        }

        // Cria novo usuário
        String novoId = UsuarioStorage.gerarNovoId();
        Usuario novoUsuario = new Usuario(novoId, nome, email, senha, tipo);
        
        // Salva no arquivo
        UsuarioStorage.salvar(novoUsuario);
        
        return true;
    }

    /**
     * Valida se o email tem formato válido
     */
    public static boolean isEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Valida se a senha atende aos critérios mínimos
     */
    public static boolean isSenhaValida(String senha) {
        return senha != null && senha.length() >= 4;
    }

    /**
     * Altera a senha do usuário logado
     */
    public static boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (usuarioLogado == null) {
            return false;
        }

        if (!usuarioLogado.getSenha().equals(senhaAtual)) {
            return false;
        }

        if (!isSenhaValida(novaSenha)) {
            return false;
        }

        usuarioLogado.setSenha(novaSenha);
        UsuarioStorage.atualizar(usuarioLogado);
        
        return true;
    }
}

