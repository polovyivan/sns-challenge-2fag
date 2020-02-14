package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.controllers;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos.SecretKeyDTO;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.SecretKeyService;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecretKeyService secretKeyService;

    @PostMapping(value = "/register/{login}/{password}")
    public SecretKeyDTO register(@PathVariable String login, @PathVariable String password) {

        Usuario user = usuarioService.criarUsuario(login, password);
        String encodedSecret = secretKeyService.encode(user.getSecret());
        return secretKeyService.getGoogleAuthenticatorBarCodeURL(encodedSecret, user.getLogin());
    }
}
