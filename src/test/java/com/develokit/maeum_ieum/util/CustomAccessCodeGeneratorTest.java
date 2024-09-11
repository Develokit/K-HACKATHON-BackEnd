package com.develokit.maeum_ieum.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class CustomAccessCodeGeneratorTest {

    @Autowired
    private CustomAccessCodeGenerator customAccessCodeGenerator;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void 같은_이름_accessCode_생성_테스트() throws Exception{
        //given
        String name1 = "이름";
        String name2 = "이름";

        //when
        String name1AccessCode = customAccessCodeGenerator.generateEncodedAccessCode(name1);
        String name2AccessCode = customAccessCodeGenerator.generateEncodedAccessCode(name2);

        //then

        assertNotNull(name1AccessCode);
        assertNotNull(name2AccessCode);
        assertNotEquals(name1AccessCode, name2AccessCode);
        assertEquals(5, name1AccessCode.length());
        assertEquals(5, name2AccessCode.length());
        assertTrue(name1AccessCode.matches("[A-Z0-9]{5}"));
        assertTrue(name2AccessCode.matches("[A-Z0-9]{5}"));

        System.out.println("name1AccessCode = " + name1AccessCode);
        System.out.println("name2AccessCode = " + name2AccessCode);

    }

}