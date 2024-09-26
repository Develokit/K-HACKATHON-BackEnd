package com.develokit.maeum_ieum.config.batch;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static com.develokit.maeum_ieum.service.report.ReportProcessor.*;

@Configuration
@RequiredArgsConstructor
public class ReportJobConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final Logger log = LoggerFactory.getLogger(ReportJobConfig.class);


    //주간/월간 보고서 통합 관리 Job
    @Bean
    public Job reportGenerationJob(@Qualifier("weeklyReportGenerationStep") Step weeklyReportGenerationStep,
                                   @Qualifier("monthlyReportGenerationStep") Step monthlyReportGenerationStep) {
        return new JobBuilder("reportGenerationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(weeklyReportGenerationStep)
                .next(monthlyReportGenerationStep)
                .build();
    }

    //[월간 보고서] : 보고서 페이징 크기 100 TODO 확인 필요
    //TODO MYSQL에서는 formatdatetime 함수 변경해야 함
    @Bean
    @StepScope
    @Qualifier("monthlyReportReader")
    public JpaPagingItemReader<Report> monthlyReportReader(@Value("#{jobParameters['date']}") String dateString) {

        LocalDate targetDate = LocalDate.parse(dateString);
        LocalDate oneMonthAgo = targetDate.minusMonths(1);


        log.info("Querying monthly reports created on: {}", oneMonthAgo);

        return new JpaPagingItemReaderBuilder<Report>()
                .name("monthlyReportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT r FROM Report r WHERE r.reportType = :reportType AND r.reportStatus = :reportStatus AND r.startDate = :targetDate")
                .parameterValues(Map.of(
                        "reportType", ReportType.MONTHLY,
                        "reportStatus", ReportStatus.PENDING,
                        "targetDate", oneMonthAgo
                ))
                .pageSize(100)
                .build();
    }

    //[주간 보고서] : 보고서 페이징 크기:100
    @Bean
    @StepScope
    @Qualifier("weeklyReportReader")
    public JpaPagingItemReader<Report> weeklyReportReader(@Value("#{jobParameters['date']}") String dateString) {
        log.info("weeklyReportReader called with dateString: {}", dateString);

        LocalDate targetDate = LocalDate.parse(dateString);
        DayOfWeek reportDay = targetDate.getDayOfWeek();

        log.info("Querying weekly reports for day: {}", reportDay);

        return new JpaPagingItemReaderBuilder<Report>()
                .name("weeklyReportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT r FROM Report r WHERE r.reportType = :reportType AND r.reportStatus = :reportStatus AND r.reportDay = :reportDay")
                .parameterValues(Map.of(
                        "reportType", ReportType.WEEKLY,
                        "reportStatus", ReportStatus.PENDING,
                        "reportDay", reportDay
                ))
                .pageSize(100)
                .build();
    }
    // 월간 보고서 생성 Step

    @Bean
    @Qualifier("monthlyReportGenerationStep")
    public Step monthlyReportGenerationStep(@Qualifier("monthlyReportReader") ItemReader<Report> monthlyReportReader,
                                            ItemProcessor<Report, Report> reportProcessor,
                                            ItemWriter<Report> reportWriter) {
        return new StepBuilder("monthlyReportGenerationStep", jobRepository)
                .<Report, Report>chunk(100, transactionManager)
                .reader(monthlyReportReader)
                .processor(reportProcessor)
                .writer(reportWriter)
                .build();
    }

    // 주간 보고서 생성 Step
    @Bean
    @Qualifier("weeklyReportGenerationStep")
    public Step weeklyReportGenerationStep(@Qualifier("weeklyReportReader") ItemReader<Report> weeklyReportReader,
                                           ItemProcessor<Report, Report> reportProcessor,
                                           ItemWriter<Report> reportWriter) {
        return new StepBuilder("weeklyReportGenerationStep", jobRepository)
                .<Report, Report>chunk(100, transactionManager)
                .reader(weeklyReportReader)
                .processor(reportProcessor)
                .writer(reportWriter)
                .build();
    }


}
