package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Cart.CartItemDto;
import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.dto.CartDto.ProductCart;
import com.example.jdshoes.dto.CustomerDto.CustomerDto;
import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.dto.Order.OrderDetailDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.*;
import com.example.jdshoes.service.CartService;
import com.example.jdshoes.utils.UserLoginUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductDetailRepository productDetailRepository;
    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DiscountRepository discountRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final AddressShippingRepository addressShippingRepository;
    private final OrderTempRepository orderTempRepository;

    @Autowired
    private UserLoginUtil userLoginUtil;

    @Autowired
    private HttpServletRequest request;

    public CartServiceImpl(CartRepository cartRepository,
                           CartDetailRepository cartDetailRepository,
                           CustomerRepository customerRepository,
                           ProductDetailRepository productDetailRepository, BillRepository billRepository, ProductRepository productRepository, PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository, DiscountRepository discountRepository, ProductDiscountRepository productDiscountRepository, AddressShippingRepository addressShippingRepository, OrderTempRepository orderTempRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.customerRepository = customerRepository;
        this.productDetailRepository = productDetailRepository;
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.discountRepository = discountRepository;
        this.productDiscountRepository = productDiscountRepository;
        this.addressShippingRepository = addressShippingRepository;
        this.orderTempRepository = orderTempRepository;
    }

    private String getSessionId() {
        HttpSession session = request.getSession(true);
        return session.getId();
    }

    private Cart getOrCreateCart() {
        Account account = userLoginUtil.getCurrentLogin();
        if (account != null && account.getCustomer() != null) {
            Customer customer = account.getCustomer();
            return cartRepository.findByCustomerId(customer.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setCustomer(customer);
                        newCart.setCreateDate(LocalDateTime.now());
                        newCart.setUpdateDate(LocalDateTime.now());
                        return cartRepository.save(newCart);
                    });
        } else {
            String sessionId = getSessionId();
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setSessionId(sessionId);
                        newCart.setCreateDate(LocalDateTime.now());
                        newCart.setUpdateDate(LocalDateTime.now());
                        return cartRepository.save(newCart);
                    });
        }
    }




    @Override
    @Transactional
    public CartDto addToCart(CartItemDto cartItemDto) throws NotFoundException {
        // Kiểm tra số lượng hợp lệ
        if (cartItemDto.getQuantity() <= 0) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm phải lớn hơn 0");
        }
        if (cartItemDto.getQuantity() > 20) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm tối đa là 20");
        }


        // Kiểm tra sản phẩm
        ProductDetail productDetail = productDetailRepository.findById(cartItemDto.getProductDetailId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

        // Kiểm tra trạng thái sản phẩm
        Product product = productRepository.findByProductDetailId(productDetail.getId());
        if (product.getStatus() == 2) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                    String.format("Sản phẩm %s - %s - %s đã ngừng bán",
                            product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
        }

        // Kiểm tra số lượng tồn kho
        if (productDetail.getQuantity() < cartItemDto.getQuantity()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                    String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                            product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName(), productDetail.getQuantity()));
        }

        Cart cart = getOrCreateCart();


        Optional<CartDetail> existingItem = cartDetailRepository.findByCartAndProductDetail(cart, productDetail);
        System.out.println("Cart ID: " + cart.getId() + ", ProductDetail ID: " + productDetail.getId() + ", Existing Item: " + existingItem.isPresent());


        if (existingItem.isPresent()) {
            CartDetail cartDetail = existingItem.get();
            int oldQuantity = cartDetail.getQuantity();
            int newQuantity = cartDetail.getQuantity() + cartItemDto.getQuantity();
            System.out.println("Old Quantity: " + oldQuantity + ", New Quantity: " + newQuantity);
            if (newQuantity > productDetail.getQuantity()) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Tổng số lượng trong giỏ hàng vượt quá số lượng tồn kho (%d)", productDetail.getQuantity()));
            }
            if (newQuantity > 20) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tổng số lượng sản phẩm tối đa là 20");
            }
            cartDetail.setQuantity(newQuantity);
            cartDetail.setUpdateDate(LocalDateTime.now());
            cartDetailRepository.save(cartDetail);
            cartDetailRepository.flush();
            System.out.println("Updated CartDetail: ID=" + cartDetail.getId() + ", Quantity=" + cartDetail.getQuantity());
        } else {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProductDetail(productDetail);
            cartDetail.setQuantity(cartItemDto.getQuantity());
            cartDetail.setCreateDate(LocalDateTime.now());
            cartDetail.setUpdateDate(LocalDateTime.now());
            cartDetailRepository.save(cartDetail);
            cartDetailRepository.flush();
        }

        return getCartByAccountId();
    }

    @Override
    @Transactional
    public CartDto updateCartItem(CartItemDto cartItemDto) throws NotFoundException {
        if (cartItemDto.getId() == null) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "ID của CartDetail không được null");
        }
        if (cartItemDto.getQuantity() <= 0) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm phải lớn hơn 0");
        }
        if (cartItemDto.getQuantity() > 20) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm tối đa là 20");
        }

        CartDetail cartDetail = cartDetailRepository.findById(cartItemDto.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng với ID: " + cartItemDto.getId()));

        Cart cart = getOrCreateCart();
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new ShoesApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật mục này trong giỏ hàng");
        }

        ProductDetail productDetail = cartDetail.getProductDetail();
        if (cartItemDto.getQuantity() > productDetail.getQuantity()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                    String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                            productDetail.getProduct().getName(), productDetail.getSize().getName(),
                            productDetail.getColor().getName(), productDetail.getQuantity()));
        }

        cartDetail.setQuantity(cartItemDto.getQuantity());
        cartDetail.setUpdateDate(LocalDateTime.now());
        cartDetailRepository.save(cartDetail);
        cartDetailRepository.flush();

        return getCartByAccountId();
    }

    @Override
    @Transactional
    public CartDto removeFromCart(Long cartDetailId) throws NotFoundException {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        Cart cart = getOrCreateCart();
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new ShoesApiException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa mục này trong giỏ hàng");
        }

        cartDetailRepository.delete(cartDetail);
        cartDetailRepository.flush();

        if (cartDetailRepository.existsById(cartDetailId)) {
            throw new ShoesApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa sản phẩm khỏi giỏ hàng");
        }

        return getCartByAccountId();
    }


    @Override
    @Transactional
    public CartDto clearCart() throws NotFoundException {
        Cart cart = getOrCreateCart();
        cartDetailRepository.deleteAllByCartId(cart.getId());
        return getCartByAccountId();
    }

    @Override
    @Transactional
    public Map<String, Object> orderUser(OrderDto orderDto) {
        // Lấy thông tin tài khoản hiện tại (nếu có)
        Account account = userLoginUtil.getCurrentLogin();
        Customer customer = null;
        if (account != null && account.getCustomer() != null) {
            customer = account.getCustomer();
        }

        // Kiểm tra sản phẩm và tính tổng tiền
        BigDecimal total = BigDecimal.ZERO;
        List<BillDetail> billDetailList = new ArrayList<>();
        for (OrderDetailDto item : orderDto.getOrderDetailDtos()) {
            ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
            Product product = productRepository.findByProductDetailId(productDetail.getId());
            if (product.getStatus() == 2) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Sản phẩm %s - %s - %s đã ngừng bán",
                                product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
            }
            if (productDetail.getQuantity() < item.getQuantity()) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                                product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName(), productDetail.getQuantity()));
            }

            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal detailPrice;
            if (productDiscount != null) {
                detailPrice = productDiscount.getDiscountedAmount();
                total = total.add(productDiscount.getDiscountedAmount().multiply(quantity));
            } else {
                detailPrice = productDetail.getPrice();
                total = total.add(productDetail.getPrice().multiply(quantity));
            }

            BillDetail billDetail = new BillDetail();
            billDetail.setQuantity(item.getQuantity());
            billDetail.setProductDetail(productDetail);
            billDetail.setDetailPrice(detailPrice);
            billDetailList.add(billDetail);
        }

        // Kiểm tra và áp dụng voucher
        BigDecimal promotionPrice = orderDto.getPromotionPrice() != null ? orderDto.getPromotionPrice() : BigDecimal.ZERO;
        if (orderDto.getVoucherId() != null) {
            Discount discount = discountRepository.findById(orderDto.getVoucherId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher"));
            if (discount.getMaximumUsage() <= 0) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Voucher đã hết lượt sử dụng");
            }
            if (total.compareTo(discount.getMinimumAmount()) < 0) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tổng giá trị đơn hàng không đủ để áp dụng voucher");
            }
            // Kiểm tra loại voucher và áp dụng giảm giá
            if (discount.getType() == 1) { // Voucher phần trăm
                BigDecimal maxDiscount = discount.getMaximumAmount();
                if (promotionPrice.compareTo(maxDiscount) > 0) {
                    promotionPrice = maxDiscount;
                }
            } else if (discount.getType() == 2) { // Voucher số tiền cố định
                promotionPrice = discount.getMaximumAmount(); // Luôn sử dụng MaximumAmount cho voucher tiền mặt
            }
        }

        // Tính nextCode để tạo orderId
        Bill billCurrent = billRepository.findTopByOrderByIdDesc();
        int nextCode = (billCurrent == null) ? 1 : Integer.parseInt(billCurrent.getCode().substring(2)) + 1;

        String orderPrefix = "HD" + String.format("%05d", nextCode); // HD00001
        String orderId = orderPrefix + "_ORDER" + System.currentTimeMillis(); // HD001_ORDER
        String vnpTxnRef = orderPrefix + "_PAY_" + System.currentTimeMillis(); // HD001_PAY_123456789

        Map<String, Object> response = new HashMap<>();

        // Tính tổng tiền sau khi áp dụng voucher
        BigDecimal totalWithShipping = total.add(orderDto.getShippingFee() != null ?
                orderDto.getShippingFee() : BigDecimal.ZERO);
        BigDecimal finalAmount = totalWithShipping.subtract(promotionPrice);

        // Nếu thanh toán bằng tiền mặt, lưu hóa đơn ngay
        if (orderDto.getPaymentMethodId() == 1L) {
            Bill bill = createBill(orderDto, billDetailList, customer, total, orderPrefix);

            // Tạo bản ghi Payment cho thanh toán tiền mặt
            Payment payment = new Payment();
            payment.setAmount(finalAmount.toString()); // Sử dụng finalAmount đã trừ voucher
            payment.setOrderId(orderPrefix);
            payment.setOrderStatus("COD");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatusExchange(0);
            payment.setBill(bill);
            paymentRepository.save(payment);
            paymentRepository.flush();

            clearCart();
            response.put("status", "SUCCESS");
            response.put("billCode", orderPrefix);
            response.put("message", "Đặt hàng thành công, vui lòng chờ xác nhận ");
        }
        // Nếu thanh toán VNPay, lưu tạm thời và đợi callback
        else if (orderDto.getPaymentMethodId() == 3L) {
            Payment payment = new Payment();
            payment.setAmount(finalAmount.toString()); // Sử dụng finalAmount đã trừ voucher
            payment.setOrderId(orderId);
            payment.setVnpTxnRef(vnpTxnRef);
            payment.setOrderStatus("PENDING");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatusExchange(0);
            paymentRepository.save(payment);

            // Lưu orderTemp vào bảng OrderTemp
            try {
                OrderTemp orderTemp = new OrderTemp();
                orderTemp.setOrderId(orderId);
                orderTemp.setVnpTxnRef(vnpTxnRef);
                orderTemp.setOrderData(new ObjectMapper().writeValueAsString(orderDto));
                orderTemp.setCreatedAt(LocalDateTime.now());
                orderTemp.setShippingFee(orderDto.getShippingFee());
                orderTempRepository.save(orderTemp);
                System.out.println("Saved OrderTemp with orderId: " + orderId);
            } catch (JsonProcessingException e) {
                System.err.println("Error saving OrderTemp: " + e.getMessage());
                throw new ShoesApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lưu dữ liệu đơn hàng tạm thời: " + e.getMessage());
            }

            response.put("status", "SUCCESS");
            response.put("orderId", orderId);
            response.put("vnpTxnRef", vnpTxnRef);
            response.put("amount", finalAmount);
        } else {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Phương thức thanh toán không hỗ trợ");
        }
        return response;
    }

    @Transactional
    public void completeOrderAfterPayment(OrderDto orderDto, String orderId) {
        System.out.println("[DEBUG] Completing order for orderId: " + orderId);
        Optional<Payment> paymentOpt = paymentRepository.findFirstByOrderId(orderId);
        if (paymentOpt.isEmpty()) {
            System.err.println("[ERROR] Payment not found for orderId: " + orderId);
            throw new NotFoundException("Không tìm thấy giao dịch thanh toán với orderId: " + orderId);
        }
        Payment payment = paymentOpt.get();
        if (!payment.getOrderStatus().equals("COMPLETED")) {
            System.err.println("[ERROR] Invalid payment status for orderId: " + orderId + ", status: " + payment.getOrderStatus());
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Giao dịch chưa được xác nhận thành công. Trạng thái: " + payment.getOrderStatus());
        }

        // Xử lý cho cả khách vãng lai
        Customer customer = null;
        Account account = userLoginUtil.getCurrentLogin();
        if (account != null && account.getCustomer() != null) {
            customer = account.getCustomer();
        }

        BigDecimal total = BigDecimal.ZERO;
        List<BillDetail> billDetailList = new ArrayList<>();
        try {
            for (OrderDetailDto item : orderDto.getOrderDetailDtos()) {
                ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + item.getProductDetailId()));
                Product product = productRepository.findByProductDetailId(productDetail.getId());
                if (product == null) {
                    throw new NotFoundException("Không tìm thấy sản phẩm cho ProductDetail ID: " + item.getProductDetailId());
                }
                if (product.getStatus() == 2) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s - %s - %s đã ngừng bán",
                                    product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
                }
                if (productDetail.getQuantity() < item.getQuantity()) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                                    product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName(), productDetail.getQuantity()));
                }

                ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal detailPrice = productDiscount != null ? productDiscount.getDiscountedAmount() : productDetail.getPrice();
                total = total.add(detailPrice.multiply(quantity));

                BillDetail billDetail = new BillDetail();
                billDetail.setQuantity(item.getQuantity());
                billDetail.setProductDetail(productDetail);
                billDetail.setDetailPrice(detailPrice);
                billDetailList.add(billDetail);

                productDetail.setQuantity(productDetail.getQuantity() - item.getQuantity());
                productDetailRepository.save(productDetail);
            }

            Bill bill = createBill(orderDto, billDetailList, customer, total, orderId);
            payment.setBill(bill);
            paymentRepository.save(payment);
            paymentRepository.flush();

            if (orderDto.getVoucherId() != null) {
                Discount discount = discountRepository.findById(orderDto.getVoucherId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher với ID: " + orderDto.getVoucherId()));
                discount.setMaximumUsage(discount.getMaximumUsage() - 1);
                discountRepository.save(discount);
            }

            clearCart();
            System.out.println("[DEBUG] Successfully completed order with bill code: " + bill.getCode() + " for orderId: " + orderId);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to complete order for orderId: " + orderId + ", message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private Bill createBill(OrderDto orderDto, List<BillDetail> billDetailList, Customer customer, BigDecimal total, String orderId) {
        String billCode;

        // Xử lý billCode dựa trên orderId
        if (orderId != null && orderId.contains("_ORDER")) {
            billCode = orderId.replace("QR_", "").substring(0, orderId.indexOf("_ORDER"));
        } else if (orderId != null && orderId.contains("QR_")) {
            billCode = orderId.replace("QR_", "HD");
        } else {
            Bill billCurrent = billRepository.findTopByOrderByIdDesc();
            int nextCode = (billCurrent == null) ? 1 : Integer.parseInt(billCurrent.getCode().substring(2)) + 1;
            billCode = "HD" + String.format("%05d", nextCode);
        }

        Bill existingBill = billRepository.findByCode(billCode);
        if (existingBill != null) {
            System.err.println("[ERROR] Bill already exists with code: " + billCode);
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Hóa đơn với mã " + billCode + " đã tồn tại");
        }

        System.out.println("[DEBUG] Creating bill with code: " + billCode + " for orderId: " + orderId);

        Bill bill = new Bill();
        bill.setCode(billCode);
        bill.setCreateDate(LocalDateTime.now());
        bill.setUpdateDate(LocalDateTime.now());
        bill.setInvoiceType(InvoiceType.ONLINE);
        bill.setStatus(orderDto.getPaymentMethodId() == 1L ? BillStatus.CHO_XAC_NHAN : BillStatus.DA_XAC_NHAN);
        bill.setPromotionPrice(orderDto.getPromotionPrice() != null ? orderDto.getPromotionPrice() : BigDecimal.ZERO);
        bill.setReturnStatus(false);
        bill.setIsShipping(true);
        bill.setBillingAddress(orderDto.getBillingAddress());
        bill.setCustomerName(orderDto.getShippingName());
        bill.setCustomerPhoneNumber(orderDto.getShippingPhone());
        bill.setCustomerEmail(orderDto.getShippingEmail());
        bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");
        bill.setShippingFee(orderDto.getShippingFee() != null ? orderDto.getShippingFee() : BigDecimal.ZERO);
        bill.setAmount(total.subtract(orderDto.getPromotionPrice() != null ? orderDto.getPromotionPrice() : BigDecimal.ZERO)); // Cập nhật amount
        bill.setCustomer(customer);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderDto.getPaymentMethodId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));
        bill.setPaymentMethod(paymentMethod);

        if (orderDto.getVoucherId() != null) {
            Discount discount = discountRepository.findById(orderDto.getVoucherId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher"));
            bill.setDiscount(discount);
        }

        bill.setBillDetail(billDetailList);
        for (BillDetail billDetail : billDetailList) {
            billDetail.setBill(bill);
        }

        billRepository.save(bill);
        billRepository.flush();
        System.out.println("[DEBUG] Saved bill with code: " + bill.getCode());

        return bill;
    }

    @Override
    public OrderDto orderAtCounter(OrderDto orderDto) {
        Bill billCurrent = billRepository.findTopByOrderByIdDesc();
        int nextCode = (billCurrent == null) ? 1 : Integer.parseInt(billCurrent.getCode().substring(2)) + 1;
        String billCode = "HD" + String.format("%05d", nextCode);

        // Tạo hóa đơn mới
        Bill bill = new Bill();
        bill.setCode(billCode);
        bill.setCreateDate(LocalDateTime.now());
        bill.setUpdateDate(LocalDateTime.now());
        bill.setInvoiceType(InvoiceType.OFFLINE);
        if (orderDto.getIsShipping() != null && orderDto.getIsShipping()) {
            bill.setStatus(BillStatus.DA_XAC_NHAN);
        } else {
            bill.setStatus(BillStatus.HOAN_THANH);
        }
        bill.setPromotionPrice(orderDto.getPromotionPrice());
        bill.setReturnStatus(false);
        bill.setBillingAddress(orderDto.getBillingAddress() != null ? orderDto.getBillingAddress() : "");

        // Xử lý khách hàng
        Customer customer = null;
        if (orderDto.getCustomer() != null && orderDto.getCustomer().getId() != null) {
            customer = customerRepository.findById(orderDto.getCustomer().getId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
            bill.setCustomer(customer);
            bill.setCustomerEmail(customer.getEmail());
            bill.setCustomerName(customer.getName());
            bill.setCustomerPhoneNumber(customer.getPhoneNumber());

            if (orderDto.getCustomer().getEmail() != null) {
                customer.setEmail(orderDto.getCustomer().getEmail());
                customerRepository.save(customer);
            }
        }

        if (orderDto.getIsShipping() != null && orderDto.getIsShipping()) {
            bill.setIsShipping(true);
            bill.setShippingFee(orderDto.getShippingFee() != null ? orderDto.getShippingFee() : BigDecimal.ZERO);

            if (customer != null) {
                if (orderDto.getShippingAddressId() != null) {
                    // TH1: Chọn từ AddressShipping
                    AddressShipping address = addressShippingRepository.findById(orderDto.getShippingAddressId())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ giao hàng"));
                    bill.setCustomerName(address.getCustomer().getName());
                    bill.setCustomerPhoneNumber(address.getCustomer().getPhoneNumber());
                    bill.setCustomerAddress(address.getAddress());
                    bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");
                } else {
                    // TH2: Nhập địa chỉ mới
                    if (orderDto.getShippingName() == null || orderDto.getShippingName().isEmpty()) {
                        throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên người nhận!");
                    }
                    if (orderDto.getShippingPhone() == null || orderDto.getShippingPhone().isEmpty()) {
                        throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập số điện thoại giao hàng!");
                    }
                    bill.setCustomerName(orderDto.getShippingName());
                    bill.setCustomerPhoneNumber(orderDto.getShippingPhone());
                    bill.setCustomerAddress(orderDto.getShippingFullAddress());
                    bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");

                    // Lưu địa chỉ mới vào AddressShipping
                    AddressShipping newAddress = new AddressShipping();
                    newAddress.setCustomer(customer);
                    newAddress.setStreet(orderDto.getShippingFullAddress().split(",")[0].trim());
                    newAddress.setWard(orderDto.getShippingFullAddress().split(",")[1].trim());
                    newAddress.setDistrict(orderDto.getShippingFullAddress().split(",")[2].trim());
                    newAddress.setProvince(orderDto.getShippingFullAddress().split(",")[3].trim());
                    newAddress.setAddress(orderDto.getShippingFullAddress());
                    newAddress.setIsDefault(false);
                    addressShippingRepository.save(newAddress);
                }
            } else {
                // Khách lẻ
                if (orderDto.getShippingName() == null || orderDto.getShippingName().isEmpty()) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên người nhận!");
                }
                if (orderDto.getShippingPhone() == null || orderDto.getShippingPhone().isEmpty()) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập số điện thoại giao hàng!");
                }
                bill.setCustomerName(orderDto.getShippingName());
                bill.setCustomerPhoneNumber(orderDto.getShippingPhone());
                bill.setCustomerAddress(orderDto.getShippingFullAddress());
                bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");
            }
        } else {
            // Không giao hàng
            bill.setIsShipping(false);
            bill.setCustomerName(customer != null ? customer.getName() : "Khách lẻ");
            bill.setCustomerPhoneNumber(customer != null ? customer.getPhoneNumber() : "");
            bill.setShippingNote("N/A");
        }

        // Xử lý chi tiết hóa đơn (giữ nguyên phần còn lại như trước)
        BigDecimal total = BigDecimal.ZERO;
        List<BillDetail> billDetailList = new ArrayList<>();
        for (OrderDetailDto item : orderDto.getOrderDetailDtos()) {
            BillDetail billDetail = new BillDetail();
            billDetail.setBill(bill);
            billDetail.setQuantity(item.getQuantity());

            ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

            Product product = productRepository.findByProductDetailId(productDetail.getId());
            if (product.getStatus() == 2) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + product.getName() + "-" + productDetail.getSize().getName() + "-" +
                                productDetail.getColor().getName() + " đã ngừng bán");
            }

            if (productDetail.getQuantity() < item.getQuantity()) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + product.getName() + "-" + productDetail.getSize().getName() + "-" +
                                productDetail.getColor().getName() + " chỉ còn lại " + productDetail.getQuantity());
            }

            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            if (productDiscount != null) {
                billDetail.setDetailPrice(productDiscount.getDiscountedAmount());
                total = total.add(productDiscount.getDiscountedAmount().multiply(quantity));
            } else {
                billDetail.setDetailPrice(productDetail.getPrice());
                total = total.add(productDetail.getPrice().multiply(quantity));
            }

            productDetail.setQuantity(productDetail.getQuantity() - item.getQuantity());
            productDetailRepository.save(productDetail);
            billDetail.setProductDetail(productDetail);
            billDetailList.add(billDetail);
        }

        bill.setAmount(total);
        bill.setBillDetail(billDetailList);

        // Xử lý voucher (giữ nguyên)
        if (orderDto.getVoucherId() != null) {
            Discount discount = discountRepository.findById(orderDto.getVoucherId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher"));
            bill.setDiscount(discount);

            // Kiểm tra và giảm số lượng voucher
            if (discount.getMaximumUsage() > 0) {
                discount.setMaximumUsage(discount.getMaximumUsage() - 1);
                discountRepository.save(discount);
            } else {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Voucher đã hết lượt sử dụng");
            }
        }

        // Xử lý phương thức thanh toán (giữ nguyên)
        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderDto.getPaymentMethodId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán"));
        bill.setPaymentMethod(paymentMethod);

        // Lưu hóa đơn
        Bill savedBill = billRepository.save(bill);

        // Tạo bản ghi Payment (giữ nguyên)
        Payment payment = new Payment();
        payment.setBill(savedBill);
        payment.setAmount(String.valueOf(savedBill.getAmount()));
        payment.setPaymentDate(LocalDateTime.now());
        if (paymentMethod.getId() == 3L) {
            payment.setOrderId("QR_" + billCode);
            payment.setOrderStatus("PENDING");
        } else if (paymentMethod.getId() == 1L) {
            payment.setOrderId("CASH_" + billCode);
            payment.setOrderStatus("COMPLETED");
        } else if (paymentMethod.getId() == 2L) {
            payment.setOrderId("CARD_" + billCode);
            payment.setOrderStatus("PENDING");
        }
        payment.setStatusExchange(0);
        paymentRepository.save(payment);

        return convertToOrderDto(savedBill);
    }



    private OrderDto convertToOrderDto(Bill bill) {
        OrderDto orderDto = new OrderDto();
        orderDto.setBillId(bill.getId());

        // Xử lý thông tin khách hàng
        if (bill.getCustomer() != null) {
            orderDto.setCustomer(new CustomerDto(
                    bill.getCustomer().getId(),
                    bill.getCustomer().getCode(),
                    bill.getCustomer().getName(),
                    bill.getCustomer().getPhoneNumber(),
                    bill.getCustomer().getEmail(),
                    bill.getCustomer().getAddressShippings() != null && !bill.getCustomer().getAddressShippings().isEmpty()
                            ? bill.getCustomer().getAddressShippings().get(0).getAddress()
                            : ""
            ));
        } else {
            // Khách lẻ - lấy thông tin từ các trường customer trong bill
            orderDto.setShippingName(bill.getCustomerName());
            orderDto.setShippingPhone(bill.getCustomerPhoneNumber());
        }

        orderDto.setInvoiceType(bill.getInvoiceType());
        orderDto.setBillStatus(bill.getStatus());
        orderDto.setPaymentMethodId(bill.getPaymentMethod().getId());
        orderDto.setBillingAddress(bill.getBillingAddress());
        orderDto.setPromotionPrice(bill.getPromotionPrice());
        orderDto.setVoucherId(bill.getDiscount() != null ? bill.getDiscount().getId() : null);
        orderDto.setIsShipping(bill.getIsShipping());
        orderDto.setShippingFee(bill.getShippingFee());

        // Xử lý thông tin giao hàng
        if (bill.getIsShipping()) {
            // Nếu là khách lẻ hoặc có nhập địa chỉ mới
            if (bill.getCustomer() == null ||
                    (bill.getCustomer() != null && bill.getCustomerAddress() != null)) {
                orderDto.setShippingFullAddress(bill.getCustomerAddress());
                orderDto.setShippingNote(bill.getShippingNote());
            }
        }

        // Chi tiết đơn hàng
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for (BillDetail billDetail : bill.getBillDetail()) {
            OrderDetailDto detailDto = new OrderDetailDto();
            detailDto.setQuantity(billDetail.getQuantity());
            detailDto.setProductDetailId(billDetail.getProductDetail().getId());
            orderDetailDtos.add(detailDto);
        }
        orderDto.setOrderDetailDtos(orderDetailDtos);

        return orderDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartByAccountId() {
        CartDto cartDtos = new CartDto();
        cartDtos.setItems(new ArrayList<>()); // Giỏ hàng rỗng mặc định

        Account account = userLoginUtil.getCurrentLogin();
        if (account == null || account.getCustomer() == null) {
            return cartDtos; // Trả về giỏ rỗng nếu chưa đăng nhập
        }

        // Tìm giỏ hàng duy nhất của khách hàng
        Customer customer = account.getCustomer();
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCreateDate(LocalDateTime.now());
                    newCart.setUpdateDate(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Tạo CartDto
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setCustomerId(customer.getId());
        cartDto.setCreateDate(cart.getCreateDate());
        cartDto.setUpdatedDate(cart.getUpdateDate());

        // Lấy tất cả CartDetail của giỏ hàng
        List<CartDetail> cartDetails = cartDetailRepository.findAllByCartId(cart.getId());
        List<CartDto.CartItem> items = new ArrayList<>();

        for (CartDetail cartDetail : cartDetails) {
            ProductDetail productDetail = cartDetail.getProductDetail();
            Product product = productDetail.getProduct();

            // Tạo ProductCart
            ProductCart productCart = new ProductCart();
            productCart.setProductId(product.getId());
            productCart.setName(product.getName());
            productCart.setCode(product.getCode());
            // Lấy ảnh đầu tiên từ ProductDetail
            List<Image> images = productDetail.getImages();
            productCart.setImageUrl(images != null && !images.isEmpty() ? images.get(0).getLink() : null);

            // Tạo ProductDetailDto
            ProductDetailDto productDetailDto = new ProductDetailDto();
            productDetailDto.setId(productDetail.getId());
            productDetailDto.setProductId(product.getId());
            productDetailDto.setPrice(productDetail.getPrice());
            productDetailDto.setQuantity(productDetail.getQuantity());
            productDetailDto.setSizeName(productDetail.getSize().getName());
            productDetailDto.setColorName(productDetail.getColor().getName());
            productDetailDto.setProductName(product.getName());
            productDetailDto.setProductDescription(product.getDescription());
            productDetailDto.setBrandName(product.getBrand().getName());
            productDetailDto.setMaterialName(product.getMaterial().getName());
            productDetailDto.setCategoryName(product.getCategory().getName());

            // Thêm ảnh vào DTO
            List<ImageDto> imageDtos = images.stream()
                    .map(img -> {
                        ImageDto dto = new ImageDto();
                        dto.setLink(img.getLink());
                        dto.setId(img.getId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            productDetailDto.setImages(imageDtos);

            // Nếu có khuyến mãi
            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
            if (productDiscount != null) {
                productDetailDto.setDiscountedAmount(productDiscount.getDiscountedAmount());
            }

            // Tạo CartItem
            CartDto.CartItem cartItem = new CartDto.CartItem();
            cartItem.setId(cartDetail.getId());
            cartItem.setQuantity(cartDetail.getQuantity());
            cartItem.setCreateDate(cartDetail.getCreateDate());
            cartItem.setUpdatedDate(cartDetail.getUpdateDate());
            cartItem.setProduct(productCart);
            cartItem.setProductDetail(productDetailDto);

            items.add(cartItem);
        }

        // Gán danh sách CartItem vào CartDto
        cartDto.setItems(items);

        return cartDto;
    }


}


