package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DiscountScheduler {

    @Autowired
    private DiscountService discountService;

    @Scheduled(fixedRate = 60000)
    public void updateDiscountStatuses() {
        List<Discount> discounts = discountService.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Discount discount : discounts) {
            int newStatus = (now.isBefore(discount.getStartDate())) ? 2
                    : (now.isAfter(discount.getEndDate())) ? 0
                    : 1;

            if (discount.getStatus() != newStatus) {
                discount.setStatus(newStatus);
                discountService.createDiscount(discount);
            }
        }
    }
}
