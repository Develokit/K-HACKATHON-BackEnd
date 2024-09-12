package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select count(r) from Report r where r.elderly =: elderly and r.startDate = : startDate")
    int findByStartDate(@Param(value = "elderly")Elderly elderly, @Param(value = "startDate")LocalDateTime startDate);

    @Query("select r from Report r where r.elderly = :elderly order by r.startDate desc limit 1")
    Optional<Report> findLatestByElderly(@Param("Elderly")Elderly elderly );


    List<Report> findByReportDayAndReportStatus(DayOfWeek reportDay, ReportStatus reportStatus);
}
