package com.develokit.maeum_ieum.service.report;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ReportGenerationScheduler {

    private final JobLauncher jobLauncher;
    private final Job reportGenerationJob;
    private final ReportService reportService;


    private final Logger log = LoggerFactory.getLogger(ReportGenerationScheduler.class);

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행 (보고서 분석 결과 기록)
    public void runReportGenerationJob() throws Exception {
        LocalDateTime today = LocalDateTime.now();
        reportService.createWeeklyEmptyReports(today);
        reportService.createMonthlyEmptyReports(today);
        runReportGenerationJob(today);
    }


    void runReportGenerationJob(LocalDateTime date) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", date.toString())
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(reportGenerationJob, jobParameters);

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            log.error("보고서 생성 Job 실패: " + jobExecution.getStatus());
            // TODO: 재시도 로직 또는 실패 처리 로직 추가
        }
    }

}
