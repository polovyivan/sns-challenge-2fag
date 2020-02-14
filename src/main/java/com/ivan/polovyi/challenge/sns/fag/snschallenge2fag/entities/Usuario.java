package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.entities;

public class Usuario {

    private String login;
    private String password;
    private String secret;

    public Usuario(String login, String password, String secret) {
        this.login = login;
        this.password = password;
        this.secret = secret;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
