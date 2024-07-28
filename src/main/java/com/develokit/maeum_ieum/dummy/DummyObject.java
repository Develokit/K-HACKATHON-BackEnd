package com.develokit.maeum_ieum.dummy;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

public class DummyObject {

    protected Caregiver newCaregiver(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return Caregiver.builder()
                .name("userA1111")
                .password(bCryptPasswordEncoder.encode("userA1111"))
                .username("userA1234")
                .gender(Gender.FEMALE)
                .contact("010-1234-5678")
                .organization("jeju")
                .role(Role.ADMIN)
                .birthDate(LocalDate.now())
                .build();
    }
}
