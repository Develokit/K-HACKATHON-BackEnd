package com.develokit.maeum_ieum.domain.report;

import com.amazonaws.services.ec2.model.CreateDhcpOptionsRequest;
import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Report extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private Elderly elderly;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType; //월간 보고서/주간 보고서

    private LocalDateTime startDate; // 보고서 기록 시작 일
    private LocalDateTime endDate;// 보고서 기록 종료 일

    @Column(length = 2048)
    private String analysisResult; //분석 결과

    private String memo; //메모

    /*


    보고서 지표들



     */
    public Report(Elderly elderly, LocalDateTime startDate, LocalDateTime endDate, ReportType reportType){
        this.elderly = elderly;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportType = reportType;
    }

}
