package com.develokit.maeum_ieum.service.report;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dummy.DummyObject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobRepository;
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

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBatchTest
@SpringBootTest
//@Rollback
@ActiveProfiles("dev")
class ReportGenerationSchedulerTest extends DummyObject {

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
    private LocalDate today = LocalDate.now();

    @Autowired
    private JobRepository jobRepository;


    @BeforeEach
    @Transactional
    @Rollback
    void setUp(){
        //reportRepository.deleteAll();
        //today = LocalDateTime.now();
//        Report report = reportRepository.findById(2L).orElse(null);
//        report.setStartDate(today.minusWeeks(1));

//        Elderly elderlyC = em.createQuery("select e from Elderly e where e.id = :id", Elderly.class)
//                .setParameter("id", 65L)
//                .getSingleResult();
//
//        Report oldReport = Report.builder()
//                .elderly(elderlyC)
//                .reportType(ReportType.MONTHLY)
//                .reportStatus(ReportStatus.PENDING)
//                .startDate(today.minusMonths(1))
//                .build();
//        reportRepository.save(oldReport);
//        Caregiver caregiver = newCaregiver();
//        em.persist(caregiver);
//        Elderly elderly = newElderly(caregiver, 1L);
//        em.persist(elderly);

    }

    @Test
    @Rollback
    @Transactional
    void testCreateWeeklyEmptyReports() {
        // 시나리오 1: 유저 A는 이미 PENDING 상태의 보고서가 있음
        Elderly elderlyA = em.createQuery("select e from Elderly e where e.id = :id", Elderly.class)
                .setParameter("id", 65L)
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

        List<Report> reportsA = reportRepository.findByElderly(elderlyA);
        List<Report> reportsB = reportRepository.findByElderly(elderlyB);

        System.out.println("reportsA = " + reportsA.size());
        System.out.println("reportsB = " + reportsB.size());

        assertEquals(1, reportsA.size());
        assertEquals(1, reportsB.size());
        assertEquals(today.minusDays(1), reportsA.get(0).getStartDate());
        assertEquals(today, reportsB.get(0).getStartDate());
    }
    @Test
    @Transactional
    @Rollback(value = false)
    void createMonthlyEmptyReports() {
        // 시나리오: 모든 노인에 대해 월간 보고서 생성
        today = today.minusMonths(1);

        reportService.createMonthlyEmptyReports(today);
        List<Elderly> elderlyList = elderlyRepository.findAll();


        List<Report> reports = reportRepository.findAll();
        //assertEquals(elderlyList.size(), reports.size());
        assertTrue(reports.stream().allMatch(r -> r.getReportType() == ReportType.MONTHLY));
        assertTrue(reports.stream().allMatch(r -> r.getReportStatus() == ReportStatus.PENDING));
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void createWeeklyEmptyReports() {
        // 시나리오: 모든 노인에 대해 주간 보고서 생성
        today = today.minusWeeks(1);

        reportService.createWeeklyEmptyReports(today);
        List<Elderly> elderlyList = elderlyRepository.findAll();


        List<Report> reports = reportRepository.findAll();
        //assertEquals(elderlyList.size(), reports.size());
        //assertTrue(reports.stream().allMatch(r -> r.getReportType() == ReportType.MONTHLY));
        assertTrue(reports.stream().allMatch(r -> r.getReportStatus() == ReportStatus.PENDING));
    }

    @Test
    //@Transactional
    @Rollback
    void testFullReportGenerationJob() throws Exception {
        // 시나리오 3: 유저 C는 일주일 전에 생성된 PENDING 상태의 보고서가 있음 -> 따라서 유저 C는 보고서 분석 작업이 진행되어야 함

        LocalDate todayLocalDate = today;
        System.out.println("todayLocalDate = " + todayLocalDate);

        // 배치 작업 실행
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", todayLocalDate.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        System.out.println("jobExecution.getExitStatus().getExitCode() = " + jobExecution.getExitStatus().getExitCode());

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        // 결과 확인
        Report processedReport = reportRepository.findById(2L).orElse(null);
        assertNotNull(processedReport);
        assertEquals(ReportStatus.COMPLETED, processedReport.getReportStatus());
    }

}