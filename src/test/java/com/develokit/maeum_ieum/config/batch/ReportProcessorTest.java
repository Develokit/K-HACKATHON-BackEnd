package com.develokit.maeum_ieum.config.batch;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.service.report.ReportProcessor;
import com.develokit.maeum_ieum.service.report.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportProcessorTest {

    @InjectMocks
    private ReportProcessor reportProcessor;

    @Mock
    private ReportService reportService;

    @Mock
    private Logger log;

    @Test
    void testProcess_Successful() throws Exception {
        // given
        Report report = mock(Report.class);
        Elderly elderly = mock(Elderly.class);

        Report report2 = mock(Report.class);
        Elderly elderly2 = mock(Elderly.class);

        when(elderly.getId()).thenReturn(1L);
        when(report.getElderly()).thenReturn(elderly);

        when(elderly2.getId()).thenReturn(2L);
        when(report2.getElderly()).thenReturn(elderly2);

        // when
        Report result = reportProcessor.process(report);
        Report result2 = reportProcessor.process(report2);

        // then
        verify(report, times(1)).updateReportStatus(ReportStatus.PROCESSING);
        verify(reportService, times(1)).generateReportContent(report);
        verify(report, times(1)).updateReportStatus(ReportStatus.COMPLETED);
        assertNotNull(result);

        verify(report2, times(1)).updateReportStatus(ReportStatus.PROCESSING);
        verify(reportService, times(1)).generateReportContent(report2);
        verify(report2, times(1)).updateReportStatus(ReportStatus.COMPLETED);
        assertNotNull(result2);
    }

    @Test
    void testProcess_WithError() throws Exception {
        // given
        Report report = mock(Report.class);
        Elderly elderly = mock(Elderly.class);

        when(elderly.getId()).thenReturn(1L);
        when(report.getElderly()).thenReturn(elderly);
        doThrow(new RuntimeException("Some error")).when(reportService).generateReportContent(report);

        // when
        Exception exception = assertThrows(CustomApiException.class, () -> reportProcessor.process(report));

        // then
        verify(report, times(1)).updateReportStatus(ReportStatus.PROCESSING);
        verify(reportService, times(1)).generateReportContent(report);
        verify(report, times(1)).updateReportStatus(ReportStatus.ERROR);
        assertEquals("보고서 배치 처리 중 오류 발생", exception.getMessage());
    }

}