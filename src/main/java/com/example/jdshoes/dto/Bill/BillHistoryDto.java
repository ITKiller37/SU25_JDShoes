package com.example.jdshoes.dto.Bill;

import com.example.jdshoes.entity.enumClass.BillStatus;

import java.time.LocalDateTime;

public interface BillHistoryDto {
    Long getId();
    BillStatus getStatus();
    String getNote();
    LocalDateTime getCreatedAt();
    String getCreatedBy();
}
