package com.goldeneggs.Dto.Statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesPointDto {
    public String name; // e.g. "2025-05-17" or "Week 20"
    public double sales;
    public int orders;
}
