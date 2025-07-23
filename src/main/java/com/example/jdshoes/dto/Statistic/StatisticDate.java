package com.example.jdshoes.dto.Statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class StatisticDate {

    private Date date1;

    private Date date2;

    private Long amount1;

    private Long amount2;
}
