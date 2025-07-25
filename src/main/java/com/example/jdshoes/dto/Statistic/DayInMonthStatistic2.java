package com.example.jdshoes.dto.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class DayInMonthStatistic2 {
    private String date;
    private BigDecimal revenue;
}
