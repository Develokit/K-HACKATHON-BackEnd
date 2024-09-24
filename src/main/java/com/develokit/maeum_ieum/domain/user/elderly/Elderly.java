package com.develokit.maeum_ieum.domain.user.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.User;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.elderly.ReqDto;
import com.develokit.maeum_ieum.service.ElderlyService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.develokit.maeum_ieum.dto.elderly.ReqDto.*;

@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@NoArgsConstructor
public class Elderly extends User {

    @Id
    @Column(name = "elderly_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String healthInfo; // 특이 사항
    private String homeAddress; //주거지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_id")
    private Assistant assistant;

    @Column(length = 5)
    private String accessCode;

    @Embedded
    private EmergencyContactInfo emergencyContactInfo;


    private LocalDateTime lastChatTime; //마지막 대화 날짜

    @OneToMany(mappedBy = "elderly", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Report> weeklyReports = new ArrayList<>(); //주간 보고서

    @OneToMany(mappedBy = "elderly", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Report> monthlyReports = new ArrayList<>(); //월간 보고서

    //TODO -> 프론트에서 추가하는 화면 구현하면 연결할것
    @Enumerated(EnumType.STRING)
    private DayOfWeek reportDay;

    public boolean hasAssistant(){
        if(assistant == null) return false;
        return true;
    }
    public void attachAssistant(Assistant assistant){
        this.assistant = assistant;
    }
    public void attachAccessCode(String accessCode){
        this.accessCode = accessCode;
    }

    public void updateLastChatDate(LocalDateTime lastChatTime){
        this.lastChatTime = lastChatTime;
    }
    @Builder
    public Elderly(Long id, String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization, String healthInfo, String homeAddress, Caregiver caregiver, Assistant assistant, EmergencyContactInfo emergencyContactInfo, Role role) {
        super(name, contact, gender, imgUrl, birthDate, organization, role);
        this.healthInfo = healthInfo;
        this.homeAddress = homeAddress;
        this.caregiver = caregiver;
        this.assistant = assistant;
        this.emergencyContactInfo = emergencyContactInfo;
        this.id = id;
        caregiver.getElderlyList().add(this);
    }

    public void updateElderlyInfo(ElderlyModifyReqDto elderlyModifyReqDto){
        super.updateCommonInfo(elderlyModifyReqDto.getName(),
                elderlyModifyReqDto.getGender(),
                elderlyModifyReqDto.getBirthDate(), elderlyModifyReqDto.getContact());

        if(elderlyModifyReqDto.getHomeAddress() != null)
            this.homeAddress = elderlyModifyReqDto.getHomeAddress();
        if(elderlyModifyReqDto.getHealthInfo() != null)
            this.healthInfo = elderlyModifyReqDto.getHealthInfo();
        if(elderlyModifyReqDto.getEmergencyName() != null)
            this.emergencyContactInfo.emergencyName = elderlyModifyReqDto.getEmergencyName();
        if(elderlyModifyReqDto.getEmergencyContact() != null)
            this.emergencyContactInfo.emergencyContact = elderlyModifyReqDto.getEmergencyContact();
    }
    public void updateImg(String imgUrl){
        super.updateImg(imgUrl);
    }

    public void modifyReportDay(DayOfWeek dayOfWeek){
        this.reportDay = dayOfWeek;
    }

    public void deleteAssistant(){
        this.assistant = null;
    }
    public void deleteAccessCode(){
        this.accessCode = null;
    }
}
