package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.BillReturn.*;
import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShopApiException;
import com.example.jdshoes.repository.*;
import com.example.jdshoes.repository.Specification.BillReturnSpecification;
import com.example.jdshoes.service.BillReturnService;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillReturnServiceImpl implements BillReturnService {

    private final BillReturnRepository billReturnRepository;
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ReturnDetailRepository returnDetailRepository;
    private final AccountRepository accountRepository;

    public BillReturnServiceImpl(BillReturnRepository billReturnRepository, BillRepository billRepository, BillDetailRepository billDetailRepository, ProductDetailRepository productDetailRepository, ProductDiscountRepository productDiscountRepository, ProductRepository productRepository, CustomerRepository customerRepository, ProductDetailRepository productDetailRepository1, ReturnDetailRepository returnDetailRepository, AccountRepository accountRepository) {
        this.billReturnRepository = billReturnRepository;
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.productDiscountRepository = productDiscountRepository;
        this.productDetailRepository = productDetailRepository1;
        this.returnDetailRepository = returnDetailRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<BillReturnDto> getAllBillReturns(SearchBillReturnDto searchBillReturnDto) {
        Specification<BillReturn> spec = new BillReturnSpecification(searchBillReturnDto);
        List<BillReturn> billReturns = billReturnRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "returnDate"));
        List<BillReturnDto> billReturnDtos = billReturns.stream().map(this::convertToDto).collect(Collectors.toList());
        return billReturnDtos;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BillReturnDto createBillReturn(BillReturnCreateDto billReturnCreateDto) {
        BillReturn billReturnLast = billReturnRepository.findTopByOrderByIdDesc();
        int nextCode = (billReturnLast == null) ? 1 : Integer.parseInt(billReturnLast.getCode().substring(2)) + 1;
        String billReturnCode = "DT" + String.format("%03d", nextCode);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email);
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy account với email: " + email));

        BillReturn billReturn = new BillReturn();
        billReturn.setCode(billReturnCode);
        billReturn.setReturnReason(billReturnCreateDto.getReason());
        billReturn.setReturnDate(LocalDateTime.now());
        billReturn.setCancel(false);

        Bill bill = billRepository.findById(billReturnCreateDto.getBillId())
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        if (bill.getStatus() == BillStatus.TRA_HANG) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Đơn hàng đã được đổi trả");
        }
        bill.setReturnStatus(true);
        bill.setStatus(BillStatus.TRA_HANG);
        billRepository.save(bill);

        billReturn.setBill(bill);
        billReturn.setAccount(account);

        BigDecimal totalRefund = BigDecimal.ZERO;

        if (billReturnCreateDto.getRefundDtos() == null || billReturnCreateDto.getRefundDtos().isEmpty()) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Không có đơn trả nào");
        }

        for (RefundDto refundDto : billReturnCreateDto.getRefundDtos()) {
            if (refundDto.getQuantityRefund() > 0) {
                BillDetail billDetail = billDetailRepository.findById(refundDto.getBillDetailId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết hóa đơn"));

                ProductDetail productDetailInBill = billDetail.getProductDetail();
                ProductDetail productDetailBefore = productDetailRepository.findById(productDetailInBill.getId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm " + productDetailInBill.getId()));
                int quantityBefore = productDetailBefore.getQuantity();
                productDetailBefore.setQuantity(quantityBefore + refundDto.getQuantityRefund());

                billDetail.setReturnQuantity(refundDto.getQuantityRefund());

                BigDecimal momentPrice = billDetail.getDetailPrice() != null ? billDetail.getDetailPrice() : BigDecimal.ZERO;
                BigDecimal refundItem = BigDecimal.valueOf(refundDto.getQuantityRefund()).multiply(momentPrice);


                totalRefund = totalRefund.add(refundItem);

                billDetailRepository.save(billDetail);
            }
        }

        // ✅ Tính tiền trả khách sau khi trừ phần trăm phí
        billReturn.setPercentFeeExchange(billReturnCreateDto.getPercent());
        BigDecimal percent = BigDecimal.valueOf(billReturnCreateDto.getPercent()).divide(BigDecimal.valueOf(100));
        BigDecimal refundToCustomer = totalRefund.subtract(totalRefund.multiply(percent));

        if (billReturnCreateDto.getReturnDtos() == null || billReturnCreateDto.getReturnDtos().isEmpty()) {
            billReturn.setReturnMoney(refundToCustomer);
            billReturn.setReturnStatus(3);
        } else {
            List<ReturnDetail> returnDetails = new ArrayList<>();
            billReturn.setReturnStatus(3);
            BigDecimal totalExchange = BigDecimal.ZERO;

            for (ReturnDto returnDto : billReturnCreateDto.getReturnDtos()) {
                ReturnDetail returnDetail = new ReturnDetail();
                ProductDetail productDetail = productDetailRepository.findById(returnDto.getProductDetailId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết sản phẩm " + returnDto.getProductDetailId()));

                int oldQuantity = productDetail.getQuantity();
                if (oldQuantity - returnDto.getQuantityReturn() < 0) {
                    throw new ShopApiException(HttpStatus.BAD_REQUEST,
                            "Sản phẩm " + productDetail.getProduct().getName() + "-" +
                                    productDetail.getSize().getName() + "-" +
                                    productDetail.getColor().getName() +
                                    " chỉ còn lại " + productDetail.getQuantity());
                }

                productDetail.setQuantity(oldQuantity - returnDto.getQuantityReturn());
                productDetailRepository.save(productDetail);

                returnDetail.setBillReturn(billReturn);
                returnDetail.setQuantityReturn(returnDto.getQuantityReturn());
                returnDetail.setProductDetail(productDetail);

                // ✅ Tính tiền sản phẩm hàng đổi
                BigDecimal exchangeItem = BigDecimal.valueOf(returnDto.getQuantityReturn())
                        .multiply(productDetail.getPrice());
                totalExchange = totalExchange.add(exchangeItem);

                returnDetails.add(returnDetail);
            }

            // ✅ So sánh tiền hàng trả - tiền hàng đổi
            if (totalRefund.compareTo(totalExchange) >= 0) {
                billReturn.setReturnMoney(totalExchange.subtract(totalRefund));
            } else {
                billReturn.setReturnMoney(BigDecimal.ZERO);
            }

            returnDetailRepository.saveAll(returnDetails);
        }

        billReturnRepository.save(billReturn);

        return convertToDto(billReturn);
    }


    @Override
    public BillReturnDetailDto getBillReturnDetailById(Long id) {
        BillReturn billReturn = billReturnRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn trả lại id: " + id));
        Bill bill = billReturn.getBill();
        BillReturnDetailDto billReturnDetailDto = new BillReturnDetailDto();
        billReturnDetailDto.setBillId(bill.getId());
        billReturnDetailDto.setBillCode(bill.getCode());
        billReturnDetailDto.setReturnDate(billReturn.getReturnDate());

        if(bill.getCustomer() != null) {
            billReturnDetailDto.setCustomerDto(new CustomerDto(bill.getCustomer().getId(), bill.getCustomer().getCode(), bill.getCustomer().getName(), bill.getCustomer().getPhoneNumber(), null, null));
        }

        List<BillDetail> billDetails = bill.getBillDetail();
        List<RefundProductDto> refundProductDtos = new ArrayList<>();

        Account handler = billReturn.getAccount();
        if (handler != null) {
            System.out.println(">>> Handler Account ID: " + handler.getId());
            System.out.println(">>> Handler Code: " + handler.getCode());
            System.out.println(">>> Handler Name: " + handler.getName());

            billReturnDetailDto.setHandledByName(handler.getName());
            billReturnDetailDto.setHandledByCode(handler.getCode());
        } else {
            System.out.println(">>> Bill.getAccount() == null");
        }

        // Lấy hàng trả
        for (BillDetail billDetail : billDetails) {
            if (billDetail.getReturnQuantity() != null && billDetail.getReturnQuantity() > 0) {
                RefundProductDto refundProductDto = new RefundProductDto();
                ProductDetail productDetail = billDetail.getProductDetail();

                BigDecimal detailPrice = billDetail.getDetailPrice() != null ? billDetail.getDetailPrice() : BigDecimal.ZERO;

                refundProductDto.setProductName(productDetail.getProduct().getName());
                refundProductDto.setDetailPrice(detailPrice);
                refundProductDto.setProductDetailId(productDetail.getId());
                refundProductDto.setColor(productDetail.getColor().getName());
                refundProductDto.setSize(productDetail.getSize().getName());
                refundProductDto.setQuantityRefund(billDetail.getReturnQuantity());

                refundProductDtos.add(refundProductDto);
            }
        }


        List<ReturnProductDto> returnProductDtos = new ArrayList<>();
        // Lấy hàng đổi
        for(ReturnDetail returnDetail: billReturn.getReturnDetails()) {
            ReturnProductDto returnProductDto = new ReturnProductDto();
            returnProductDto.setProductDetailId(returnDetail.getProductDetail().getId());
            returnProductDto.setQuantityReturn(returnDetail.getQuantityReturn());
            returnProductDto.setDetailPrice(returnDetail.getProductDetail().getPrice());
            returnProductDto.setProductName(returnDetail.getProductDetail().getProduct().getName());
            returnProductDto.setSize(returnDetail.getProductDetail().getSize().getName());
            returnProductDto.setColor(returnDetail.getProductDetail().getColor().getName());
            returnProductDtos.add(returnProductDto);
        }
        billReturnDetailDto.setReturnProductDtos(returnProductDtos);

        billReturnDetailDto.setRefundProductDtos(refundProductDtos);
        billReturnDetailDto.setBillReturnCode(billReturn.getCode());

        billReturnDetailDto.setId(billReturn.getId());
        billReturnDetailDto.setReturnMoney(billReturn.getReturnMoney());
        billReturnDetailDto.setPercentFeeExchange(billReturn.getPercentFeeExchange());
        billReturnDetailDto.setBillReturnStatus(billReturn.getReturnStatus());
        return billReturnDetailDto;
    }

    @Override
    public BillReturnDetailDto getBillReturnDetailByCode(String code) {
        BillReturn billReturn = billReturnRepository.findByCode(code).orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn trả lại id: " + code));
        Bill bill = billReturn.getBill();
        BillReturnDetailDto billReturnDetailDto = new BillReturnDetailDto();
        billReturnDetailDto.setBillId(bill.getId());
        billReturnDetailDto.setBillCode(bill.getCode());
        billReturnDetailDto.setReturnDate(billReturn.getReturnDate());

        if(bill.getCustomer() != null) {
            billReturnDetailDto.setCustomerDto(new CustomerDto(bill.getCustomer().getId(), bill.getCustomer().getCode(), bill.getCustomer().getName(), bill.getCustomer().getPhoneNumber(), null, null));
        }

        List<BillDetail> billDetails = bill.getBillDetail();
        List<RefundProductDto> refundProductDtos = new ArrayList<>();

        // Lấy hàng trả
        for (BillDetail billDetail:
                billDetails) {
            if(billDetail.getReturnQuantity() != null) {
                if(billDetail.getReturnQuantity() > 0 ) {
                    RefundProductDto refundProductDto = new RefundProductDto();
                    ProductDetail productDetail = billDetail.getProductDetail();
                    refundProductDto.setProductName(productDetail.getProduct().getName());
                    BigDecimal momentPrice = billDetail.getDetailPrice() != null ? billDetail.getDetailPrice() : BigDecimal.ZERO;
                    refundProductDto.setDetailPrice(momentPrice);
                    refundProductDto.setProductDetailId(productDetail.getId());
                    refundProductDto.setColor(productDetail.getColor().getName());
                    refundProductDto.setSize(productDetail.getSize().getName());
                    refundProductDto.setQuantityRefund(billDetail.getReturnQuantity());
                    refundProductDtos.add(refundProductDto);
                }
            }
        }

        List<ReturnProductDto> returnProductDtos = new ArrayList<>();
        // Lấy hàng đổi
        for(ReturnDetail returnDetail: billReturn.getReturnDetails()) {
            ReturnProductDto returnProductDto = new ReturnProductDto();
            returnProductDto.setProductDetailId(returnDetail.getProductDetail().getId());
            returnProductDto.setQuantityReturn(returnDetail.getQuantityReturn());
            returnProductDto.setDetailPrice(returnDetail.getProductDetail().getPrice());
            returnProductDto.setProductName(returnDetail.getProductDetail().getProduct().getName());
            returnProductDto.setSize(returnDetail.getProductDetail().getSize().getName());
            returnProductDto.setColor(returnDetail.getProductDetail().getColor().getName());
            returnProductDtos.add(returnProductDto);
        }
        billReturnDetailDto.setReturnProductDtos(returnProductDtos);

        billReturnDetailDto.setRefundProductDtos(refundProductDtos);
        billReturnDetailDto.setBillReturnCode(billReturn.getCode());

        billReturnDetailDto.setId(billReturn.getId());
        billReturnDetailDto.setReturnMoney(billReturn.getReturnMoney());
        billReturnDetailDto.setPercentFeeExchange(billReturn.getPercentFeeExchange());
        billReturnDetailDto.setBillReturnStatus(billReturn.getReturnStatus());
        return billReturnDetailDto;
    }

    @Override
    public String generateHtmlContent(Long billReturnId) {
        BillReturnDetailDto billReturnDetailDto = getBillReturnDetailById(billReturnId);
        return null;
    }

    @Override
    public BillReturnDto updateStatus(Long id, int returnStatus) {
        BillReturn billReturn = billReturnRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn trả lại id: " + id));
        billReturn.setReturnStatus(returnStatus);
        if(returnStatus == 4) {
            for (ReturnDetail returnDetail:
                    billReturn.getReturnDetails()) {
                ProductDetail productDetail = returnDetail.getProductDetail();
                int quantityReturn = returnDetail.getQuantityReturn();
                int beforeQuantity = productDetail.getQuantity();
                productDetail.setQuantity(beforeQuantity + quantityReturn);
                productDetailRepository.save(productDetail);
            }
        }
        return convertToDto(billReturnRepository.save(billReturn));
    }

    private BillReturnDto convertToDto(BillReturn billReturn) {
        BillReturnDto billReturnDto = new BillReturnDto();
        billReturnDto.setId(billReturn.getId());
        billReturnDto.setCode(billReturn.getCode());
        billReturnDto.setReturnDate(billReturn.getReturnDate());
        billReturnDto.setReturnReason(billReturn.getReturnReason());
        billReturnDto.setCancel(billReturn.isCancel());
        billReturnDto.setReturnMoney(billReturn.getReturnMoney());
        billReturnDto.setReturnStatus(billReturn.getReturnStatus());
        if(billReturn.getBill().getCustomer() != null) {
            Customer customer = billReturn.getBill().getCustomer();
            CustomerDto customerDto = new CustomerDto(customer.getId(), customer.getCode(), customer.getName(), customer.getPhoneNumber(), customer.getEmail(), null);
            billReturnDto.setCustomer(customerDto);
        }
        return billReturnDto;
    }
    private BigDecimal safeMomentPrice(BillDetail billDetail) {
        BigDecimal momentPrice = billDetail.getDetailPrice();
        return momentPrice != null ? momentPrice : BigDecimal.ZERO;
    }

}
