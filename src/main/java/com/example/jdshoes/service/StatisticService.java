package com.example.jdshoes.service;


import com.example.jdshoes.dto.Product.ProductStock;
import com.example.jdshoes.dto.Statistic.*;

import java.sql.Date;
import java.util.List;

public interface StatisticService {
    List<DayInMonthStatistic> getDayInMonthStatistic(String month, String year);

    List<MonthInYearStatistic> getMonthInYearStatistic(String year);

    List<MonthInYearStatistic2> getMonthlyRevenue(String fromDate, String toDate);
    List<BestSellerProduct> getBestSellerProduct(String fromDate, String toDate);

    List<BestSellerProduct> getBestSellerProductAll();


    List<DayInMonthStatistic2> getDailyRevenue2(String startDate, String endDate);

    List<ProductStatistic> getStatisticProductInTime(String fromDate, String toDate);

    List<OrderStatistic> getStatisticOrder();

    List<ProductStock> getProductStock();

    StatisticDate getStatisticDate(Date date1, Date date2);

}
