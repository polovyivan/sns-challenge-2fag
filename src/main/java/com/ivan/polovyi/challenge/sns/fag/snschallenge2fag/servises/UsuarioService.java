package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    SecretKeyService secretKeyService;

    @Value(("${sns.ivan.authentication.2fag.secret.key.length}"))
    private int SECRET_SIZE;

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
}
