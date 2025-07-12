package com.example.jdshoes.controller.api;


import com.example.jdshoes.dto.AddressShipping.AddressShippingDto;
import com.example.jdshoes.service.AddressShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AddressShippingController {

    private final AddressShippingService addressShippingService;

    public AddressShippingController(AddressShippingService addressShippingService) {
        this.addressShippingService = addressShippingService;
    }

    @ResponseBody
    @PostMapping("api/public/addressShipping")
    public ResponseEntity<AddressShippingDto> createAddressShipping(@RequestBody AddressShippingDto addressShippingDto){
        return ResponseEntity.ok(addressShippingService.saveAddressShippingUser(addressShippingDto));
    }

    @ResponseBody
    @DeleteMapping("/api/deleteAddress/{id}")
    public void deleteAddressShipping(@PathVariable Long id) {
        addressShippingService.deleteAddressShipping(id);
    }
}
