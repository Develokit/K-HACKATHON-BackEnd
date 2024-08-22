package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.util.CustomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ElderlyRepository elderlyRepository;

    @Scheduled(cron = "0 0 0 * * MON") //매주 월요일 자정에 주간 보고서 생성
    @Transactional
    public void generateWeeklyReport(){
        LocalDateTime[] weekStartAndEnd = CustomUtil.getWeekStartAndEnd(LocalDateTime.now());
        LocalDateTime startOfWeek = weekStartAndEnd[0];
        LocalDateTime endOfWeek = weekStartAndEnd[1];

        elderlyRepository.findAll()
                .forEach(elderlyPS -> {
                    Report reportPS = reportRepository.save(new Report(elderlyPS, startOfWeek, endOfWeek, false));
                    elderlyPS.getWeeklyReports().add(reportPS);
                });
    }


}
