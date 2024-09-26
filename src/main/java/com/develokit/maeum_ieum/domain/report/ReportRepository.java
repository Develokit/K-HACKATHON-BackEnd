package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {


    @Query("select r from Report r " +
            "where r.elderly = :elderly " +
            "and r.reportStatus = :reportStatus " +
            "and r.reportType = :reportType " +
            "and r.id <= :cursor ")
    List<Report> findWeeklyReportByElderly(@Param("elderly") Elderly elderly,
                                           @Param("reportStatus")ReportStatus reportStatus,
                                           @Param("reportType")ReportType reportType,
                                           @Param("cursor")Long cursor, PageRequest pageRequest);


    @Query("select r from Report r " +
            "where r.elderly = :elderly " +
            "and r.reportStatus = :reportStatus " +
            "and r.reportType = :reportType " +
            "and r.id <= :cursor ")
    List<Report> findMonthlyReportByElderly(@Param("elderly") Elderly elderly,
                                            @Param("reportStatus")ReportStatus reportStatus,
                                            @Param("reportType")ReportType reportType,
                                            @Param("cursor")Long cursor, PageRequest pageRequest);

    @Query("select count(r) from Report r where r.elderly =: elderly and r.startDate = : startDate")
    int findByStartDate(@Param(value = "elderly")Elderly elderly, @Param(value = "startDate")LocalDateTime startDate);

    @Query("select r from Report r where r.elderly = :elderly and r.reportStatus = :reportStatus order by r.startDate desc")
    List<Report> findLatestByElderly(@Param("elderly")Elderly elderly, @Param("reportStatus")ReportStatus reportStatus);

    @Query("SELECT r FROM Report r WHERE r.startDate <= :date AND r.reportStatus = :status")
    List<Report> findReportsReadyForProcessing(@Param("date") LocalDateTime date, @Param("status") ReportStatus status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r " +
            "WHERE r.elderly = :elderly " +
            "AND r.reportType = :reportType " +
            "AND r.reportStatus = :reportStatus " +
            "AND r.reportDay = :reportDay")
    boolean existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
            @Param("elderly") Elderly elderly,
            @Param("reportType") ReportType reportType,
            @Param("reportStatus") ReportStatus reportStatus,
            @Param("reportDay") DayOfWeek reportDay);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r " +
            "WHERE r.elderly = :elderly " +
            "AND r.reportType = :reportType " +
            "AND r.reportStatus = :reportStatus ")
    boolean existsByElderlyAndReportTypeAndReportStatusAndStartDateGreaterThanEqual(
            @Param("elderly") Elderly elderly,
            @Param("reportType") ReportType reportType,
            @Param("reportStatus") ReportStatus reportStatus);

    List<Report> findByElderly(Elderly eldelry);

    @Query("SELECT r FROM Report r " +
            "WHERE r.elderly = :elderly " +
            "and r.reportType = :reportType " +
            "AND r.reportStatus = :reportStatus " +
            "AND r.startDate >= :startOfMonth " +
            "AND r.startDate < :startOfNextMonth")
    List<Report> findByReportTypeAndReportStatusAndYearAndMonth(
            @Param("elderly") Elderly elderly,
            @Param("reportType") ReportType reportType,
            @Param("reportStatus") ReportStatus reportStatus,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("startOfNextMonth") LocalDate startOfNextMonth
    );

    @Modifying
    @Query("DELETE FROM Report r WHERE r.elderly = :elderly")
    int deleteAllByElderly(@Param("elderly") Elderly elderly);

}
