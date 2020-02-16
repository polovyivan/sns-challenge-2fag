package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities;

public class Usuario {

    private String login;
    private String senha;
    private String secretKey;

    public Usuario(String login, String senha, String secretKey) {
        this.login = login;
        this.senha = senha;
        this.secretKey = secretKey;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
