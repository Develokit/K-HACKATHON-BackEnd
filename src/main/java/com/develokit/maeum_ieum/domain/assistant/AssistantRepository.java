package com.develokit.maeum_ieum.domain.assistant;

import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    Assistant findByCaregiver(Caregiver caregiver);

}
