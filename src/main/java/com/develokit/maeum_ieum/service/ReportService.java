package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.util.CustomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ElderlyRepository elderlyRepository;

    @Scheduled(cron = "0 0 0 * * *") //매일 해당 요일에 보고서 생성을 지정한 사용자의 보고서 생성
    @Transactional
    public void generateWeeklyReport(){
        DayOfWeek today = LocalDateTime.now().getDayOfWeek();
        LocalDateTime[] weekStartAndEnd = CustomUtil.getWeekStartAndEnd(LocalDateTime.now());
        LocalDateTime startOfWeek = weekStartAndEnd[0];
        LocalDateTime endOfWeek = weekStartAndEnd[1];

        //해당 요일에 보고서 생성되도록 지정된 노인 사용자 찾기
        List<Elderly> elderlyList = elderlyRepository.findByReportDay(today);
        List<Report> reportList = new ArrayList<>();

        //보고서 생성 안됐는지 검증하고 보고서 생성하기
        for (Elderly elderly : elderlyList) {

        }


    }

    //보고서 지정된 요일에 주간 보고서 자동 생성 -> 그 요일이 되는 자정에 생성하도록






    //보고서 지정된 요일에 월간 보고서 자동 생성


}
