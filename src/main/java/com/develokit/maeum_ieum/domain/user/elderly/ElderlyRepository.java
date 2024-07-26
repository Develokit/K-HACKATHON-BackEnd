package com.develokit.maeum_ieum.domain.user.elderly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElderlyRepository extends JpaRepository<Elderly, Long> {
}
