package com.example.jdshoes.dto.BillReturn;

import lombok.Data;

@Data
public class SearchBillReturnDto {
    private String fromDate;
    private String toDate;
    private String returnStatus;
}
