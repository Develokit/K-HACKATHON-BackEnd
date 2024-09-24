package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.dto.assistant.ReqDto;
import com.develokit.maeum_ieum.dummy.DummyObject;
import com.develokit.maeum_ieum.service.AssistantService;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@AutoConfigureMockMvc
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CaregiverControllerTest extends DummyObject {
    @Autowired private CaregiverService caregiverService;
    @Autowired private CareGiverRepository careGiverRepository;
    @Autowired private AssistantService assistantService;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "user6666")
    void 월간보고서_정량적평가_조회_테스트() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(
                get("/caregivers/elderlys/{elderlyId}/monthly-reports/quantitative/{reportId}", 34L, 71L)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        //then
        System.out.println("responseBody = " + responseBody);

    }

    @Test
    public void join_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setName("userA");
        joinReqDto.setUsername("userA1111");
        joinReqDto.setPassword("userA1111");
        joinReqDto.setBirthDate(LocalDate.now().toString());
        joinReqDto.setImg(null);

        String requestBody = om.writeValueAsString(joinReqDto);

        //when
        ResultActions resultAction = mvc.perform(
                post("/caregivers")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultAction.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
    }
    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "userA3333")
    void 노인생성_테스트() throws Exception{
        //given
        ElderlyCreateReqDto elderlyCreateReqDto = ElderlyCreateReqDto.builder()
                .name("노인 이름")
                .contact("010-1234-1244")
                .healthInfo("건강하시다")
                .build();

        String requestBody = om.writeValueAsString(elderlyCreateReqDto);

        //when
        ResultActions resultActions = mvc.perform(
                multipart("/caregivers/elderlys")
                        .param("name",elderlyCreateReqDto.getName())
                        .param("contact",elderlyCreateReqDto.getContact())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)

        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then

    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "userA3333")
    public void 어시스턴트수정_테스트() throws Exception {
        AssistantModifyReqDto assistantModifyReqDto = new AssistantModifyReqDto();
        assistantModifyReqDto.setName("");
        assistantModifyReqDto.setPersonality("간사이 성격");

        String requestBody = om.writeValueAsString(assistantModifyReqDto);

        //when
        ResultActions resultActions = mvc.perform(
                patch("/caregivers/elderlys/1/assistants/3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
    }
    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "userA3333")
    public void 어시스턴트삭제_테스트() throws Exception{
        //when
        ResultActions resultActions = mvc.perform(
                delete("/caregivers/elderlys/1/assistants/3")
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
    }


}