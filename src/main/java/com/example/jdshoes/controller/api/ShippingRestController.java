package com.example.jdshoes.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ShippingRestController {
    private final RestTemplate restTemplate;

    @Value("${ghn.api.key}")
    private String ghnApiKey;

    @Value("${ghn.shop.id}")
    private Integer ghnShopId;

    @PostMapping("/calculate-shipping-fee")
    public ResponseEntity<Map<String, Object>> calculateShippingFee(
            @RequestBody ShippingFeeRequest request) {

        // Lấy districtId và wardCode từ tên
        Integer districtId = getDistrictId(request.province(), request.district());
        String wardCode = getWardCode(request.district(), request.ward());


        if (districtId == null || wardCode == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Invalid district or ward");
            return ResponseEntity.badRequest().body(errorResult);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", ghnApiKey);
            headers.set("ShopId", ghnShopId.toString());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from_district_id", 1454); // ID quận cửa hàng
            requestBody.put("service_id", 53320); // ID dịch vụ giao hàng
            requestBody.put("to_district_id", districtId);
            requestBody.put("to_ward_code", wardCode);
            requestBody.put("weight", 200);
            requestBody.put("length", 20);
            requestBody.put("width", 15);
            requestBody.put("height", 10);
            requestBody.put("insurance_value", 0);
            requestBody.put("cod_failed_amount", 0);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null && data.containsKey("total")) {
                    Integer fee = (Integer) data.get("total");
                    Map<String, Object> result = new HashMap<>();
                    result.put("shippingFee", fee);
                    return ResponseEntity.ok(result);
                }
            }
        } catch (Exception e) {
            log.error("Error calculating shipping fee: ", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("shippingFee", 0);
        return ResponseEntity.ok(result);
    }

    private Integer getDistrictId(String provinceName, String districtName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", ghnApiKey);

            // Lấy danh sách quận/huyện từ GHN
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district?province_name=" + provinceName,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                // Xử lý dữ liệu trả về để tìm districtId phù hợp
                // Đây là logic mẫu, cần điều chỉnh theo response thực tế từ GHN
                for (Object district : (Iterable<?>) data.get("data")) {
                    Map<?, ?> districtMap = (Map<?, ?>) district;
                    if (districtName.equals(districtMap.get("DistrictName"))) {
                        return (Integer) districtMap.get("DistrictID");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting district ID: ", e);
        }
        return null;
    }

    private String getWardCode(String districtName, String wardName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", ghnApiKey);

            // Lấy danh sách phường/xã từ GHN
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_name=" + districtName,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                // Xử lý dữ liệu trả về để tìm wardCode phù hợp
                // Đây là logic mẫu, cần điều chỉnh theo response thực tế từ GHN
                for (Object ward : (Iterable<?>) data.get("data")) {
                    Map<?, ?> wardMap = (Map<?, ?>) ward;
                    if (wardName.equals(wardMap.get("WardName"))) {
                        return (String) wardMap.get("WardCode");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting ward code: ", e);
        }
        return null;
    }
}

record ShippingFeeRequest(String province, String district, String ward) {}