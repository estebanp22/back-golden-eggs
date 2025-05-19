package com.goldeneggs.Statistics;

import com.goldeneggs.Dto.Statistics.StatisticsChartDto;
import com.goldeneggs.Dto.Statistics.StatisticsKpiDto;
import com.goldeneggs.Dto.Statistics.StatisticsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetGeneralStatistics() throws Exception {
        // Preparo un objeto dummy que devuelva el service
        StatisticsResponseDto dummyResponse = new StatisticsResponseDto();
        dummyResponse.kpis = new StatisticsKpiDto();
        dummyResponse.charts = new StatisticsChartDto();

        when(statisticsService.getGeneralStatistics()).thenReturn(dummyResponse);

        mockMvc.perform(get("/api/v1/statistics/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpis").exists())
                .andExpect(jsonPath("$.charts").exists());

        verify(statisticsService, times(1)).getGeneralStatistics();
    }
}

