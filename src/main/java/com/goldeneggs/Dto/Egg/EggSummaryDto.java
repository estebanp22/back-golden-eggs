package com.goldeneggs.Dto.Egg;

import com.goldeneggs.TypeEgg.TypeEgg;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@AllArgsConstructor
@Data
public class EggSummaryDto {
    private TypeEgg type;
    private String color;
    private double salePrice;
    private Date expirationDate;
}
