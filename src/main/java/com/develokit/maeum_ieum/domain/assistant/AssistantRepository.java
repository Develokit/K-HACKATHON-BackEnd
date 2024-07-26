package com.develokit.maeum_ieum.domain.assistant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {
}
