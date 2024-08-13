package com.develokit.maeum_ieum.service;

import com.amazonaws.services.s3.transfer.internal.CompleteMultipartCopy;
import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
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
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    //노인 등록
    @Transactional
    public ElderlyCreateRespDto createElderly(ElderlyCreateReqDto elderlyCreateReqDto, String username){

        //요양사 검증
        Caregiver caregiverPS = careGiverRepository.findByUsername(username)
                .orElseThrow(
                        () -> new CustomApiException("등록 권한이 없습니다")
                );

        //노인 중복 검사 -> 연락처
        Optional<Elderly> elderlyOP = elderlyRepository.findByContact(elderlyCreateReqDto.getContact());
        if(elderlyOP.isPresent()) throw new CustomApiException("이미 등록된 어르신 입니다");

        String imgUrl = null;
        //이미지 저장
        if(elderlyCreateReqDto.getImgFile() != null)
            imgUrl = s3Service.uploadImage(elderlyCreateReqDto.getImgFile());

        //저장
        Elderly elderlyPS = elderlyRepository.save(elderlyCreateReqDto.toEntity(caregiverPS, imgUrl));

        return new ElderlyCreateRespDto(elderlyPS);
    }

    //홈화면(노인): assistant의 pk -> 마지막 대화 시간, 이름, 생년월일(나이), 프로필 img, 요양사 이름, 요양사 프로필, 요양사 저나번호
    public ElderlyMainRespDto getElderlyMainInfo(Long elderlyId, Long assistantId){

        //연결된 노인 사용자 찾기
        Elderly elderlyPS = elderlyRepository.findById(elderlyId)
                .orElseThrow(
                        () -> new CustomApiException("등록되지 않은 사용자 입니다. 담당 요양사에게 문의해주세요")
                );

        //연결된 요양사 찾기
        Caregiver caregiverPS = careGiverRepository.findById(elderlyPS.getCaregiver().getId())
                .orElseThrow(
                        () -> new CustomApiException("해당 전문 요양사가 존재하지 않습니다")
                );

        return new ElderlyMainRespDto(caregiverPS, elderlyPS);
    }


    @Transactional
    //채팅 화면 들어가기: 어시스턴트 이름 + 이전 대화 기록 -> 우선 어시스턴트 이름과 openAiAssistant아이디 반환
    public CheckAssistantInfoRespDto checkAssistantInfo(Long elderlyId, Long assistantId){
        //어시스턴트 검증
        Assistant assistantPS = assistantRepository.findById(assistantId)
                .orElseThrow(
                        () -> new CustomApiException("존재하지 않는 AI Assistant 입니다")
                );

        //사용자 검증
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 사용자입니다. 담당 요양사에게 문의해주세요")
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
        List<MessageRespDto> messageRespDto = null;
        if(elderlyPS.getLastChatTime() != null){
            ListMessageRespDto listMessageRespDto = openAiService.listMessages(threadId);
            messageRespDto = listMessageRespDto.getData();
        }

        return new CheckAssistantInfoRespDto(assistantPS, messageRespDto);
    }


    @Transactional
    public void updateLastChatDate(Long elderlyId){
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다")
        );
        elderlyPS.updateLastChatDate(LocalDateTime.now());
    }











}
