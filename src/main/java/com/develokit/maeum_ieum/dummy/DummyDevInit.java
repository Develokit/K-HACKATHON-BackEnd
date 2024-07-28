package com.develokit.maeum_ieum.dummy;

import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import jakarta.validation.groups.ConvertGroup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DummyDevInit extends DummyObject{

    @Profile("dev")
    @Bean
    CommandLineRunner init(CareGiverRepository careGiverRepository){
        return (args) -> {
            careGiverRepository.save(newCaregiver());
        };
    }
}
