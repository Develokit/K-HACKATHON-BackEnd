package com.develokit.maeum_ieum.service.report;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ReportGenerationScheduler {

    private final JobLauncher jobLauncher;
    private final Job reportGenerationJob;
    private final ReportService reportService;


    private final Logger log = LoggerFactory.getLogger(ReportGenerationScheduler.class);

    @Scheduled(cron = "0 5 0 * * *") // 매일 자정 5분 후에 실행하도록
    public void runReportGenerationJob() throws Exception {
        log.info("스케줄러 시작");

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        log.info("현재 날짜: {}, 어제 날짜: {}", today, yesterday);

        // 보고서 분석 배치 작업 시작 (어제 날짜 기준)
        runReportGenerationJob(yesterday);

        // 작업 종료 후 빈 보고서 생성 (오늘 날짜 기준)
        reportService.createWeeklyEmptyReports(today);
        reportService.createMonthlyEmptyReports(today);
    }


    void runReportGenerationJob(LocalDate date) throws Exception {
        log.info("배치: Running report generation job for date: {}", date);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", date.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(reportGenerationJob, jobParameters);

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            log.error("보고서 생성 Job 실패: " + jobExecution.getStatus());
            // TODO: 재시도 로직 또는 실패 처리 로직 추가
        }
    }

}
