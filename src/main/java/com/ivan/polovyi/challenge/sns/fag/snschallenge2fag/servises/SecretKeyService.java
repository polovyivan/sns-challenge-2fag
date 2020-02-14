package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag.servises;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class SecretKeyService {

    public String generate(int secretSize) {

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[secretSize];
        secureRandom.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeAsString(bytes);
    }
}
