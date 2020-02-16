package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*
A implementoçao e bem basica, ja que isso não é o foco do challenge,
no applicativo real o melhor jeito é implementar com banco de dados e Spring Data JPA
 */
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

    public Optional<Usuario> buscarUsuario(String login, String senha) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equals(login) && u.getSenha().equals(senha))
                .findFirst();
    }

    public String generateSecret() {
        return secretKeyService.generate(SECRET_SIZE);
    }
}
