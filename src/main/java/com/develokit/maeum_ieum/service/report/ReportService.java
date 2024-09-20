package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ElderlyRepository elderlyRepository;
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    //노인 사용자의 발행된 전체 주간 보고서 내역 보내기
    //노인 1의 '2024년 07월 07일 (수)', '2024년 06월 31일 (수)', .... 이런 식으로
    //페이징을 어떻게 할 것인가.......
    public ReportListRespDto getElderlyReportList(Long elderlyId){


        return new ReportListRespDto();
    }

    @NoArgsConstructor
    @Getter
    public static class ReportListRespDto{
        private Long reportId; //보고서 아이디
        private String publishedDate; //발행 날짜
        private String reportDay; //DayOfWeek 타입의 reportDay를 한글로 변환시킬 것
    }


    //지표에 따른 보고서 생성하기
    @Transactional
    public void generateReportContent(Report report){

        //어쩌구저쩌구
        report.setQualitativeAnalysis("정성적 보고서 분석 결과");
        report.setQuantitativeAnalysis("정량적 보고서 분석 결과");

    }

    //PENDING 상태의 빈 보고서가 없으면 -> 해당 주의 주간 보고서 생성
    @Transactional
    public void createWeeklyEmptyReports(LocalDateTime date) {
        LocalDateTime oneWeekAgo = date.minusWeeks(1);

        List<Elderly> eligibleElderly = elderlyRepository.findByReportDay(date.getDayOfWeek());
        for (Elderly elderly : eligibleElderly) {
            if (!reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
                    elderly, ReportType.WEEKLY, ReportStatus.PENDING, oneWeekAgo, date)) {
                Report newReport = Report.builder()
                        .elderly(elderly)
                        .reportType(ReportType.WEEKLY)
                        .reportStatus(ReportStatus.PENDING)
                        .startDate(date)
                        .reportDay(date.getDayOfWeek())
                        .build();
                reportRepository.save(newReport);
            }
        }
    }

    @Transactional
    public void createMonthlyEmptyReports(LocalDateTime date) {
        // 한 달 전 날짜 계산
        LocalDateTime oneMonthAgo = date.minusMonths(1);

        // 모든 노인에 대해 월간 보고서 생성 (또는 특정 조건의 노인만 선택할 수 있음)
        List<Elderly> allElderly = elderlyRepository.findAll();
        for (Elderly elderly : allElderly) {
            if (!reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateGreaterThanEqual(
                    elderly, ReportType.MONTHLY, ReportStatus.PENDING, oneMonthAgo, date)) {
                Report newReport = Report.builder()
                        .elderly(elderly)
                        .reportType(ReportType.MONTHLY)
                        .reportStatus(ReportStatus.PENDING)
                        .startDate(date)
                        .reportDay(date.getDayOfWeek())  // 월간 보고서의 경우 이 필드가 필요한지 검토 필요
                        .build();
                reportRepository.save(newReport);
            }
        }
    }



}
