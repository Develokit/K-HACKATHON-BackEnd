package com.develokit.maeum_ieum.config.jwt;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.elderly.ReqDto;
import com.develokit.maeum_ieum.dto.elderly.ReqDto.ElderlyCreateReqDto;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc //mock 환경에 mvc가 new돼서 들어감
@ActiveProfiles("dev")
public class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired private CareGiverRepository careGiverRepository;

    @Test
    void authorization_success_test() throws Exception{
        //given
        Caregiver caregiver = Caregiver.builder().username("yaewon0411").role(Role.ADMIN).build();
        LoginUser loginUser = new LoginUser(caregiver);
        String token = JwtProvider.create(loginUser);
        System.out.println("token = " + token);


        ElderlyCreateReqDto elderlyCreateReqDto = ElderlyCreateReqDto.builder()
                .name("userA")
                .birthDate(LocalDate.now().toString())
                .gender("FEMALE")
                .img(null)
                .contact("010-8840-1231")
                .homeAddress("제주시 연동")
                .build();

        String requestBody = om.writeValueAsString(elderlyCreateReqDto);


        //when
        ResultActions resultActions = mvc.perform(post("/caregivers/elderlys")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtVo.HEADER, token));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();


        System.out.println("responseBody = " + responseBody);


        //then

    }


}