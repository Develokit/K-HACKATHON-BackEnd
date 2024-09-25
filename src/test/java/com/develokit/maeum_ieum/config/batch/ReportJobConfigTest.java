package com.develokit.maeum_ieum.config.batch;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@Import(ReportJobConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
@ActiveProfiles("dev")
class ReportJobConfigTest {

    @Autowired
    private Job reportGenerationJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Test
    void testReportGenerationJob() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", LocalDate.now().toString())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncher.run(reportGenerationJob, jobParameters);

        // Then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
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