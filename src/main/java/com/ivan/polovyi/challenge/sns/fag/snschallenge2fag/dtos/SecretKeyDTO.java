package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos;

public class SecretKeyDTO {

    private String secretKey;
    private String qrCodeUrl;

    public SecretKeyDTO(String secretKey, String qrCodeUrl) {
        this.secretKey = secretKey;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
