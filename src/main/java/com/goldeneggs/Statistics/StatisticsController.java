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
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Returns general business statistics.
     */
    @GetMapping("/general")
    public ResponseEntity<StatisticsResponseDto> getGeneralStatistics() {
        return ResponseEntity.ok(statisticsService.getGeneralStatistics());
    }
}
