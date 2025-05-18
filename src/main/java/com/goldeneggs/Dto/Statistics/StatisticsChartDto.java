package com.goldeneggs.Dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsChartDto {
    public List<TimeSeriesPointDto> salesOverTime = new ArrayList<>();
    public List<TimeSeriesPointDto> ordersOverTime = new ArrayList<>();

    public List<DistributionDto> ordersByState = new ArrayList<>();
    public List<DistributionDto> paidVsUnpaid = new ArrayList<>();

    public List<DistributionDto> topCustomers = new ArrayList<>();
    public List<DistributionDto> topProducts = new ArrayList<>();
    public List<DistributionDto> salesByCategory = new ArrayList<>();
    public List<DistributionDto> revenueByProduct = new ArrayList<>();

    public List<DistributionDto> salesComparisonMonth = new ArrayList<>();
    public List<DistributionDto> customerComparison = new ArrayList<>();
}

