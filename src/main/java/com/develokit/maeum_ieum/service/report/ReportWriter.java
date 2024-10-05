package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReportWriter implements ItemWriter<ProcessedReport> {

    private final ReportRepository reportRepository;

    @Override
    public void write(Chunk<? extends ProcessedReport> chunk) throws Exception {
        for (ProcessedReport processedReport : chunk.getItems()) {
            if(processedReport.isAnalyzed()){
                reportRepository.save(processedReport.getReport());
            }else{
                reportRepository.delete(processedReport.getReport());
            }
        }
    }
}
