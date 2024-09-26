package com.develokit.maeum_ieum.domain.user.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.report.Report;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ElderlyRepository extends JpaRepository<Elderly, Long> {
    Optional<Elderly> findByContact(String contact);

    List<Elderly> findByReportDay(DayOfWeek dayOfWeek);

    //List<Elderly> findByCaregiverIdAndIdAfter(Long caregiverId, Long decodeCursor, PageRequest pageRequest);

    List<Elderly> findByCaregiverIdAndIdLessThanEqual(Long caregiverId, Long id, PageRequest pageRequest);


    Optional<Elderly> findByAccessCode(String accessCode);

    @Query("select e from Elderly e left join fetch e.assistant where e.id = :id")
    Optional<Elderly> findByIdWithAssistant(@Param("id")Long id);
}
