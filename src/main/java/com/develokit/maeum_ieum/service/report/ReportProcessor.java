package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.develokit.maeum_ieum.service.report.ReportProcessor.*;

@Component
@RequiredArgsConstructor
public class ReportProcessor implements ItemProcessor<Report, ProcessedReport> {

    private final ReportService reportService;
    private final Logger log = LoggerFactory.getLogger(ReportProcessor.class);

    @Override
    public ProcessedReport process(Report report) throws Exception {
        try {
            log.info("보고서 분석 작업 시작: {}", report.getElderly().getId());
            //보고서 상태 변경: 대기 -> 분석 진행 중
            report.updateReportStatus(ReportStatus.PROCESSING);

            //분석 시작
            Report processedReport = reportService.generateReportContentSync(report);

            if(processedReport == null){
                log.info("보고서가 생성을 위한 데이터가 없습니다: 노인 ID {}", report.getElderly().getId());
                return new ProcessedReport(report, false);
                //return null;
            }

            //보고서 상태 변경: 분석 진행 중 -> 분석

            LocalDateTime today = LocalDateTime.now();
            processedReport.updateReportStatus(ReportStatus.COMPLETED);
            processedReport.updateEndDate(today);


            log.info("보고서 결과 반영 성공: {}", report.getElderly().getId());

            return new ProcessedReport(processedReport, true);
            //return processedReport;

        }catch (Exception e){
            log.error("보고서 분석 중 오류 발생: elderlyId = {}, error = {}", report.getElderly().getId(), e.getMessage());
            report.updateReportStatus(ReportStatus.ERROR);
            //스프링 배치에 에러 던지는 방향으로 수정할 수 있을듯 (재시작 로직에 대한 요청같은)
            throw new CustomApiException("보고서 배치 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
