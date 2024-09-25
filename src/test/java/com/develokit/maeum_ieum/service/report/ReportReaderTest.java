package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.config.batch.ReportJobConfig;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import jakarta.persistence.Query;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest
@SpringBatchTest
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        StepScopeTestExecutionListener.class
})
public class ReportReaderTest {
    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private ReportJobConfig reportJobConfig;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @StepScope
    void testWeeklyReportReader_pending이고_일주일전_생성된_weekly보고서_읽어야함() throws Exception {
        String today = LocalDate.now().toString();
        System.out.println("today = " + today);
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", today)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("weeklyReportGenerationStep", jobParameters);

        // Then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // 추가적인 검증 로직
        // 예: 처리된 아이템 수 확인
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertTrue(stepExecution.getReadCount() > 0);
    }
    @Test
    @StepScope
    void testMonthlyReportReader_pending이고_한_달_전_생성된_monthly보고서_읽어야함() throws Exception {
        String today = LocalDate.now().toString();
        System.out.println("today = " + today);
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", today)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("monthlyReportGenerationStep", jobParameters);

        // Then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // 추가적인 검증 로직
        // 예: 처리된 아이템 수 확인
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertTrue(stepExecution.getReadCount() > 0);
    }
}
