package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //Mokito 테스트
class CaregiverServiceTest {

    @InjectMocks
    private CaregiverService caregiverService;
    @InjectMocks
    private ObjectMapper om;

    @Mock
    private CareGiverRepository careGiverRepository;
    @Mock
    private ElderlyRepository elderlyRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void join_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setName("userA");
        joinReqDto.setContact("010-1234-5678");
        joinReqDto.setGender(Gender.FEMALE.toString());
        joinReqDto.setPassword("1234");
        joinReqDto.setUsername("user1111");
        joinReqDto.setImg(new MockMultipartFile("imgFile", new byte[0]));


        //when
        when(careGiverRepository.findByUsername(joinReqDto.getUsername())).thenReturn(Optional.empty());
        when(s3Service.uploadImage(joinReqDto.getImg())).thenReturn("imgUrl");
        when(bCryptPasswordEncoder.encode(joinReqDto.getPassword())).thenReturn("비밀번호 인코딩");


        Caregiver caregiver = Caregiver.builder()
                .username(joinReqDto.getUsername())
                .build();


        when(careGiverRepository.save(any())).thenReturn(caregiver);

        JoinRespDto joinRespDto = caregiverService.join(joinReqDto);
        String response = om.writeValueAsString(joinRespDto);

        //then
        assertNotNull(joinRespDto);
        assertThat(joinRespDto.getUsername()).isEqualTo(joinReqDto.getUsername());

    }


    @Test
    void testGetCaregiverMainInfo_FirstPage() {
        // Given
        String username = "testUser";
        Caregiver caregiver = Caregiver.builder().username(username).build();

        Elderly elderly1 = Elderly.builder().name("1").build();
        Elderly elderly2 = Elderly.builder().name("2").build();
        Elderly elderly3 = Elderly.builder().name("3").build();
        Elderly elderly4 = Elderly.builder().name("4").build();
        Elderly elderly5 = Elderly.builder().name("5").build();
        Elderly elderly6 = Elderly.builder().name("6").build();
        Elderly elderly7 = Elderly.builder().name("7").build();

        List<Elderly> elderlyList = Arrays.asList(
                elderly1,elderly2,elderly3,elderly4,elderly5,elderly6,elderly7
        );

        when(careGiverRepository.findByUsername(username)).thenReturn(Optional.of(caregiver));
        when(elderlyRepository.findByCaregiverIdAndIdAfter(eq(1L), eq(0L), any(PageRequest.class)))
                .thenReturn(elderlyList);

        // When
        CaregiverMainRespDto result = caregiverService.getCaregiverMainInfo(username, null, 5);
        result.getElderlyInfoDto().stream()
                .map(e -> e.getName())
                        .forEach(e -> System.out.println("e = " + e));

        // Then
        assertNotNull(result);
        assertEquals(5, result.getElderlyInfoDto().size());
        assertNotNull(result.getNextCursor());
    }



}