package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.dtos.SecretKeyDTO;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class SecretKeyService {

    @Value("${sns.ivan.authentication.2fag.emissor}")
    private String emissor;

    @Value("${sns.ivan.authentication.2fag.token.duration}")
    private int codeUpdateDuration;

    @Value("${sns.ivan.authentication.2fag.token.length}")
    String returnDigits;

    public boolean verify(String totpCode, String secretKey) {
        return generateTotpBySecret(secretKey).equals(totpCode);
    }

    public String generate(int secretKeySize) {
        return RandomStringUtils.random(secretKeySize, true, true).toUpperCase();
    }

    public String encode(String secretKey) {
        return new Base32().encodeToString(secretKey.getBytes());
    }

    String generateTotpBySecret(String secretKey) {
        // Getting current timestamp representing 30 seconds time frame
        long timeFrame = System.currentTimeMillis() / 1000L / codeUpdateDuration;

        // Encoding time frame value to HEX string - required by TOTP generator which is used here.
        String timeEncoded = Long.toHexString(timeFrame);

        String totpCodeBySecretKey;
        try {
            // Encoding given secretKey string to HEX string - required by TOTP generator which is used here.
            char[] secretEncoded = (char[]) new Hex().encode(secretKey);

            // Generating TOTP by given time and secretKey - using TOTP algorithm implementation provided by IETF.
            totpCodeBySecretKey = TOTP.generateTOTP(String.copyValueOf(secretEncoded), timeEncoded, returnDigits);
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
        return totpCodeBySecretKey;
    }

    public SecretKeyDTO getGoogleAuthenticatorBarCodeURL(String secretKey, String account) {
        try {
            return new SecretKeyDTO(secretKey, "otpauth://totp/"
                    + URLEncoder.encode(emissor + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(emissor, "UTF-8").replace("+", "%20"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


}
