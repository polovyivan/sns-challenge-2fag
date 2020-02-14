package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.controllers;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos.SecretKeyDTO;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.SecretKeyService;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.UsuarioService;
import jdk.net.SocketFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecretKeyService secretKeyService;

    @Value(("${sns.ivan.authentication.2fag.enabled}"))
    private boolean isTwoAuthEnabled;

    @PostMapping(value = "/register/{login}/{password}")
    public ResponseEntity<SecretKeyDTO> register(@PathVariable String login, @PathVariable String password) {

        Usuario user = usuarioService.criarUsuario(login, password);
        String encodedSecret = secretKeyService.encode(user.getSecret());

        if(!isTwoAuthEnabled){
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(secretKeyService.getGoogleAuthenticatorBarCodeURL(
                encodedSecret, user.getLogin()), HttpStatus.OK);
    }
}
