package com.develokit.maeum_ieum.service.report;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBatchTest
@SpringBootTest
//@Rollback
@ActiveProfiles("dev")
class ReportGenerationSchedulerTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private ElderlyRepository elderlyRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    private EntityManager em;
    private LocalDateTime today;

    @BeforeEach
    void setUp(){
        //reportRepository.deleteAll();
        today = LocalDateTime.now();
    }
    @Test
    @Rollback
    @Transactional
    void testCreateWeeklyEmptyReports() {
        // 시나리오 1: 유저 A는 이미 PENDING 상태의 보고서가 있음
        Elderly elderlyA = em.createQuery("select e from Elderly e where e.id = :id", Elderly.class)
                .setParameter("id", 1L)
                .getSingleResult();
        elderlyA.modifyReportDay(today.getDayOfWeek());


        Report existingReport = Report.builder()
                .elderly(elderlyA)
                .reportType(ReportType.WEEKLY)
                .reportStatus(ReportStatus.PENDING)
                .startDate(today.minusDays(1))
                .build();
        reportRepository.save(existingReport);

        // 시나리오 2: 유저 B는 PENDING 상태의 보고서가 없음
        Elderly elderlyB = em.createQuery("select e from Elderly e where e.id = :id", Elderly.class)
                .setParameter("id",33L)
                .getSingleResult();
        elderlyB.modifyReportDay(today.getDayOfWeek());

        System.out.println("레포트 서비스 쿼리 실행!");
        reportService.createWeeklyEmptyReports(today); //유저 B에 대해서 빈 보고서를 생성해야 함
//        LocalDateTime oneWeekAgo = today.minusWeeks(1);
//
//        boolean elderlyA조건에맞냐 = reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
//                elderlyA, ReportType.WEEKLY, ReportStatus.PENDING, oneWeekAgo, today);
//
//        boolean elderlyB조건에맞냐 = reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
//                elderlyB, ReportType.WEEKLY, ReportStatus.PENDING, oneWeekAgo, today);
//
//        System.out.println("elderlyA조건에맞냐 = " + elderlyA조건에맞냐);
//        System.out.println("elderlyB조건에맞냐 = " + elderlyB조건에맞냐);

//
        List<Report> reportsA = reportRepository.findByElderly(elderlyA);
        List<Report> reportsB = reportRepository.findByElderly(elderlyB);

        System.out.println("reportsA = " + reportsA.size());
        System.out.println("reportsB = " + reportsB.size());

        assertEquals(1, reportsA.size());
        assertEquals(1, reportsB.size());
        assertEquals(today.minusDays(1), reportsA.get(0).getStartDate());
        assertEquals(today, reportsB.get(0).getStartDate());
    }


}