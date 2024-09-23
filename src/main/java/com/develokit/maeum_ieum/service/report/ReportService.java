package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.report.ReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.develokit.maeum_ieum.dto.report.ReqDto.*;
import static com.develokit.maeum_ieum.dto.report.RespDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ElderlyRepository elderlyRepository;
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    //보고서 메모 작성하는 기능
    @Transactional
    public ReportMemoCreateRespDto createReportMemo(ReportMemoCreateReqDto reportMemoCreateReqDto, Long elderlyId, Long reportId){

        Report reportPS = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomApiException("존재하지 않는 보고서입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        if(reportPS.getReportStatus() != ReportStatus.COMPLETED)
            throw new CustomApiException("접근할 수 없는 보고서입니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);

        elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //메모 추가(또는 수정)
        reportPS.setMemo(reportMemoCreateReqDto.getMemo());

        return new ReportMemoCreateRespDto(reportPS);
    }






    //노인 사용자의 발행된 전체 주간 보고서 내역 보내기
    //노인 1의 '2024년 07월 07일 (수)', '2024년 06월 31일 (수)', .... 이런 식으로
    public WeeklyReportListRespDto getElderlyWeeklyReportList(Long elderlyId, Long cursor, int limit){

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //초기 cursor가 설정 안되면 max_value로 설정
        if(cursor==null || cursor.equals(0L)) cursor = Long.MAX_VALUE;

        //limit+1만큼 조회해야 함!!!!
        PageRequest pageRequest = PageRequest.of(0, limit + 1, Sort.by("id").descending());

        List<Report> reportList = reportRepository.findWeeklyReportByElderly(elderlyPS,ReportStatus.COMPLETED, ReportType.WEEKLY, cursor, pageRequest);


        //다음 페이지 커서 설정
        Long nextCursor = null;
        if(reportList.size() > limit){
            nextCursor = reportList.get(limit).getId(); //다음 커서 설정
            reportList = reportList.subList(0, limit); //limit만큼 잘라서 반환
        }

        return new WeeklyReportListRespDto(reportList, nextCursor);
    }

    //노인 사용자의 발행된 전체 월간 보고서 내역 보내기
    public MonthlyReportListRespDto getElderlyMonthlyReportList(Long elderlyId, Long cursor, int limit){

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //초기 cursor가 설정 안되면 max_value로 설정
        if(cursor==null || cursor.equals(0L)) cursor = Long.MAX_VALUE;

        PageRequest pageRequest = PageRequest.of(0, limit + 1, Sort.by("id").descending());

        List<Report> reportList = reportRepository.findMonthlyReportByElderly(elderlyPS, ReportStatus.COMPLETED, ReportType.MONTHLY, cursor, pageRequest);

        Long nextCursor = null;
        if(reportList.size()>limit){
            nextCursor = reportList.get(limit).getId();
            reportList = reportList.subList(0, limit);
        }

        return new MonthlyReportListRespDto(reportList, nextCursor);

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
