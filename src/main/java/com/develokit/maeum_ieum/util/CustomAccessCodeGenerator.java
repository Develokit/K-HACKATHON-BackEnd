package com.develokit.maeum_ieum.util;

import com.develokit.maeum_ieum.config.jwt.RequireAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
@RequiredArgsConstructor
public class CustomAccessCodeGenerator {

    private static final int CODE_LEN = 5;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecureRandom secureRandom;

    public String generateEncodedAccessCode(String name){
        String seed = name + System.currentTimeMillis();
        String encodedString = bCryptPasswordEncoder.encode(seed);

        // BCrypt 결과에서 안전하게 사용할 수 있는 부분 선택 -> 앞의 "$2a$10$" 제외하고 선택
        String safeEncodedString = encodedString.substring(7);

        StringBuilder sb = new StringBuilder(CODE_LEN);
        for (int i = 0; i < CODE_LEN; i++) {
            int randomIndex = secureRandom.nextInt(safeEncodedString.length());
            char randomChar = safeEncodedString.charAt(randomIndex);
            sb.append(ALPHABET.charAt(randomChar % ALPHABET.length()));// ALPHABET에 있는 문자로 매핑
        }

        return sb.toString();
    }
}
