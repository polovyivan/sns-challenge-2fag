package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.SecretKeyDTO;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Service
public class SecretKeyService {


    public boolean verifyCode(String totpCode, String secret) {
        String totpCodeBySecret = generateTotpBySecret(secret);

        return totpCodeBySecret.equals(totpCode);
    }


    public static String generateTOTPCode(String secret) {
        Base32 base32 = new Base32();
        byte[] decoded = base32.decode(secret);
        String encodeHexString = Hex.encodeHexString(decoded);
        return TOTP.getOTP(encodeHexString);
    }

    // generate a secret key
    public String generate(int secretSize) {

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[secretSize];
        secureRandom.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeAsString(bytes);
    }

    public SecretKeyDTO getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return new SecretKeyDTO(secretKey, "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String generateTotpBySecret(String secret) {
        // Getting current timestamp representing 30 seconds time frame
        long timeFrame = System.currentTimeMillis() / 1000L / 30;

        // Encoding time frame value to HEX string - requred by TOTP generator which is used here.
        String timeEncoded = Long.toHexString(timeFrame);

        String totpCodeBySecret;
        try {
            // Encoding given secret string to HEX string - requred by TOTP generator which is used here.
            char[] secretEncoded = (char[]) new Hex().encode(secret);

            // Generating TOTP by given time and secret - using TOTP algorithm implementation provided by IETF.
            totpCodeBySecret = TOTP2.generateTOTP(String.copyValueOf(secretEncoded), timeEncoded, "6");
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
        return totpCodeBySecret;
    }


}
