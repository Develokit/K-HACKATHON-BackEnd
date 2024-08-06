package com.develokit.maeum_ieum.domain.assistant;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Assistant extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_ai_assistant_id")
    private String openAiAssistantId; //실제 OpenAI에 등록되는 어시스턴트 아이디
    private String name;

    @Column(length = 5)
    private String accessCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @OneToOne
    @JoinColumn(name = "elderly_id")
    private Elderly elderly;

    private String rule; //필수 규칙
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
    public Assistant(String name, Caregiver caregiver, Elderly elderly, String rule, String conversationTopic, String responseType, String personality, String forbiddenTopic, String openAiAssistantId, String accessCode) {
        this.name = name;
        this.caregiver = caregiver;
        this.elderly = elderly;
        this.rule = rule;
        this.conversationTopic = conversationTopic;
        this.responseType = responseType;
        this.personality = personality;
        this.forbiddenTopic = forbiddenTopic;
        this.openAiAssistantId = openAiAssistantId;
        this.accessCode = accessCode;
        caregiver.getAssistantList().add(this);
    }
}
