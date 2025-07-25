package com.example.jdshoes.dto.Statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatisticMonth {

    private Integer month1;

    private Integer month2;

    private Integer year1;

    private Integer year2;

    private Long amount1;

    private Long amount2;
}
