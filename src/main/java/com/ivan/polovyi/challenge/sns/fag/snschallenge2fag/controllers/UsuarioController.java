package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.controllers;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos.SecretKeyDTO;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities.Usuario;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.SecretKeyService;
import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecretKeyService secretKeyService;

    @Value("${sns.ivan.authentication.2fag.enabled}")
    private boolean isTwoAuthEnabled;

    @PostMapping(path = "/cadastro/{login}/{senha}")
    public ResponseEntity<SecretKeyDTO> register(@PathVariable String login, @PathVariable String senha) {

        Usuario usuario = usuarioService.criarUsuario(login, senha);
        String encodedSecret = secretKeyService.encode(usuario.getSecretKey());

        if (!isTwoAuthEnabled) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(secretKeyService.getGoogleAuthenticatorBarCodeURL(
                encodedSecret, usuario.getLogin()), HttpStatus.OK);
    }
}
