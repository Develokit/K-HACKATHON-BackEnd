package com.develokit.maeum_ieum.service;

import com.amazonaws.services.s3.transfer.internal.CompleteMultipartCopy;
import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageRepository;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.develokit.maeum_ieum.dto.elderly.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.dto.openAi.audio.ReqDto.AudioRequestDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.ContentDto;
import com.develokit.maeum_ieum.dto.openAi.message.ReqDto.CreateMessageReqDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.ListMessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.message.RespDto.MessageRespDto;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto.CreateRunReqDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Transactional(readOnly = true)
public class ElderlyService {

    private final ElderlyRepository elderlyRepository;
    private final CareGiverRepository careGiverRepository;
    private final S3Service s3Service;
    private final AssistantRepository assistantRepository;
    private final OpenAiService openAiService;
    private final ReportRepository reportRepository;
    private final MessageRepository messageRepository;
    private final Logger log = LoggerFactory.getLogger(CaregiverService.class);

    //노인 등록
    @Transactional
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


    @Transactional
    //채팅 화면 들어가기: 어시스턴트 이름 + 이전 대화 기록 -> 우선 어시스턴트 이름과 openAiAssistant아이디 반환
    public CheckAssistantInfoRespDto checkAssistantInfo(Long elderlyId, Long assistantId){
        //어시스턴트 검증
        Assistant assistantPS = assistantRepository.findById(assistantId)
                .orElseThrow(
                        () -> new CustomApiException("등록된 AI 어시스턴트가 없습니다",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
                );

        //사용자 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.",HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //스레드 있는지 확인
        String threadId = "";
        //스레드가 있는지 확인 -> 없으면 스레드 생성
        if(assistantPS.hasThread()){ //스레드 있음
            threadId = assistantPS.getThreadId();
        }else{ //스레드 없음 -> 생성 후 db에 스레드 아이디 저장
            ThreadRespDto threadRespDto = openAiService.createThread();
            threadId = threadRespDto.getId();
            assistantPS.attachThread(threadId);
        }

        //이전 대화 기록 -> 마지막 대화 시간 정보가 있으면 이전 대화 기록 끌고오기
        List<Message> messageList = messageRepository.findByElderlyOrderByCreatedDate(elderlyPS);

        return new CheckAssistantInfoRespDto(assistantPS, messageList);
    }


    @Transactional
    public void updateLastChatDate(Long elderlyId){
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        elderlyPS.updateLastChatDate(LocalDateTime.now());
    }

    //TODO 요양사 화면에서 노인 기본 정보 수정 - 이미지 제외
    @Transactional
    public ElderlyModifyRespDto modifyElderlyInfo(ElderlyModifyReqDto elderlyModifyReqDto, Long elderlyId, String username){

        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //해당 요양사가 관리하는 노인 사용자가 맞는지 검증
        if(!caregiverPS.getElderlyList().contains(elderlyPS))
            throw new CustomApiException("해당 사용자의 관리 대상이 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);


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
    @Transactional
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


    //보고서 날짜 수정
    @Transactional
    public ElderlyReportDayModifyRespDto modifyReportDay(Long elderlyId, ElderlyReportDayModifyReqDto elderlyReportDayModifyReqDto){
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        String reportDay = elderlyReportDayModifyReqDto.getReportDay();

        elderlyPS.modifyReportDay(DayOfWeek.valueOf(reportDay));

        return new ElderlyReportDayModifyRespDto(elderlyPS);
    }















}
