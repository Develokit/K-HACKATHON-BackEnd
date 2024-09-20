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
    Optional<Report> findLatestByElderly(@Param("elderly")Elderly elderly );

    @Query("SELECT r FROM Report r WHERE r.startDate <= :date AND r.reportStatus = :status")
    List<Report> findReportsReadyForProcessing(@Param("date") LocalDateTime date, @Param("status") ReportStatus status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r " +
            "WHERE r.elderly = :elderly " +
            "AND r.reportType = :reportType " +
            "AND r.reportStatus = :reportStatus " +
            "AND r.startDate BETWEEN :startDate AND :endDate")
    boolean existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
            @Param("elderly") Elderly elderly,
            @Param("reportType") ReportType reportType,
            @Param("reportStatus") ReportStatus reportStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r " +
            "WHERE r.elderly = :elderly " +
            "AND r.reportType = :reportType " +
            "AND r.reportStatus = :reportStatus " +
            "AND r.startDate BETWEEN :startDate AND :endDate")
    boolean existsByElderlyAndReportTypeAndReportStatusAndStartDateGreaterThanEqual(
            @Param("elderly") Elderly elderly,
            @Param("reportType") ReportType reportType,
            @Param("reportStatus") ReportStatus reportStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Report> findByElderly(Elderly eldelry);
}
