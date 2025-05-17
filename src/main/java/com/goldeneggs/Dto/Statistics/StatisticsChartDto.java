package com.goldeneggs.Dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsChartDto {
    public List<TimeSeriesPointDto> salesOverTime;
    public List<TimeSeriesPointDto> ordersOverTime;

    public List<DistributionDto> ordersByState;
    public List<DistributionDto> paidVsUnpaid;

    public List<DistributionDto> topCustomers;
    public List<DistributionDto> topProducts;
    public List<DistributionDto> salesByCategory;
    public List<DistributionDto> revenueByProduct;

    public List<DistributionDto> salesComparisonMonth;
    public List<DistributionDto> customerComparison;
}
