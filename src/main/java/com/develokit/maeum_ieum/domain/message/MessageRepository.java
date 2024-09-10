package com.develokit.maeum_ieum.domain.message;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByElderlyOrderByCreatedDate(Elderly elderly);
}
