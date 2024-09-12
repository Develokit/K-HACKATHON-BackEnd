package com.develokit.maeum_ieum.config.batch;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.ReportService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.develokit.maeum_ieum.config.batch.ReportProcessor.*;

@Component
@RequiredArgsConstructor
public class ReportProcessor implements ItemProcessor<Report, ReportProcessResult> {

    private final ReportService reportService;
    private final Logger log = LoggerFactory.getLogger(ReportProcessor.class);

    @Override
    public ReportProcessResult process(Report report) throws Exception {
        ReportProcessResult result = new ReportProcessResult();
        try {
            log.info("보고서 분석 작업 시작: {}", report.getElderly().getId());
            //보고서 상태 변경: 대기 -> 분석 진행 중
            report.updateReportStatus(ReportStatus.PROCESSING);

            //분석 시작
            reportService.generateReportContent(report);

            //보고서 상태 변경: 분석 진행 중 -> 분석 완료
            LocalDateTime today = LocalDateTime.now();
            report.updateReportStatus(ReportStatus.COMPLETED);
            report.updateEndDate(today);

            //다음 주기의 빈 보고서 생성
            Report nextReport = Report.builder()
                    .reportDay(report.getReportDay())
                    .reportStatus(ReportStatus.PENDING)
                    .startDate(today)
                    .elderly(report.getElderly())
                    .build();

            result.setCompletedReport(report);
            result.setNextReport(nextReport);
            log.info("보고서 결과 반영 성공: {}", report.getElderly().getId());

        }catch (Exception e){
            log.error("보고서 분석 중 오류 발생: elderlyId = {}, error = {}", report.getElderly().getId(), e.getMessage());
            report.updateReportStatus(ReportStatus.ERROR);
            //스프링 배치에 에러 던지는 방향으로 수정할 수 있을듯 (재시작 로직에 대한 요청같은)
            throw new CustomApiException("보고서 배치 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    @Getter
    @Setter
    public static class ReportProcessResult{
        private Report completedReport;
        private Report nextReport;
    }

}
