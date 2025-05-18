package com.goldeneggs.Statistics;


import com.goldeneggs.Dto.Statistics.StatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling statistical data endpoints.
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@CrossOrigin("*")

public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Handles the HTTP GET request to retrieve general statistical data.
     * The statistics include key performance indicators (KPIs) such as total sales,
     * total orders, paid and unpaid orders, average ticket size, and more.
     * Additionally, it provides chart data to analyze various statistical trends.
     *
     * @return a {@code ResponseEntity} containing a {@code StatisticsResponseDto}
     *         object with the calculated general statistics and chart data.
     */
    @GetMapping("/general")
    public ResponseEntity<StatisticsResponseDto> getGeneralStatistics() {
        return ResponseEntity.ok(statisticsService.getGeneralStatistics());
    }
}
