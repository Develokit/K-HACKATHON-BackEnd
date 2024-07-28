package com.develokit.maeum_ieum.config.jwt;

import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtAuthenticationFilterTest {

    @Autowired
    private CareGiverRepository careGiverRepository;

    @BeforeEach
    public void setUp() {
        Caregiver caregiver =
                Caregiver.builder()
                        .name("userA")
                        .username("user1111")
                        .password("1234")
                        .build();

        careGiverRepository.save(caregiver);
    }

    @Test
    public void authentication_test(){


    }

}