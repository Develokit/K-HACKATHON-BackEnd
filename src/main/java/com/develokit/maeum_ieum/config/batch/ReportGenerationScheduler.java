package com.develokit.maeum_ieum.config.batch;

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
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ReportGenerationScheduler {

    private final JobLauncher jobLauncher;
    private final Job weeklyReportGenerationJob;
    private final Logger log = LoggerFactory.getLogger(ReportProcessor.class);
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행 (보고서 분석 결과 기록)
    public void runReportGenerationJob() throws Exception {

        LocalDateTime today = LocalDateTime.now();

        //기존 보고서 처리(분석 결과 반영)
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", Date.from(today.atZone(ZoneId.systemDefault()).toInstant()))
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(weeklyReportGenerationJob, jobParameters);

        //Job 완료 확인
        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            // Job 실패 처리
            log.error("주간 보고서 생성 Job 실패: " + jobExecution.getStatus());
            //재시도 로직이나.. job 실패 시 어떻게 할지는 모르겠음
        }
    }
}
