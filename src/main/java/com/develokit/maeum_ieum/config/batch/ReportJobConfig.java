package com.develokit.maeum_ieum.config.batch;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.develokit.maeum_ieum.config.batch.ReportProcessor.*;

@Configuration
@RequiredArgsConstructor
public class ReportJobConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;


    @Bean
    public Job weeklyReportGenerationJob(Step reportGenerationStep, JobRepository repository){
        return new JobBuilder("weeklyReportGenerationJob", repository)
                .start(reportGenerationStep)
                .build();
    }

    //보고서 저장
    @Bean
    public JpaItemWriter<Report> reportWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Report> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    //보고서 페이징 크기:100
    @Bean
    public JpaPagingItemReader<Report> reportReader(EntityManagerFactory entityManagerFactory) {

        Map<String, Object> parameterValues = new HashMap<>();

        LocalDate today = LocalDate.now();
        parameterValues.put("reportDay", today.getDayOfWeek());
        parameterValues.put("today", today);
        parameterValues.put("sevenDaysAgo", today.minusDays(7)); //일주일 이상까지 대기 상태인 보고서들 땡겨오기

        return new JpaPagingItemReaderBuilder<Report>()
                .name("reportReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("select r from Report r where r.status = 'PENDING' and r.reportDay = :reportDay and r.startDate <= :sevenDaysAgo")
                .parameterValues(parameterValues)
                .build();
    }

    //보고서 생성 위한 청크 구성
    @Bean
    public Step reportGenerationStep(ItemReader<Report> reader,
                                     ItemProcessor<Report, ReportProcessResult> processor,
                                     ItemWriter<ReportProcessResult> writer
                                     ){

        return new StepBuilder("reportGenerationStep", jobRepository)
                .<Report, ReportProcessResult>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


}
