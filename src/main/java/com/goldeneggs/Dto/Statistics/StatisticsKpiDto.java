package com.goldeneggs.Dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsKpiDto {
    public double totalSales;
    public int totalOrders;
    public int paidOrders;
    public int unpaidOrders;
    public double averageTicket;
    public String bestCustomer;
    public String mostSoldProduct;
    public String lastOrderDate;
    public String dayWithLeastSales;
    public int cancelledOrders;
    public double averageTimeOrderToBill; // in hours
}
