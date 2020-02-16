package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos.SecretKeyDTO;
import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(properties = {
        "sns.ivan.authentication.2fag.secret-key.length=10",
        "sns.ivan.authentication.2fag.token.duration=30",
        "sns.ivan.authentication.2fag.token.length=6",
        "sns.ivan.authentication.twofag.emissor = www.ivan-sensedia-challange.com.br"
})
class SecretKeyServiceTest {

    @Autowired
    private SecretKeyService secretKeyService;


    @Value("${sns.ivan.authentication.2fag.secret-key.length}")
    private int SECRET_SIZE;

    @Value("${sns.ivan.authentication.2fag.token.duration}")
    private int TOKEN_DURATION;

    @Value("${sns.ivan.authentication.twofag.emissor}")
    private String emissor;


    @Test
    @DisplayName("Validar secredKey")
    void generate() {
        String secretKey = secretKeyService.generate(SECRET_SIZE);
        assertEquals(SECRET_SIZE, secretKey.length());
        assertTrue(secretKey != null &&
                secretKey.chars().allMatch(Character::isLetterOrDigit));
    }

    @Test
    @DisplayName("Validar codificação")
    void encode() {
        String generatedKey = secretKeyService.generate(SECRET_SIZE);
        String encodedSecretKey = secretKeyService.encode(generatedKey);

        assertDoesNotThrow(() -> new Base32().decode(encodedSecretKey.getBytes()));
        assertThrows(
                org.apache.commons.codec.DecoderException.class,
                () -> new Base32().decode(111));

    }

    @Test
    @DisplayName("QR code url validação")
    void getGoogleAuthenticatorBarCodeURL() {
        String secretKey = secretKeyService.generate(SECRET_SIZE);
        String encodedSecretKey = secretKeyService.encode(secretKey);
        String account = "account";

        SecretKeyDTO googleAuthenticatorBarCodeURL =
                secretKeyService.getGoogleAuthenticatorBarCodeURL(encodedSecretKey, account);

        String secretKeyDTO = googleAuthenticatorBarCodeURL.getSecretKey();
        String qrCodeUrlDTO = googleAuthenticatorBarCodeURL.getQrCodeUrl();

        assertEquals(encodedSecretKey, secretKeyDTO);

        String prefix = "otpauth://totp/";
        assertTrue(qrCodeUrlDTO.startsWith(prefix));

        String withoutPrefix = qrCodeUrlDTO.replace(prefix, "");
        assertTrue(withoutPrefix.startsWith(emissor));

        String semEmissor = withoutPrefix.replace(emissor + "%3A", "");
        assertTrue(semEmissor.startsWith(account));

        String senSecretPrefix = semEmissor.replace("?secret=", "");
        String semAccount = senSecretPrefix.replace(account, "");

        assertTrue(semAccount.startsWith(encodedSecretKey));

        String semPrefixIssuer = semAccount.replace(secretKeyDTO + "&issuer=", "");
        assertTrue(semPrefixIssuer.equals(emissor));

    }

    @Test
    @DisplayName("TOTR -code validação")
    void verify() {
        //maior numero mais tempo vai demorar o teste
        int testCycles = 30;
        System.out.println("Teste começou... Pode levar  " + testCycles + " sec");
        String generatedKey = secretKeyService.generate(SECRET_SIZE);
        String encodedKey = secretKeyService.encode(generatedKey);
        Set<Boolean> tokens = new HashSet<>();

        Thread thread = new Thread(() -> {

            for (int i = testCycles; i > 0; i--) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tokens.add(secretKeyService.verify(secretKeyService.generateTotpBySecret(encodedKey), encodedKey));
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, tokens.size());
        assertEquals(true, tokens.stream().findFirst().get());
    }

}