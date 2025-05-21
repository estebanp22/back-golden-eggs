package com.goldeneggs.Report;

import com.goldeneggs.Exception.InvalidReportDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportValidator reportValidator;

    private Report report;

    @BeforeEach
    void setUp() {
        report = new Report();
        report.setId(1L);
        report.setType("VENTAS");
        report.setDateReport(new java.sql.Date(System.currentTimeMillis()));
        report.setContent("This is a test report");
    }

    @Test
    void testSave_ValidData() {
        // Simula que los datos del reporte son válidos
        Report savedReport = new Report();
        savedReport.setId(1L);
        savedReport.setType(report.getType());
        savedReport.setDateReport(report.getDateReport());
        savedReport.setContent(report.getContent());

        when(reportRepository.save(report)).thenReturn(savedReport);

        Report result = reportService.save(report);

        assertEquals(report.getType(), result.getType());
        assertEquals(report.getContent(), result.getContent());
        verify(reportRepository).save(report);
    }

    @Test
    void testSave_InvalidType() {
        report.setType("INVALIDO");
        assertThrows(InvalidReportDataException.class, () -> reportService.save(report));
    }

    @Test
    void testSave_InvalidDate() {
        report.setDateReport(null);
        assertThrows(InvalidReportDataException.class, () -> reportService.save(report));
    }

    @Test
    void testSave_InvalidContent() {
        report.setContent("");
        assertThrows(InvalidReportDataException.class, () -> reportService.save(report));
    }

    @Test
    void testFindAll() {
        List<Report> reports = Arrays.asList(report);
        when(reportRepository.findAll()).thenReturn(reports);

        List<Report> result = reportService.findAll();

        assertEquals(1, result.size());
        assertEquals("VENTAS", result.get(0).getType());
        verify(reportRepository).findAll();
    }

    @Test
    void testGet_ExistingId() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        Report found = reportService.get(1L);

        assertNotNull(found);
        assertEquals("VENTAS", found.getType());
        verify(reportRepository).findById(1L);
    }

    @Test
    void testGet_NotFound() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.get(99L));
    }

    @Test
    void testUpdate_ExistingId() {
        Report updatedData = new Report();
        updatedData.setType("DIARIO");
        updatedData.setDateReport(new Date(System.currentTimeMillis()));
        updatedData.setContent("Nuevo contenido");

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        Report updated = reportService.update(1L, updatedData);

        assertEquals("DIARIO", updated.getType());
        assertEquals("Nuevo contenido", updated.getContent());
        verify(reportRepository).save(report);
    }

    @Test
    void testUpdate_NotFound() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        Report updatedData = new Report();
        assertThrows(ResourceNotFoundException.class, () -> reportService.update(99L, updatedData));
    }

    @Test
    void testDelete_ExistingId() {
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.delete(1L);

        verify(reportRepository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(reportRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reportService.delete(99L));
    }
}
