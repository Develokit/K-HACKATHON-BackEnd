package com.develokit.maeum_ieum.config.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
class ReportJobConfigTest {

    @Autowired
    private JobLauncher jobLauncher;

//    @Autowired
//    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job weeklyReportGenerationJob;

    @Autowired
    private Step reportGenerationStep;

    @Test
    void testReportGenerationJob() throws Exception {
        // Job 실행
        JobExecution jobExecution = jobLauncher.run(weeklyReportGenerationJob, new JobParameters());

        // 결과 검증
        assertEquals("COMPLETED", jobExecution.getStatus().toString());
    }

//    @Test
//    void testReportGenerationStep() throws Exception {
//        // Step 실행
//        JobExecution jobExecution = jobLauncherTestUtils.launchStep("reportGenerationStep");
//
//        // Step 결과 검증
//        assertEquals("COMPLETED", jobExecution.getStatus().toString());
//    }


}