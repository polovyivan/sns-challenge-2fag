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
@RequestMapping(value = "/authenticate/")
public class AuthenticationController {

    // @Value("${2fa.enabled}")
    private boolean isTwoFaEnabled = true;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecretKeyService secretKeyService;


    @PostMapping(path = "{login}/{password}")
    public AuthenticationStatus authenticate(@PathVariable String login, @PathVariable String password) {
        Optional<Usuario> user = usuarioService.buscarUsuario(login, password);

        if (!user.isPresent()) {
            return AuthenticationStatus.FAILED;
        }
        if (!isTwoFaEnabled) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(login, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return AuthenticationStatus.AUTHENTICATED;
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            return AuthenticationStatus.REQUIRE_TOKEN_CHECK;
        }
    }


    @PostMapping(path = "token/{login}/{password}/{token}")
    public AuthenticationStatus tokenCheck(@PathVariable String login, @PathVariable String password, @PathVariable String token) {
        Optional<Usuario> user = usuarioService.buscarUsuario(login, password);

        if (!user.isPresent()) {
            return AuthenticationStatus.FAILED;
        }

        if (!secretKeyService.verifyCode(token, user.get().getSecret())) {
            return AuthenticationStatus.FAILED;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.get().getLogin(), user.get().getPassword(), new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthenticationStatus.AUTHENTICATED;
    }

    @PostMapping(path = "/logout")
    public void logout() {
        SecurityContextHolder.clearContext();
    }

}
