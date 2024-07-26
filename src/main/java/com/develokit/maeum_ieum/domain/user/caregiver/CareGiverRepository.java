package com.develokit.maeum_ieum.domain.user.caregiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CareGiverRepository extends JpaRepository<Caregiver, Long> {

    Optional<Caregiver> findByUsername(String username);
}
