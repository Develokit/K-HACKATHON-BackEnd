package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.develokit.maeum_ieum.dto.elderly.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderlyService {

    private final ElderlyRepository elderlyRepository;
    private final CareGiverRepository careGiverRepository;
    private final S3Service s3Service;

    //노인 등록
    @Transactional
    public ElderlyCreateRespDto createElderly(ElderlyCreateReqDto elderlyCreateReqDto, String username){

        //요양사 검증
//        careGiverRepository.findByUsername(caregiver.getUsername())
//                    .orElseThrow(() -> new CustomApiException("등록할 권한이 없습니다"));

        List<Caregiver> all = careGiverRepository.findAll();

        System.out.println("username = " + username);
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




}
