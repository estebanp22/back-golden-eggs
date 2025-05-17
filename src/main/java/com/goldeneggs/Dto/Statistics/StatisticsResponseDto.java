package com.goldeneggs.Dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponseDto {
    public StatisticsKpiDto kpis;
    public StatisticsChartDto charts;
}