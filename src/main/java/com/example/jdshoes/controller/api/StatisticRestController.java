package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Statistic.*;
import com.example.jdshoes.repository.BillRepository;
import com.example.jdshoes.service.AccountService;
import com.example.jdshoes.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
public class StatisticRestController {

    private final StatisticService statisticService;

    private final AccountService accountService;

    @Autowired
    private BillRepository billRepository;

    public StatisticRestController(StatisticService statisticService, AccountService accountService) {
        this.statisticService = statisticService;
        this.accountService = accountService;    }

    @GetMapping("/api/get-statistic-revenue-day-in-month")
    private List<DayInMonthStatistic> getDayInMonthStatistic(@RequestParam String month, @RequestParam String year) {
        return statisticService.getDayInMonthStatistic(month, year);
    }

    @GetMapping("/api/get-statistic-revenue-day-from-time")
    private List<DayInMonthStatistic2> getDayInMonthStatistic2(@RequestParam String fromDate, @RequestParam String toDate) {
        return statisticService.getDailyRevenue2(fromDate, toDate);
    }

    @GetMapping("/api/get-statistic-revenue-month-from-time")
    private List<MonthInYearStatistic2> getMonthlyStatistic(@RequestParam String fromMonth, @RequestParam String toMonth) {
        return statisticService.getMonthlyRevenue(fromMonth, toMonth);
    }


    @GetMapping("/api/get-bestseller-product")
    private List<BestSellerProduct> getBestSellerProductInTime(@RequestParam String fromDate, @RequestParam String toDate) {
        return statisticService.getBestSellerProduct(fromDate, toDate);
    }

    @GetMapping("/api/get-bestseller-product-all")
    private List<BestSellerProduct> getBestSellerProductAll() {
        return statisticService.getBestSellerProductAll();
    }

//    @GetMapping("/api/get-bestseller-product-all")
//    private List<ProductStock> get() {
//        return statisticService.getBestSellerProductAll();
//    }

    @GetMapping("/api/get-statistic-revenue-month-in-year")
    private List<MonthInYearStatistic> getMonthInYearStatistic(@RequestParam String year) {
        return statisticService.getMonthInYearStatistic(year);
    }

    @GetMapping("/api/get-bestseller-product-time")
    private List<BestSellerProduct> getBestSellerProductTime(@RequestParam String fromDate, @RequestParam String toDate) {
        return statisticService.getBestSellerProduct(fromDate, toDate);
    }

    @GetMapping("/get-statistic-user-by-month")
    public List<UserStatistic> getStatisticUserByMonth() {
        List<UserStatistic> userStatistics = accountService.getUserStatistics("2023-01-01", "2023-12-31");
        return  userStatistics;
    }

    @GetMapping("/api/get-statistic-product-time")
    public List<ProductStatistic> getStatisticProductInTime(@RequestParam String fromDate, @RequestParam String toDate) {
        List<ProductStatistic> productStatistics = statisticService.getStatisticProductInTime(fromDate, toDate);
        return  productStatistics;
    }

    @GetMapping("/api/get-statistic-order")
    public List<OrderStatistic> getStatisticOrder() {
        List<OrderStatistic> orderStatisticList = statisticService.getStatisticOrder();
        return orderStatisticList;
    }

    @GetMapping("/api/get-statistic-date")
    public StatisticDate getStatisticDate(@RequestParam Date date1, @RequestParam Date date2) {
        StatisticDate result = statisticService.getStatisticDate(date1, date2);
        return result;
    }

    @GetMapping("/api/get-statistic-month")
    public StatisticMonth getStatisticMonth(@RequestParam Integer month1, @RequestParam Integer month2,
                                          @RequestParam Integer year1, @RequestParam Integer year2) {
        Long amount1 = billRepository.doanhThuThang(month1, year1);
        Long amount2 = billRepository.doanhThuThang(month2, year2);
        StatisticMonth result = new StatisticMonth(month1, month2, year1, year2,amount1, amount2);
        return result;
    }

    @GetMapping("/api/get-statistic-year")
    public StatisticYear getStatisticYear(@RequestParam Integer year1, @RequestParam Integer year2) {
        Long amount1 = billRepository.doanhThuNam(year1);
        Long amount2 = billRepository.doanhThuNam(year2);
        StatisticYear result = new StatisticYear(year1, year2,amount1, amount2);
        return result;
    }
}
