package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    SecretKeyService secretKeyService;

    private static final int SECRET_SIZE = 20;

    private List<Usuario> usuarios = new ArrayList<>();

    public Usuario criarUsuario(String login, String password) {
        Usuario usuario = new Usuario(login, password, generateSecret());

        usuarios.add(usuario);

        return usuario;
    }

    public Optional<Usuario> buscarUsuario(String login, String password) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equals(login) && u.getPassword().equals(password))
                .findFirst();
    }

    private String generateSecret() {
        return secretKeyService.generate(SECRET_SIZE);
    }

    public String encodeSecretKey(byte[] bytes) {
        // This Base32 encode may usually return a string with padding characters - '='.
        // QR generator which is user (zxing) does not recognize strings containing symbols other than alphanumeric
        // So just remove these redundant '=' padding symbols from resulting string
        return new Base32().encodeToString(bytes).replace("=", "");
    }
}
