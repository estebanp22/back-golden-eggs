package com.goldeneggs.Report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.InvalidReportDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private ObjectMapper objectMapper;
    private Report report;

    @BeforeEach
    void  setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
        objectMapper = new ObjectMapper();

        report = new Report();
        report.setId(1L);
        report.setType("VENTAS");
        report.setDateReport(new java.sql.Date(System.currentTimeMillis()));
        report.setContent("This is a test report");
    }

    @Test
    void testCreateReportSuccess() throws Exception {
        when(reportService.save(any(Report.class))).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(report.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(report.getType()));
    }

    @Test
    void testCreateReportInvalid() throws Exception {
        when(reportService.save(any(Report.class))).thenThrow(new InvalidReportDataException("Tipo de reporte no válido"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Tipo de reporte no válido"));
    }

    @Test
    void testGetAllReports() throws Exception {
        when(reportService.findAll()).thenReturn(Arrays.asList(report));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/getAll"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(report.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type").value(report.getType()));
    }

    @Test
    void testGetReportByIdFound() throws Exception {
        when(reportService.get(1L)).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/get/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void testGetReportByIdNotFound() throws Exception {
        when(reportService.get(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/get/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateReportSuccess() throws Exception {
        when(reportService.update(Mockito.eq(1L), any(Report.class))).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/reports/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(report.getId()));
    }

    @Test
    void testUpdateReportNotFound() throws Exception {
        when(reportService.update(Mockito.eq(1L), any(Report.class)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/reports/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Not found"));
    }

    @Test
    void testUpdateReportInvalid() throws Exception {
        when(reportService.update(Mockito.eq(1L), any(Report.class)))
                .thenThrow(new InvalidPayDataException("Datos inválidos"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/reports/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Datos inválidos"));
    }

    @Test
    void testDeleteReportSuccess() throws Exception {
        doNothing().when(reportService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/reports/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testDeleteReportNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Not found")).when(reportService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/reports/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
