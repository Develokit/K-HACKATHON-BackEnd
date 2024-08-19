package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id
    private Long id;
    @ManyToOne
    private Elderly elderly;

    private boolean isMonthly; //월간 보고서이면 true

    private LocalDateTime startDate; // 보고서 기록 시작 일
    private LocalDateTime endDate;// 보고서 기록 종료 일

    @Column(length = 512)
    private String analysisResult; //분석 결과

    private String memo; //메모

    //보고서 지표들

}
