package com.example.jdshoes.controller.api;

import com.example.jdshoes.config.ConfigVNPay;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.dto.Payment.PaymentResultDto;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.OrderTemp;
import com.example.jdshoes.entity.Payment;
import com.example.jdshoes.repository.BillRepository;
import com.example.jdshoes.repository.OrderTempRepository;
import com.example.jdshoes.repository.PaymentRepository;
import com.example.jdshoes.service.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final OrderTempRepository orderTempRepository;
    private final RestTemplate restTemplate;
    private final CartService cartService;
    private final BillRepository billRepository;

    public PaymentController(PaymentRepository paymentRepository, OrderTempRepository orderTempRepository, RestTemplate restTemplate, CartService cartService, BillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.orderTempRepository = orderTempRepository;
        this.restTemplate = restTemplate;
        this.cartService = cartService;
        this.billRepository = billRepository;
    }

    @GetMapping("/payment-result")
    public String viewPaymentResult(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        // Xác thực chữ ký VNPay
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = ConfigVNPay.hashAllFields(fields);

        if (!signValue.equals(vnp_SecureHash)) {
            model.addAttribute("status", "Chữ ký không hợp lệ");
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        Payment payment = paymentRepository.findByVnpTxnRef(vnp_TxnRef);
        if (payment == null) {
            System.err.println("[ERROR] Payment not found for vnp_TxnRef: " + vnp_TxnRef);
            model.addAttribute("status", "Mã giao dịch không tồn tại");
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        if (!"PENDING".equals(payment.getOrderStatus())) {
            System.err.println("[ERROR] Invalid payment status for orderId: " + payment.getOrderId() + ", status: " + payment.getOrderStatus());
            model.addAttribute("status", "Giao dịch đã tồn tại hoặc đã được xử lý");
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        PaymentResultDto paymentResultDto = new PaymentResultDto();
        paymentResultDto.setTxnRef(vnp_TxnRef);
        paymentResultDto.setAmount(new BigDecimal(fields.get("vnp_Amount")).divide(new BigDecimal(100)).toString());
        paymentResultDto.setBankCode(fields.get("vnp_BankCode"));
        paymentResultDto.setDatePay(fields.get("vnp_PayDate"));
        paymentResultDto.setResponseCode(fields.get("vnp_ResponseCode"));
        paymentResultDto.setTransactionStatus(fields.get("vnp_TransactionStatus"));
        model.addAttribute("result", paymentResultDto);

        if (!"00".equals(paymentResultDto.getResponseCode())) {
            model.addAttribute("status", "Giao dịch không thành công. Mã lỗi: " + paymentResultDto.getResponseCode());
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        BigDecimal vnpAmount = new BigDecimal(fields.get("vnp_Amount")).divide(new BigDecimal(100));
        BigDecimal paymentAmount = new BigDecimal(payment.getAmount());
        if (vnpAmount.compareTo(paymentAmount) != 0) {
            System.err.println("[ERROR] Amount mismatch: VNPay=" + vnpAmount + ", Payment=" + paymentAmount);
            model.addAttribute("status", "Số tiền không khớp. VNPay: " + vnpAmount + ", Hệ thống: " + paymentAmount);
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        payment.setOrderStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        paymentRepository.flush();

        OrderTemp orderTemp = orderTempRepository.findByVnpTxnRef(vnp_TxnRef);
        if (orderTemp == null) {
            System.err.println("[ERROR] OrderTemp not found for vnp_TxnRef: " + vnp_TxnRef);
            model.addAttribute("status", "Không tìm thấy dữ liệu đơn hàng tạm thời");
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }
        String orderId = orderTemp.getOrderId();
        if (!orderId.equals(payment.getOrderId())) {
            System.err.println("[ERROR] orderId mismatch: Payment orderId = " + payment.getOrderId() + ", OrderTemp orderId = " + orderId);
            model.addAttribute("status", "orderId không khớp với giao dịch thanh toán");
            model.addAttribute("paymentSuccess", false);
            return "user/payment-result";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            OrderDto orderDto = mapper.readValue(orderTemp.getOrderData(), OrderDto.class);
            System.out.println("[DEBUG] Parsed OrderDto: " + orderDto);
            cartService.completeOrderAfterPayment(orderDto, orderId);
            orderTempRepository.delete(orderTemp); // Xóa OrderTemp sau khi hoàn tất
            String billCode = orderId.substring(0, orderId.indexOf("_ORDER"));
            Bill bill = billRepository.findByCode(billCode);
            if (bill != null) {
                model.addAttribute("status", "Giao dịch thành công");
                model.addAttribute("paymentSuccess", true);
                model.addAttribute("orderId", orderId);
                model.addAttribute("billCode", billCode);
            } else {
                System.err.println("[ERROR] Bill not found for orderId: " + orderId + ", billCode: " + billCode);
                model.addAttribute("status", "Không tìm thấy hóa đơn sau khi thanh toán. BillCode: " + billCode);
                model.addAttribute("paymentSuccess", false);
            }
        } catch (JsonProcessingException e) {
            System.err.println("[ERROR] Failed to parse OrderDto from OrderTemp: " + orderTemp.getOrderData());
            e.printStackTrace();
            model.addAttribute("status", "Lỗi khi xử lý dữ liệu đơn hàng: " + e.getMessage());
            model.addAttribute("paymentSuccess", false);
        } catch (Exception e) {
            System.err.println("[ERROR] Complete order error for orderId: " + payment.getOrderId() + ", message: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("status", "Lỗi khi hoàn tất đơn hàng: " + e.getMessage());
            model.addAttribute("paymentSuccess", false);
        }

        return "user/payment-result";

    }

    private void updatePaymentStatus(Payment payment) {
        payment.setOrderStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
