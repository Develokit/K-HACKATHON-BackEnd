package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageRepository;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.report.indicator.HealthStatusIndicator;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.OpenAiService;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
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
    private final MessageRepository messageRepository;
    private final WeeklyReportAnalysisService weeklyReportAnalysisService;
    private final MonthlyReportAnalysisService monthlyReportAnalysisService;
    private final Logger log = LoggerFactory.getLogger(ReportService.class);



    //TODO 월간 보고서 정량적 & 정성적 평가 조회
    public MonthlyReportAnalysisRespDto getMonthlyReportQuantitativeAnalysis(Long elderlyId, Long reportId){

        //노인 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //해당 월간 보고서 가져오기
        Report reportPS = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomApiException("존재하지 않는 보고서입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //월간 보고서 아이디로 들어왔는데, 타입이 월간 보고서가 아닌 경우 서버 에러 throw
        if(!reportPS.getReportType().equals(ReportType.MONTHLY))
            throw new CustomApiException("서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);

        try{
            return new MonthlyReportAnalysisRespDto(reportPS, elderlyPS);
        }catch (JsonSyntaxException e){
            log.error("월간 보고서 정량적 분석 결과 파싱 중 오류 발생: ", e);
            throw new CustomApiException("월간 보고서 정량적 분석 결과 파싱 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //주간 보고서 정량적 & 정성적 평가 조회
    public WeeklyReportAnalysisRespDto getWeeklyReportQuantitativeAnalysis(Long elderlyId, Long reportId){

        //노인 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //해당 주간 보고서 가져오기
        Report reportPS = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomApiException("존재하지 않는 보고서입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //주간 보고서 아이디로 들어왔는데, 타입이 주간 보고서가 아닌 경우 서버 에러 throw
        if(!reportPS.getReportType().equals(ReportType.WEEKLY))
            throw new CustomApiException("서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);

        try{
            return new WeeklyReportAnalysisRespDto(reportPS, elderlyPS);
        }catch (JsonSyntaxException e){
            log.error("주간 보고서 정량적 분석 결과 파싱 중 오류 발생: ", e);
            throw new CustomApiException("주간 보고서 정량적 분석 결과 파싱 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


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
        PageRequest pageRequest = PageRequest.of(0, limit + 1, Sort.by("endDate").descending());

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

        PageRequest pageRequest = PageRequest.of(0, limit + 1, Sort.by("endDate").descending());

        List<Report> reportList = reportRepository.findMonthlyReportByElderly(elderlyPS, ReportStatus.COMPLETED, ReportType.MONTHLY, cursor, pageRequest);

        Long nextCursor = null;
        if(reportList.size()>limit){
            nextCursor = reportList.get(limit).getId();
            reportList = reportList.subList(0, limit);
        }

        return new MonthlyReportListRespDto(reportList, nextCursor);

    }
    // 동기 메서드 (배치 프로세서용)
    @Transactional
    public Report generateReportContentSync(Report report) {
        return generateReportContent(report)
                .block(Duration.ofMinutes(30)); // 타임아웃 설정
    }

    //========================컨트롤러로 테스트하기 위한 용도===============================================================
    public Mono<Report> generate(){
        return Mono.fromCallable(() -> reportRepository.findById(121L).get())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::generateReportContent);
    }

    public Mono<Report>generateMonthly(){
        return Mono.fromCallable(() -> reportRepository.findById(122L).get())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::generateReportContent);
    }
    //==============================================================================================

    //지표에 따른 보고서 생성하기
    public Mono<Report> generateReportContent(Report report) {

        Elderly elderlyPS = elderlyRepository.findById(report.getElderly().getId()).orElseThrow(
                () -> new CustomApiException("존재하지 않는 사용자의 보고서 분석이 작동되었습니다", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR)
        );


        if (report.getReportType().equals(ReportType.WEEKLY)) {
            LocalDate startDate = report.getStartDate();
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime today = LocalDateTime.now();

            List<Message> messageList = messageRepository.findByElderlyWithTimeZone(elderlyPS, startDateTime, today);

            if (messageList.isEmpty()) {
                //TODO 대화 내역이 없다고 집어넣기
                report.setReportStatus(ReportStatus.COMPLETED);
                report.updateEndDate(LocalDateTime.now());
                return Mono.just(report);
            }

            return generateWeeklyReportAnalysis(report, messageList)
                    .flatMap(Mono::just)
                    .doOnError(e -> log.error("주간 보고서 분석 중 오류 발생", e));
        }
        else{
            return generateMonthlyReportAnalysis(report, elderlyPS)
                    .flatMap(Mono::just)
                    .doOnError(e -> log.error("월간 보고서 분석 중 오류 발생", e));
        }
    }
    //월간 보고서 분석
    private Mono<Report> generateMonthlyReportAnalysis(Report report, Elderly elderly){
        //먼저 report와 같은 달에 생성된 주간 보고서를 찾음
        LocalDate startOfMonth = CustomUtil.getStartOfMonth(report.getStartDate());
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        //주간 보고서 객체를 MonthlyReportAnalysisService에 넘기기
        return Mono.fromCallable(() ->
                        reportRepository.findByReportTypeAndReportStatusAndYearAndMonth(
                elderly, ReportType.WEEKLY, ReportStatus.COMPLETED,
                startOfMonth, startOfNextMonth
        )).subscribeOn(Schedulers.boundedElastic())
                .flatMap(weeklyReportList -> monthlyReportAnalysisService.generateMonthlyReportAnalysis(report, weeklyReportList))
                .doOnSuccess(r -> log.info("월간 보고서 분석 완료: 보고서 ID {}", r.getId()))
                .doOnError(e -> log.error("월간 보고서 분석 중 오류 발생", e));
    }

    //주간 보고서 분석
    private Mono<Report> generateWeeklyReportAnalysis(Report report, List<Message>messageList){
        return weeklyReportAnalysisService.generateWeeklyReportAnalysis(report, messageList)
                .doOnSuccess(r -> log.info("주간 보고서 분석 완료: 보고서 ID {}", r.getId()))
                .doOnError(e -> log.error("주간 보고서 분석 중 오류 발생", e));
    }


    //PENDING 상태의 빈 보고서가 없으면 -> 해당 주의 주간 보고서 생성
    @Transactional
    public void createWeeklyEmptyReports(LocalDate date) {
        LocalDate oneWeekAgo = date.minusWeeks(1);

        List<Elderly> eligibleElderly = elderlyRepository.findByReportDay(date.getDayOfWeek());
        for (Elderly elderly : eligibleElderly) {
            if (!reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateInLastWeek(
                    elderly, ReportType.WEEKLY, ReportStatus.PENDING, date.getDayOfWeek())) {
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
    public void createMonthlyEmptyReports(LocalDate date) {
        // 한 달 전 날짜 계산
        LocalDate oneMonthAgo = date.minusMonths(1);

        // 모든 노인에 대해 월간 보고서 생성 (또는 특정 조건의 노인만 선택할 수 있음)
        List<Elderly> allElderly = elderlyRepository.findAll();
        for (Elderly elderly : allElderly) {
            if (!reportRepository.existsByElderlyAndReportTypeAndReportStatusAndStartDateGreaterThanEqual(
                    elderly, ReportType.MONTHLY, ReportStatus.PENDING)) {
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
