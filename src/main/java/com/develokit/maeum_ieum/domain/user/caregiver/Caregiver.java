package com.develokit.maeum_ieum.domain.user.caregiver;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.User;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.service.CaregiverService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.service.CaregiverService.*;

@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Caregiver extends User {

    @Id
    @Column(name = "caregiver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(mappedBy = "caregiver", fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<Assistant> assistantList = new ArrayList<>();

    @OneToMany(mappedBy = "caregiver", fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<Elderly> elderlyList = new ArrayList<>();

    @OneToMany(mappedBy = "caregiver", fetch = FetchType.LAZY) //긴급 알림 리스트
    @ToString.Exclude
    private List<EmergencyRequest> emergencyRequestList = new ArrayList<>();

    @Builder
    public Caregiver(String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization, String username, String password, Role role) {
        super(name, contact, gender, imgUrl, birthDate, organization, role);
        this.username = username;
        this.password = password;
    }

    public void updateCaregiverInfo(CaregiverModifyReqDto caregiverModifyReqDto){
        super.updateCommonInfo(caregiverModifyReqDto.getName(), caregiverModifyReqDto.getGender(), caregiverModifyReqDto.getOrganization(),
                 caregiverModifyReqDto.getBirthDate(), caregiverModifyReqDto.getContact());
    }

    public void updateImg(String imgUrl){
        super.updateImg(imgUrl);
    }

    public void removeAssistant(Assistant assistant){
        this.assistantList.remove(assistant);
    }

    public void removeElderly(Elderly elderly){
        this.elderlyList.remove(elderly);
    }


}
