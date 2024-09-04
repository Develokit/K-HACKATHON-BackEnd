package com.develokit.maeum_ieum.domain.assistant;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.dto.assistant.ReqDto;
import com.develokit.maeum_ieum.service.CaregiverService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static com.develokit.maeum_ieum.dto.assistant.ReqDto.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Assistant extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_ai_assistant_id")
    private String openAiAssistantId; //실제 OpenAI에 등록되는 어시스턴트 아이디
    private String name; //어시스턴트 이름

    @Column(length = 5)
    private String accessCode; //접근 코드

    private String openAiInstruction;// openAI에 등록되는 실제 프롬프트

    @Column(length = 512)
    private String mandatoryRule; //AI 필수 규칙

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver;

    @OneToOne
    @JoinColumn(name = "elderly_id")
    private Elderly elderly;

    private String conversationTopic; //대화 주제
    private String responseType; //응답 형식
    private String personality; //성격
    private String forbiddenTopic; //금기 주제

    private String threadId; //openAi 스레드 아이디
    private LocalDateTime threadCreatedDate; //해당 어시스턴트에 스레드가 생성된 날 (30일 이후 파기됨)

    public void attachThread(String threadId){
        this.threadId = threadId;
        threadCreatedDate = LocalDateTime.now();
    }

    public boolean hasThread(){
        if(threadId == null) return false;
        else return true;
    }


    @Builder
    public Assistant(String name, Caregiver caregiver, Elderly elderly, String conversationTopic, String responseType, String personality, String forbiddenTopic, String openAiAssistantId, String accessCode, String openAiInstruction, String mandatoryRule) {
        this.name = name;
        this.caregiver = caregiver;
        this.elderly = elderly;
        this.conversationTopic = conversationTopic;
        this.responseType = responseType;
        this.personality = personality;
        this.forbiddenTopic = forbiddenTopic;
        this.openAiAssistantId = openAiAssistantId;
        this.accessCode = accessCode;
        this.openAiInstruction = openAiInstruction;
        this.mandatoryRule = mandatoryRule;
        caregiver.getAssistantList().add(this);
    }
    public void modifyAssistantName(String assistantName){
        if(assistantName != null)
            this.name = assistantName;
    }
    public void update(AssistantModifyReqDto assistantModifyReqDto){
        if(assistantModifyReqDto.getName()!=null)
            this.name = assistantModifyReqDto.getName();
        if(assistantModifyReqDto.getMandatoryRule() != null)
            this.mandatoryRule = assistantModifyReqDto.getMandatoryRule();
        if(assistantModifyReqDto.getPersonality()!=null)
            this.personality = assistantModifyReqDto.getPersonality();
        if(assistantModifyReqDto.getResponseType()!=null)
            this.responseType = assistantModifyReqDto.getResponseType();
        if(assistantModifyReqDto.getForbiddenTopic() != null)
            this.forbiddenTopic = assistantModifyReqDto.getForbiddenTopic();
        if(assistantModifyReqDto.getConversationTopic() != null)
            this.conversationTopic = assistantModifyReqDto.getConversationTopic();

        String newOpenAiInstructions = "";

        if(this.conversationTopic != null)
            newOpenAiInstructions += "[대화 주제 : 당신은 '"+this.conversationTopic+"'을 주제로 얘기합니다.]\n";
        if(this.forbiddenTopic != null)
            newOpenAiInstructions += "[금기어 및 금기 주제 : '"+this.forbiddenTopic+"']\n";
        if(this.responseType != null)
            newOpenAiInstructions += "[준수 응답 형식 : 당신은 대화할 때 '"+this.responseType+"]\n";
        if(this.personality != null)
            newOpenAiInstructions += "[성격 : 당신의 성격은 '"+this.conversationTopic+"']";

        this.openAiInstruction = newOpenAiInstructions;

    }

}
