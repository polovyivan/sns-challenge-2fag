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
        "sns.ivan.authentication.2fag.secret.key.length=10",
        "sns.ivan.authentication.2fag.code.update.duration = 30",
        "sns.ivan.authentication.2fag.code.length= 6",
        "sns.ivan.authentication.2fag.emissor = www.ivan-sensedia-challange.com.br"
})
class SecretKeyServiceTest {

    @Autowired
    private SecretKeyService secretKeyService;


    @Value("${sns.ivan.authentication.2fag.secret.key.length}")
    private int SECRET_SIZE;

    @Value("${sns.ivan.authentication.2fag.code.update.duration}")
    private int codeUpdateDuration;

    @Value("${sns.ivan.authentication.2fag.emissor}")
    private String emissor;


    @Test
    void generate() {
        String generated = secretKeyService.generate(SECRET_SIZE);
        assertEquals(SECRET_SIZE, generated.length());
        assertTrue(generated != null &&
                generated.chars().allMatch(Character::isLetterOrDigit));
    }

    @Test
    void encode() {
        String generatedKey = secretKeyService.generate(SECRET_SIZE);
        String encodedKey = secretKeyService.encode(generatedKey);

        assertDoesNotThrow(() -> new Base32().decode(encodedKey.getBytes()));
        assertThrows(
                org.apache.commons.codec.DecoderException.class,
                () -> new Base32().decode(111));

    }

    @Test
    void getGoogleAuthenticatorBarCodeURL() {
        String generatedKey = secretKeyService.generate(SECRET_SIZE);
        String encodedKey = secretKeyService.encode(generatedKey);
        String account = "account";

        SecretKeyDTO googleAuthenticatorBarCodeURL = secretKeyService.getGoogleAuthenticatorBarCodeURL(encodedKey, account);

        String secretKey = googleAuthenticatorBarCodeURL.getSecretKey();
        String qrCodeUrl = googleAuthenticatorBarCodeURL.getQrCodeUrl();

        assertEquals(encodedKey, secretKey);

        String prefix = "otpauth://totp/";
        assertTrue(qrCodeUrl.startsWith(prefix));

        String withoutPrefix = qrCodeUrl.replace(prefix, "");
        assertTrue(withoutPrefix.startsWith(emissor));

        String semEmissor = withoutPrefix.replace(emissor + "%3A", "");
        assertTrue(semEmissor.startsWith(account));

        String senSecretPrefix = semEmissor.replace("?secret=", "");
        String semAccount = senSecretPrefix.replace(account, "");

        assertTrue(semAccount.startsWith(encodedKey));

        String semPrefixIssuer = semAccount.replace(secretKey + "&issuer=", "");
        assertTrue(semPrefixIssuer.equals(emissor));

    }

    @Test
    @DisplayName("TOTR equality verification")
    void verify() {
        System.out.println("Test has begun... Can take a moment...");
        String generatedKey = secretKeyService.generate(SECRET_SIZE);
        String encodedKey = secretKeyService.encode(generatedKey);
        String generateTotpBySecret = secretKeyService.generateTotpBySecret(encodedKey);
        Set<Boolean> tokens = new HashSet<>();
        int testCycles = 30;
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