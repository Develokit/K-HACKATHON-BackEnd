package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.caregiver.RespDto.JoinRespDto;
import com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.CreateAssistantRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.CustomAccessCodeGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.openAi.assistant.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.*;
import static com.develokit.maeum_ieum.dto.openAi.gpt.RespDto.CreateGptMessageRespDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CareGiverRepository careGiverRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;
    private final OpenAiService openAiService;
    private final ElderlyRepository elderlyRepository;


    private final Logger log = LoggerFactory.getLogger(CaregiverService.class);

    //전체 데이터 서버에 던지기 전에 아이디 중복인지 검사
    public CaregiverDuplicatedRespDto validateDuplicatedCaregiver(String username){
        boolean isExist = careGiverRepository.findByUsername(username).isPresent();
        if (isExist) {
            return new CaregiverDuplicatedRespDto(username, true);
        }
        return new CaregiverDuplicatedRespDto(username, false);

    }


    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto){ //회원가입

        //아이디 중복 검사
        Optional<Caregiver> caregiverOP = careGiverRepository.findByUsername(joinReqDto.getUsername());
        if(caregiverOP.isPresent()) throw new CustomApiException("이미 존재하는 아이디 입니다", HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT);

        //이미지 저장
        String imgUrl = null;
        if(joinReqDto.getImg() != null)
            imgUrl = s3Service.uploadImage(joinReqDto.getImg());

        //비밀번호 인코딩
        Caregiver caregiverPS = careGiverRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder, imgUrl));

        //DTO 반환
        return new JoinRespDto(caregiverPS);
    }



    public MyInfoRespDto caregiverInfo(String username){//내 정보(이름, 이미지, 성별, 생년월일, 주거지, 소속기관, 연락처)
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        return new MyInfoRespDto(caregiverPS);
    }

    //홈 화면 (요양사 이름, 이미지, 총 관리 인원, 기관, 노인 사용자(이름, 나이, 주소, 연락처,  마지막 대화(n시간 전)), AI 생성 여부
    public CaregiverMainRespDto getCaregiverMainInfo(String username, String cursor, int limit){

        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        //cursor가 null이면 첫 페이지, 아니면 해당 커서 이후의 데이터를 가져옴 -> 최근 생성된 순으로 반환
        List<Elderly> elderlyList = elderlyRepository
                .findByCaregiverIdAndIdAfter(caregiverPS.getId(),
                        decodeCursor(cursor),
                        PageRequest.of(0, limit + 1, Sort.by("id").descending()));

        String nextCursor = null;
        if(elderlyList.size()>limit){
            nextCursor = encodeCursor(elderlyList.get(limit).getId());
            elderlyList = elderlyList.subList(0, limit);
        }

        return new CaregiverMainRespDto(caregiverPS, elderlyList, nextCursor);
    }


    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes());
    }
    private Long decodeCursor(String cursor) {
        return cursor == null ? 0L : Long.parseLong(new String(Base64.getDecoder().decode(cursor)));
    }

    //마이페이지 -> 수정 (이미지 제외)
    @Transactional
    public CaregiverModifyRespDto modifyCaregiverInfo(String username, CaregiverModifyReqDto caregiverModifyReqDto){

        //요양사 가져오기
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        caregiverPS.updateCaregiverInfo(caregiverModifyReqDto);
        return new CaregiverModifyRespDto(caregiverPS);
    }

    @Transactional
    public CaregiverImgModifyRespDto modifyCaregiverImg(String username, MultipartFile file){

        //요양사 가져오기
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        if (file==null || file.isEmpty()) {
            return new CaregiverImgModifyRespDto(caregiverPS.getImgUrl());
        }

        String newImgUrl = null;
        String oldImgUrl = caregiverPS.getImgUrl();

        try {
            // 이미지 있다면 -> 새 이미지 업로드
            if(!file.isEmpty())
                newImgUrl = s3Service.uploadImage(file);

            caregiverPS.updateImg(newImgUrl);

            // 기존 이미지 삭제
            if (oldImgUrl != null && !oldImgUrl.isEmpty()) {
                s3Service.deleteImage(oldImgUrl);
            }
            return new CaregiverImgModifyRespDto(newImgUrl);
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

    //TODO 노인 기본 정보 페이지 진입
    public ElderlyInfoRespDto getElderlyInfo(Long elderlyId, String username){

        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );


        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        if(!caregiverPS.getElderlyList().contains(elderlyPS))
            throw new CustomApiException("관리 대상이 아닙니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);

        return new ElderlyInfoRespDto(elderlyPS);
    }


    // AI 필수 규칙 자동 생성
    public Mono<AssistantMandatoryRuleRespDto> createAutoMandatoryRule(AssistantMandatoryRuleReqDto assistantMandatoryRuleReqdto){
        return openAiService.createGptMessage(assistantMandatoryRuleReqdto);
    }













}
