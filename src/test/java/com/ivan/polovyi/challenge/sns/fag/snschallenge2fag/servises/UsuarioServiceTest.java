package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    static private List<Usuario> usuarios = new ArrayList<>();


    @BeforeAll
    static void criarUsuario() {
        usuarios.add(new Usuario("Ivan", "ivanSenha", "secret"));
        usuarios.add(new Usuario("Ivan2", "ivanSenha2", "secret2"));
        usuarios.add(new Usuario("Ivan3", "ivanSenha3", "secret3"));
    }


    @Test
    void criarUsuarioUsandoUsuarioServise() {


        Usuario usuarioCriado = usuarioService.criarUsuario("Ivan", "ivanSenha");

        assertEquals(usuarios.get(0).getLogin(), usuarioCriado.getLogin());
        assertEquals(usuarios.get(0).getSenha(), usuarioCriado.getSenha());

    }

    @Test
    void buscarUsuario() {
        Usuario usuarioExiste = usuarioService.buscarUsuario(usuarios.get(0).getLogin(), usuarios.get(0).getSenha()).get();

        assertEquals(usuarios.get(0).getSenha(), usuarioExiste.getSenha());
        assertEquals(usuarios.get(0).getSenha(), usuarioExiste.getSenha());

        Optional<Usuario> usuarioNaoExiste = usuarioService.buscarUsuario("Nao existe", "Nao existe");

        assertEquals(Optional.empty(), usuarioNaoExiste);

    }
}