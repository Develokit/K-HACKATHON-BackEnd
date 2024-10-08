package com.develokit.maeum_ieum.domain.message;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByElderly(Elderly elderly, Pageable pageable);

    @Modifying(clearAutomatically = true) //PC 초기화
    @Query("delete from Message m where m.elderly = :elderly")
    int deleteAllByElderly(@Param("elderly") Elderly elderly);

    @Query("select m from Message m where m.elderly = :elderly and m.createdDate >= :startDate and m.createdDate <= :endDate")
    List<Message> findByElderlyWithTimeZone(@Param("elderly") Elderly elderly, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
