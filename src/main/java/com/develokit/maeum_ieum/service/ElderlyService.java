package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequestRepository;
import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageRepository;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.assistant.RespDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServlet;
import lombok.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.thread.RespDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ElderlyService {

    private final ElderlyRepository elderlyRepository;
    private final CareGiverRepository careGiverRepository;
    private final S3Service s3Service;
    private final AssistantRepository assistantRepository;
    private final OpenAiService openAiService;
    private final ReportRepository reportRepository;
    private final MessageRepository messageRepository;
    private final EmergencyRequestRepository emergencyRequestRepository;

    private final Logger log = LoggerFactory.getLogger(CaregiverService.class);

    //노인 등록
    //@Transactional
    public ElderlyCreateRespDto createElderly(ElderlyCreateReqDto elderlyCreateReqDto, String username){

        //요양사 가져오기
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //노인 중복 검사 -> 연락처
        Optional<Elderly> elderlyOP = elderlyRepository.findByContact(elderlyCreateReqDto.getContact());
        if(elderlyOP.isPresent()) throw new CustomApiException("이미 등록된 사용자 입니다", HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);

        String imgUrl = null;
        //이미지 저장
        if(elderlyCreateReqDto.getImg() != null)
            imgUrl = s3Service.uploadImage(elderlyCreateReqDto.getImg());

        //저장
        Elderly elderlyPS = elderlyRepository.save(elderlyCreateReqDto.toEntity(caregiverPS, imgUrl));

        //TODO 보고서 생성 -> 출력을 원하는 요일에 생성되도록 지정해야 함
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime[] weekStartAndEnd = CustomUtil.getWeekStartAndEnd(now);
//        LocalDateTime startDayOfWeek = weekStartAndEnd[0];
//        LocalDateTime endDayOfWeek = weekStartAndEnd[1];
//        LocalDateTime lastDayOfMonth = CustomUtil.getMonthEnd(now);
//
//        //TODO 이것도 수정해야 함
//        Report weeklyReportPS = reportRepository.save(new Report(elderlyPS, startDayOfWeek, endDayOfWeek, ReportType.WEEKLY));
//        Report monthlyReportPS = reportRepository.save(new Report(elderlyPS, startDayOfWeek, lastDayOfMonth, ReportType.MONTHLY ));
//
//        elderlyPS.getWeeklyReports().add(weeklyReportPS);
//        elderlyPS.getMonthlyReports().add(monthlyReportPS);


        return new ElderlyCreateRespDto(elderlyPS);
    }

    //홈화면(노인): assistant의 pk -> 마지막 대화 시간, 이름, 생년월일(나이), 프로필 img, 요양사 이름, 요양사 프로필, 요양사 저나번호
    public ElderlyMainRespDto getElderlyMainInfo(Long elderlyId, Long assistantId){

        //연결된 노인 사용자 찾기
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(
                        () -> new CustomApiException("등록되지 않은 노인 사용자 입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );

        //노인 사용자의 어시스턴트가 아니면
        if(elderlyPS.getAssistant() != null)
            if(!elderlyPS.getAssistant().getId().equals(assistantId))
                throw new CustomApiException("해당 사용자의 AI 어시스턴트가 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);

        //노인 사용자가 어시스턴트 없으면
        if(elderlyPS.getAssistant() == null)
            throw new CustomApiException("등록된 AI 어시스턴트가 없습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);

        //연결된 요양사 찾기
        Caregiver caregiverPS = careGiverRepository.findById(elderlyPS.getCaregiver().getId())
                .orElseThrow(
                        () -> new CustomApiException("관리하는 요양사 사용자가 존재하지 않습니다",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND )
                );

        return new ElderlyMainRespDto(caregiverPS, elderlyPS);
    }


    //@Transactional
    //채팅 화면 들어가기: 어시스턴트 검증 및 스레드 검증 진행
    public CheckAssistantInfoRespDto checkAssistantInfo(Long elderlyId, Long assistantId){

        //사용자 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //사용자에게 어시스턴트가 없으면 에러 throw
        if(elderlyPS.getAssistant() == null)
            throw new CustomApiException("등록된 AI 어시스턴트가 없습니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);


        //어시스턴트 검증
        Assistant assistantPS = assistantRepository.findById(assistantId)
                .orElseThrow(
                        () -> new CustomApiException("존재하지 않는 AI 어시스턴트 입니다",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );

        //사용자의 어시스턴트가 아니면
        if(elderlyPS.getAssistant() != assistantPS)
            throw new CustomApiException("해당 사용자의 AI 어시스턴트가 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);


        //스레드가 있는지 확인 -> 없으면 스레드 생성
        if(!assistantPS.hasThread()){ //스레드 없음
            log.debug("디버그 : 해당 AI 어시스턴트는 스레드 없으므로 스레드 생성");
            ThreadRespDto threadRespDto = openAiService.createThread();
            assistantPS.attachThread(threadRespDto.getId());
        }
        else{ //스레드가 있다면
            log.debug("디버그 : 해당 AI 어시스턴트는 스레드가 존재함");
            LocalDateTime lastChatTime = elderlyPS.getLastChatTime();
            //이전 응답 기록이 없다면
            if (lastChatTime == null) {
                //스레드 생성일로부터 30일이 지났는지 검증 -> 지났다면 새로운 스레드 생성
                LocalDateTime threadCreatedDate = assistantPS.getThreadCreatedDate();

                //스레드 생성일로부터 30일이 지남
                if(ChronoUnit.DAYS.between(threadCreatedDate, LocalDateTime.now()) >= 30){
                    log.debug("디버그 : 해당 AI 어시스턴트는 스레드 생성일로부터 30일이 지났고, 이전 대화 기록이 없으므로 새 스레드 생성");
                    ThreadRespDto threadRespDto = openAiService.createThread();
                    assistantPS.attachThread(threadRespDto.getId());
                }
                //스레드 생성일로부터 30일이 지나지 않으면 그대로 반환

            } else { //이전 응답 기록이 있다면
                //마지막 대화일로부터 30일이 지났는지 확인
                if(ChronoUnit.DAYS.between(lastChatTime, LocalDateTime.now()) >= 30){
                    log.debug("디버그 : 해당 AI 어시스턴트는 마지막 대화로부터 30일이 지났으므로 새 스레드 생성");
                    ThreadRespDto threadRespDto = openAiService.createThread();
                    assistantPS.attachThread(threadRespDto.getId());
                }
                //마지막 대화일로부터 30일이 지나지 않았으므로 그대로 반환
            }
        }

        return new CheckAssistantInfoRespDto(assistantPS);
    }

    //채팅 내역 끌고오기
    public ChatInfoDto getChatHistory(int page, int size, Long elderlyId){

        //사용자 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //이전 대화 기록 -> 마지막 대화 시간 정보가 있으면 이전 대화 기록 끌고오기
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Message> messagePage = messageRepository.findByElderly(elderlyPS, pageable);

        return new ChatInfoDto(messagePage, size);
    }


    //@Transactional
    public void updateLastChatDate(Long elderlyId){
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        elderlyPS.updateLastChatDate(LocalDateTime.now());
    }

    //TODO 요양사 화면에서 노인 기본 정보 수정 - 이미지 제외
    //@Transactional
    public ElderlyModifyRespDto modifyElderlyInfo(ElderlyModifyReqDto elderlyModifyReqDto, Long elderlyId, String username){

        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //해당 요양사가 관리하는 노인 사용자가 맞는지 검증
        if(!caregiverPS.getElderlyList().contains(elderlyPS))
            throw new CustomApiException("관리 대상이 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);


        //노인 기본 정보 수정
        elderlyPS.updateElderlyInfo(elderlyModifyReqDto);

        //어시스턴트 이름 수정
        if(elderlyModifyReqDto.getAssistantName() != null) {
            Assistant assistantPS = elderlyPS.getAssistant();
            assistantPS.modifyAssistantName(elderlyModifyReqDto.getAssistantName());
        }

        return new ElderlyModifyRespDto(elderlyPS);
    }

    //요양사 화면에서 노인 프로필 사진 수정
    //@Transactional
    public ElderlyImgModifyRespDto modifyElderlyImg(MultipartFile file, Long elderlyId){

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        if (file == null || file.isEmpty()) {
            return new ElderlyImgModifyRespDto(elderlyPS.getImgUrl());
        }

        String newImgUrl = null;
        String oldImgUrl = elderlyPS.getImgUrl();

        try {
            // 이미지 있다면 -> 새 이미지 업로드
            if(!file.isEmpty())
                newImgUrl = s3Service.uploadImage(file);

            elderlyPS.updateImg(newImgUrl);

            // 기존 이미지 삭제
            if (oldImgUrl != null && !oldImgUrl.isEmpty()) {
                s3Service.deleteImage(oldImgUrl);
            }
            return new ElderlyImgModifyRespDto(newImgUrl);
        } catch (Exception e) {
            // 업로드 실패 시 새로 업로드된 이미지 삭제 (있다면)
            if (newImgUrl != null) {
                try {
                    s3Service.deleteImage(newImgUrl);
                } catch (Exception ignored) {
                    log.error("S3에 올라간 업데이트된 이미지 삭제 작업 실패");
                }
            }
            log.error("이미지 업데이트 중 오류 발생");
            throw new CustomApiException("이미지 업데이트 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //보고서 날짜 수정 -> 날짜를 수정한 localDateTime을 보고서의 startDate으로 삼아서 빈 보고서 생성
    //@Transactional
    public ElderlyReportDayModifyRespDto modifyReportDay(Long elderlyId, ElderlyReportDayModifyReqDto elderlyReportDayModifyReqDto){
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        String reportDay = elderlyReportDayModifyReqDto.getReportDay();

        elderlyPS.modifyReportDay(DayOfWeek.valueOf(reportDay));

        //TODO 생성된 주간 보고서와 월간 보고서가 있는지 확인
        //보고서 날짜 수정 전에 기록을 위해 생성된 빈 보고서가 있는지 확인
        Optional<Report> reportOP = reportRepository.findLatestByElderly(elderlyPS);


        if(reportOP.isEmpty()){ //비어 있으면, 즉 최초로 보고서 생성 날을 지정한 경우이면 바로 보고서 생성
            Report weeklyreport = Report.builder()
                    .reportStatus(ReportStatus.PENDING)
                    .elderly(elderlyPS)
                    .reportType(ReportType.WEEKLY)
                    .startDate(LocalDateTime.now())
                    .reportDay(DayOfWeek.valueOf(reportDay))
                    .build();

            Report monthlyreport = Report.builder()
                    .reportStatus(ReportStatus.PENDING)
                    .elderly(elderlyPS)
                    .reportType(ReportType.WEEKLY)
                    .startDate(LocalDateTime.now())
                    .reportDay(DayOfWeek.valueOf(reportDay))
                    .build();

            reportRepository.save(weeklyreport);
            reportRepository.save(monthlyreport);
        }
        else{ //보고서가 있다면, 시작 날짜와 발행 요일을 현재 시각과 reportDay로 수정

            //TODO DB에서 PENDING 상태의 보고서 끌고와서 수정

            //reportOP.get().modifyStartDateAndReportDay(LocalDateTime.now(), DayOfWeek.valueOf(reportDay));
        }
        return new ElderlyReportDayModifyRespDto(elderlyPS);
    }

    //TODO 노인 사용자 삭제 -> 보고서도 삭제 해야함...? (보고서 삭제 할 것 일단,..)
    //@Transactional
    public ElderlyDeleteRespDto deleteElderly(Long elderlyId, String username) throws MalformedURLException {

        Caregiver caregiverPS = careGiverRepository.findByUsernameWithElderlys(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        String elderlyName = elderlyPS.getName();

        if(elderlyPS.getCaregiver() != caregiverPS) {
            log.error("요양사 {}: 비관리 대상인 노인 {} 삭제 작업 시도", caregiverPS.getId(), elderlyId);
            throw new CustomApiException("관리 대상이 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);
        }


        // S3에서 프로필 사진 삭제
        deleteElderlyProfileImage(elderlyPS);

        // 보고서 삭제
        deleteElderlyReports(elderlyPS);

        // 메시지 내역 삭제
        deleteElderlyMessages(elderlyPS);

        // 긴급 알림 내역 삭제
        deleteElderlyEmergencyRequests(elderlyPS, caregiverPS);

        // AI 어시스턴트 삭제
        //deleteElderlyAssistant(elderlyPS, caregiverPS);
        if(elderlyPS.getAssistant() != null) {
            log.info("노인 {}: AI 어시스턴트 삭제 작업 시작", elderlyPS.getId());
            Caregiver caregiverWithAssistants = careGiverRepository.findCaregiverWithAssistants(caregiverPS.getId());//컬렉션 지연 로딩 프록시 문제로 인해 페치 조인으로 assistantList 한번에 끌고옴
            Assistant assistantPS = elderlyPS.getAssistant();
            caregiverWithAssistants.removeAssistant(assistantPS); //컬렉션 초기화된 caregiver 객체를 사용해 삭제
            assistantRepository.delete(assistantPS);
            log.info("노인 {}: 삭제된 AI 어시스턴트 id {}", elderlyPS.getId(), assistantPS.getId());
        }

        // 노인 삭제
        caregiverPS.removeElderly(elderlyPS);
        elderlyRepository.delete(elderlyPS);

//        //s3에 있는 노인 프로필 사진 삭제
//        String imgUrl = elderlyPS.getImgUrl();
//        if(imgUrl != null && !imgUrl.isEmpty()) {
//            log.info("노인 {}: 프로필 사진 삭제 작업 시작", elderlyId);
//            s3Service.deleteImage(imgUrl);
//            log.info("노인 {}: 프로필 사진 {} 삭제 완료", elderlyId, imgUrl);
//        }
//
//        log.info("노인 {}: 보고서 리스트 삭제 작업 시작", elderlyId);
//        //TODO 노인 보고서 삭제하기 (양방향: orphanRemoval=true 동작)
//        List<Report> reportList = reportRepository.findByElderly(elderlyPS);
//        int deletedWeeklyReportCnt = 0, deletedMonthlyReportCnt=0;
//        for (Report report : reportList) {
//            if(report.getReportType().equals(ReportType.WEEKLY)) {
//                elderlyPS.getWeeklyReports().remove(report);
//                deletedWeeklyReportCnt++;
//                //log.info("노인 {}: 주간 보고서 id {} 삭제 완료", elderlyId, report.getId());
//            }
//            else if(report.getReportType().equals(ReportType.MONTHLY)){
//                elderlyPS.getMonthlyReports().remove(report);
//                deletedMonthlyReportCnt++;
//                //log.info("노인 {}: 월간 보고서 id {} 삭제 완료", elderlyId, report.getId());
//            }
//        }
//        reportRepository.deleteAllByElderly(elderlyPS);
//        log.info("노인 {}: 삭제된 주간 보고서 개수 {}", elderlyId, deletedWeeklyReportCnt);
//        log.info("노인 {}: 삭제된 월간 보고서 개수 {}", elderlyId, deletedMonthlyReportCnt);
//
//        //TODO 노인 대화 기록 삭제하기(단방향)
//        log.info("노인 {}: 메시지 내역 삭제 작업 시작", elderlyId);
//        int deletedMessagesCnt = messageRepository.deleteAllByElderly(elderlyPS);
//        log.info("노인 {}: 삭제된 메시지 개수 {}", elderlyId, deletedMessagesCnt);
//
//        //TODO 노인 긴급 알람 내역 삭제하기(양방향: orphanRemoval=true 동작)
//        //TODO 아예 물리적으로 삭제할까 아니면 논리적 삭제해서 기록은 놔둘지
//        log.info("노인 {}: 긴급 알림 내역 삭제 작업 시작", elderlyId);
//        List<EmergencyRequest> emergencyRequestList = emergencyRequestRepository.findByElderly(elderlyPS);
//        if(!emergencyRequestList.isEmpty()) {
//            Caregiver caregiverWithEmergencyRequests = careGiverRepository.findCaregiverWithEmergencyRequests(caregiverPS.getId());
//            caregiverWithEmergencyRequests.getEmergencyRequestList().removeAll(emergencyRequestList);
//            emergencyRequestRepository.deleteAllByElderly(elderlyPS);
//        }
//        log.info("노인 {}: 삭제된 긴급 알림 내역 개수 {}", elderlyId, emergencyRequestList.size());
//
//
//        //TODO 노인 어시스턴트 삭제 & 요양사와의 연결 끊기 (양방향: orphanRemoval=true 동작)
//        if(elderlyPS.getAssistant()!=null) {
//            log.info("노인 {}: AI 어시스턴트 삭제 작업 시작", elderlyId);
//            Caregiver caregiverWithAssistants = careGiverRepository.findCaregiverWithAssistants(caregiverPS.getId());//컬렉션 지연 로딩 프록시 문제로 인해 페치 조인으로 assistantList 한번에 끌고옴
//            Assistant assistantPS = elderlyPS.getAssistant();
//            caregiverWithAssistants.removeAssistant(assistantPS); //컬렉션 초기화된 caregiver 객체를 사용해 삭제
//            assistantRepository.delete(assistantPS);
//            log.info("노인 {}: 삭제된 AI 어시스턴트 id {}", elderlyId, assistantPS.getId());
//        }
//
//        //TODO 노인 삭제(양방향: orphanRemoval=true 동작)
//        log.info("노인 {}: 노인 삭제 작업 시작", elderlyId);
//        Caregiver caregiverWithElderlys = careGiverRepository.findCaregiverWithElderlys(caregiverPS.getId());//컬렉션 지연 로딩 프록시 문제로 인해 페치 조인으로 elderlyList 한번에 끌고옴
//        caregiverWithElderlys.removeElderly(elderlyPS);
//        elderlyRepository.delete(elderlyPS);
//        log.info("노인 {}: 요양사 id {} 관리 대상에서 삭제 완료", elderlyId, caregiverPS.getId());


        log.info("노인 {}: 성공적으로 삭제되었습니다", elderlyId);
        return new ElderlyDeleteRespDto(elderlyName);
    }
    private void deleteElderlyProfileImage(Elderly elderly) throws MalformedURLException {
        String imgUrl = elderly.getImgUrl();
        if(imgUrl != null && !imgUrl.isEmpty()) {
            log.info("노인 {}: 프로필 사진 삭제 작업 시작", elderly.getId());
            s3Service.deleteImage(imgUrl);
            log.info("노인 {}: 프로필 사진 {} 삭제 완료", elderly.getId(), imgUrl);
        }
    }

    private void deleteElderlyReports(Elderly elderly) {
        log.info("노인 {}: 보고서 삭제 작업 시작", elderly.getId());
        int deletedReportCount = reportRepository.deleteAllByElderly(elderly);
        log.info("노인 {}: 삭제된 보고서 개수 {}", elderly.getId(), deletedReportCount);
    }

    private void deleteElderlyMessages(Elderly elderly) {
        log.info("노인 {}: 메시지 내역 삭제 작업 시작", elderly.getId());
        int deletedMessageCount = messageRepository.deleteAllByElderly(elderly);
        log.info("노인 {}: 삭제된 메시지 개수 {}", elderly.getId(), deletedMessageCount);
    }

    private void deleteElderlyEmergencyRequests(Elderly elderly, Caregiver caregiver) {
        log.info("노인 {}: 긴급 알림 내역 삭제 작업 시작", elderly.getId());
        int deletedEmergencyRequestCount = emergencyRequestRepository.deleteAllByElderly(elderly);
        log.info("노인 {}: 삭제된 긴급 알림 내역 개수 {}", elderly.getId(), deletedEmergencyRequestCount);
    }

    private void deleteElderlyAssistant(Elderly elderly, Caregiver caregiver) {
        if(elderly.getAssistant() != null) {
            log.info("노인 {}: AI 어시스턴트 삭제 작업 시작", elderly.getId());
            Assistant assistant = elderly.getAssistant();

            Assistant assistantPS = caregiver.getAssistantList().stream()
                    .filter(ai -> ai.getId().equals(assistant.getId()))
                    .findFirst()
                    .get();

            caregiver.removeAssistant(assistantPS);
            assistantRepository.delete(assistantPS);
            log.info("노인 {}: 삭제된 AI 어시스턴트 id {}", elderly.getId(), assistantPS.getId());
        }
    }

    private void deleteElderlyFromCaregiver(Elderly elderly, Caregiver caregiver) {
        log.info("노인 {}: 노인 삭제 작업 시작", elderly.getId());
        caregiver.getElderlyList().size();
        caregiver.removeElderly(elderly);
        elderlyRepository.delete(elderly);
        log.info("노인 {}: 요양사 id {} 관리 대상에서 삭제 완료", elderly.getId(), caregiver.getId());
    }















}
