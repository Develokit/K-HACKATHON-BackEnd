package com.develokit.maeum_ieum.domain.emergencyRequest;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class EmergencyRequest extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmergencyType emergencyType; //긴급 요청 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private Elderly elderly; //노인 사용자


    private String message; //최종 반환 알림 메시지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver; //요양사 사용자

    //private boolean isRead; //읽었는지

    @Builder
    public EmergencyRequest(Long id, EmergencyType emergencyType, Elderly elderly, Caregiver caregiver) {
        this.emergencyType = emergencyType;
        this.elderly = elderly;
        this.message = EmergencyRequest.createMessage(emergencyType, elderly);
        this.caregiver = caregiver;
        this.id = id;
        caregiver.getEmergencyRequestList().add(this);
    }
    public static String createMessage(EmergencyType emergencyType, Elderly elderly){
        if(emergencyType.equals(EmergencyType.CAREGIVER_NOTIFY))
            return elderly.getName()+" 어르신으로부터 "+EmergencyType.CAREGIVER_NOTIFY.getSymbol()+"이 왔습니다";
        return "정의되지 않은 알람입니다";
    }

//    public void markAsRead(){ //해당 알림 읽었을 시 체크
//        this.isRead = true;
//    }
}
