package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.controllers;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.enums.AuthenticationStatus;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.SecretKeyService;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path = "/authenticate/")
public class AuthenticationController {

    @Value("${sns.ivan.authentication.2fag.enabled}")
    private boolean isTwoAuthEnabled;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecretKeyService secretKeyService;


    @PostMapping(path = "{login}/{senha}")
    public AuthenticationStatus authenticate(@PathVariable String login, @PathVariable String senha) {
        Optional<Usuario> usuario = usuarioService.buscarUsuario(login, senha);

        if (!usuario.isPresent()) {
            return AuthenticationStatus.FAILED;
        }
        if (isTwoAuthEnabled) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return AuthenticationStatus.REQUIRE_TOKEN_CHECK;
        } else {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario.get().getLogin(), usuario.get().getSenha(), new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return AuthenticationStatus.AUTHENTICATED;

        }
    }


    @PostMapping(path = "token/{login}/{senha}/{token}")
    public AuthenticationStatus tokenCheck(@PathVariable String login, @PathVariable String senha, @PathVariable String token) {
        Optional<Usuario> usuario = usuarioService.buscarUsuario(login, senha);

        if (!usuario.isPresent()) {
            return AuthenticationStatus.FAILED;
        }

        if (!secretKeyService.verify(token, usuario.get().getSecretKey())) {

            return AuthenticationStatus.FAILED;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario.get().getLogin(), usuario.get().getSenha(), new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthenticationStatus.AUTHENTICATED;
    }

    @PostMapping(path = "/logout")
    public void logout() {
        SecurityContextHolder.clearContext();
    }

}
