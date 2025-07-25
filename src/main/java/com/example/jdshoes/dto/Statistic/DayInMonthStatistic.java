package com.example.jdshoes.dto.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayInMonthStatistic {
    private String date;
    private Double revenue;

}
