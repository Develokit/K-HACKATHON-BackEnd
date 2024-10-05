package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ProcessedReport{
    private Report report;
    private boolean isAnalyzed;
}