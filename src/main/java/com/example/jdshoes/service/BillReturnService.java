package com.example.jdshoes.service;

import com.example.jdshoes.dto.BillReturn.BillReturnCreateDto;
import com.example.jdshoes.dto.BillReturn.BillReturnDetailDto;
import com.example.jdshoes.dto.BillReturn.BillReturnDto;
import com.example.jdshoes.dto.BillReturn.SearchBillReturnDto;

import java.util.List;

public interface BillReturnService {
    List<BillReturnDto> getAllBillReturns(SearchBillReturnDto searchBillReturnDto);

    BillReturnDto createBillReturn(BillReturnCreateDto billReturnCreateDto);

    BillReturnDetailDto getBillReturnDetailById(Long id);
    BillReturnDetailDto getBillReturnDetailByCode(String code);

    String generateHtmlContent(Long billReturnId);

    BillReturnDto updateStatus(Long id, int returnStatus);
}
