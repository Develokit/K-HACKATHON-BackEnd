package com.develokit.maeum_ieum.domain.user.caregiver;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CareGiverRepository extends JpaRepository<Caregiver, Long> {

    @Query("select c from Caregiver c where c.username = :username")
    Optional<Caregiver> findByUsername(@Param(value = "username")String username);

    @Query("select c from Caregiver c")
    List<Caregiver> findAll();

    @Query("select c from Caregiver c join fetch c.assistantList where c.id = :caregiverId")
    Caregiver findCaregiverWithAssistants(@Param("caregiverId") Long caregiverId);

    @Query("select c from Caregiver c join fetch c.elderlyList where c.id = :caregiverId")
    Caregiver findCaregiverWithElderlys(@Param("caregiverId") Long caregiverId);
}
