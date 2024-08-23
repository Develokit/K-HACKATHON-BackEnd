package com.develokit.maeum_ieum.util;

import com.develokit.maeum_ieum.config.jwt.RequireAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Component
public class CustomAccessCodeGenerator {

    private static final int CODE_LEN = 5;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecureRandom secureRandom;

    public String generateEncodedAccessCode(){
        StringBuilder sb = new StringBuilder(CODE_LEN);
        for(int i = 0;i<CODE_LEN;i++){
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        String accessCode = sb.toString();
        return bCryptPasswordEncoder.encode(accessCode).substring(0,5);
    }
}
