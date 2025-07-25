package com.example.jdshoes.controller.api;

import com.example.jdshoes.config.ConfigVNPay;
import com.example.jdshoes.dto.Payment.PaymentDto;
import com.example.jdshoes.dto.Payment.PaymentResultDto;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.Payment;
import com.example.jdshoes.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {
    private final PaymentRepository paymentRepository;

    public PaymentRestController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createPayment(HttpServletRequest req,
                                                    @RequestParam String orderId,
                                                    @RequestParam long amount) throws UnsupportedEncodingException {
        Optional<Payment> paymentOpt = paymentRepository.findFirstByOrderId(orderId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }
        Payment payment = paymentOpt.get();

        // Kiểm tra xem Payment đã có vnp_TxnRef chưa
        String vnp_TxnRef = payment.getVnpTxnRef();
        if (vnp_TxnRef == null) {
            throw new IllegalArgumentException("Invalid vnp_TxnRef for orderId: " + orderId);
        }

        long vnp_Amount = amount * 100;

        PaymentResultDto paymentResultDto = new PaymentResultDto();
        paymentResultDto.setTxnRef(vnp_TxnRef);
        paymentResultDto.setAmount(String.valueOf(amount));

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String vnp_TmnCode = ConfigVNPay.vnp_TmnCode;
        String vnp_IpAddr = ConfigVNPay.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", ConfigVNPay.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = ConfigVNPay.hmacSHA512(ConfigVNPay.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = ConfigVNPay.vnp_PayUrl + "?" + queryUrl;

        PaymentDto paymentDto = new PaymentDto("OK", "success", paymentUrl, vnp_TxnRef);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "success");
        response.put("paymentUrl", paymentUrl);
        response.put("vnpTxnRef", vnp_TxnRef);
        response.put("orderId", orderId);
        return ResponseEntity.ok(response);
    }


}
