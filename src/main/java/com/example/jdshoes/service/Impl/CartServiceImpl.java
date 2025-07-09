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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public CartServiceImpl(CartRepository cartRepository,
                           CartDetailRepository cartDetailRepository,
                           CustomerRepository customerRepository,
                           ProductDetailRepository productDetailRepository, BillRepository billRepository, ProductRepository productRepository, PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository, DiscountRepository discountRepository, ProductDiscountRepository productDiscountRepository, AddressShippingRepository addressShippingRepository) {
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
    }


    @Value("${ghn.api.key}")
    private String ghnApiKey;

    @Value("${ghn.shop.id}")
    private Integer ghnShopId;

    @Override
    public List<CartItemDto> getCartByCustomerId(Long customerId) {
//        Cart cart = cartRepository.findByCustomerId(customerId)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy giỏ hàng"));
//
//        return cartDetailRepository.findByCartId(cart.getId()).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
        return null;
    }

    @Override
    @Transactional
    public void addToCart(CartItemDto cartItemDto) throws NotFoundException {
        Customer customer = customerRepository.findById(cartItemDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));

        ProductDetail productDetail = productDetailRepository.findById(cartItemDto.getProductDetailId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

        // Kiểm tra số lượng tồn kho
        if (productDetail.getQuantity() < cartItemDto.getQuantity()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
        }

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCreateDate(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        Optional<CartDetail> existingItem = cartDetailRepository.findByCartAndProductDetail(cart, productDetail);

        if (existingItem.isPresent()) {
            // Cập nhật số lượng nếu đã có
            CartDetail cartDetail = existingItem.get();
            int newQuantity = cartDetail.getQuantity() + cartItemDto.getQuantity();
            cartDetail.setQuantity(newQuantity);
            cartDetail.setUpdateDate(LocalDateTime.now());
            cartDetailRepository.save(cartDetail);
        } else {
            // Thêm mới vào giỏ hàng
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProductDetail(productDetail);
            cartDetail.setQuantity(cartItemDto.getQuantity());
            cartDetail.setCreateDate(LocalDateTime.now());
            cartDetail.setUpdateDate(LocalDateTime.now());
            cartDetailRepository.save(cartDetail);
        }
    }

    @Override
    @Transactional
    public void updateCartItem(CartItemDto cartItemDto) throws NotFoundException {
        CartDetail cartDetail = cartDetailRepository.findById(cartItemDto.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        ProductDetail productDetail = cartDetail.getProductDetail();

        if (productDetail.getQuantity() < cartItemDto.getQuantity()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
        }

        cartDetail.setQuantity(cartItemDto.getQuantity());
        cartDetail.setUpdateDate(LocalDateTime.now());
        cartDetailRepository.save(cartDetail);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartDetailId) throws NotFoundException {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        cartDetailRepository.delete(cartDetail);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) throws NotFoundException {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giỏ hàng"));
        cartDetailRepository.deleteAllByCartId(cart.getId());
    }

    @Override
    @Transactional
    public void orderUser(OrderDto orderDto) {
//        Bill billCurrent = billRepository.findTopByOrderByIdDesc();
//        int nextCode = (billCurrent == null) ? 1 : Integer.parseInt(billCurrent.getCode().substring(2)) + 1;
//        String billCode = "HD" + String.format("%03d", nextCode);
//
//        Bill bill = new Bill();
//        bill.setBillingAddress(orderDto.getBillingAddress());
//        bill.setCreateDate(LocalDateTime.now());
//        bill.setUpdateDate(LocalDateTime.now());
//        bill.setCode(billCode);
//        bill.setInvoiceType(InvoiceType.ONLINE);
//        bill.setStatus(BillStatus.CHO_XAC_NHAN);
//        bill.setPromotionPrice(orderDto.getPromotionPrice());
//        bill.setReturnStatus(false);
//        if (UserLoginUtil.getCurrentLogin() != null) {
//            Account account = UserLoginUtil.getCurrentLogin();
//            bill.setCustomer(account.getCustomer());
//        }
//        BigDecimal total = BigDecimal.ZERO;
//        List<BillDetail> billDetailList = new ArrayList<>();
//        for (OrderDetailDto item :
//                orderDto.getOrderDetailDtos()) {
//            BillDetail billDetail = new BillDetail();
//            billDetail.setBill(bill);
//            billDetail.setQuantity(item.getQuantity());
//            ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId()).orElseThrow(() -> new NotFoundException("Product not found"));
//            billDetail.setProductDetail(productDetail);
//            Product product = productRepository.findByProductDetailId(productDetail.getId());
//            if (product.getStatus() == 2) {
//                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Sản phẩm " + productDetail.getProduct().getName() + "-" + productDetail.getSize().getName() + "-" + productDetail.getColor().getName() + " đã ngừng bán");
//
//            }
//            if (productDetail.getQuantity() - item.getQuantity() < 0) {
//                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Sản phẩm " + productDetail.getProduct().getName() + "-" + productDetail.getSize().getName() + "-" + productDetail.getColor().getName() + " chỉ còn lại " + productDetail.getQuantity());
//            }
//
//            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
//            BigDecimal quantity = new BigDecimal(item.getQuantity());
//
//            if (productDiscount != null) {
//                billDetail.setDetailPrice(productDiscount.getDiscountedAmount());
//                total = total.add(productDiscount.getDiscountedAmount().multiply(quantity));
//            } else {
//                billDetail.setDetailPrice(productDetail.getPrice());
//                total = total.add(productDetail.getPrice().multiply(quantity));
//            }
//
//            productDetail.setQuantity(productDetail.getQuantity() - item.getQuantity());
//            productDetailRepository.save(productDetail);
//            billDetailList.add(billDetail);
//        }
    }

    @Override
    public OrderDto orderAtCounter(OrderDto orderDto) {
        Bill billCurrent = billRepository.findTopByOrderByIdDesc();
        int nextCode = (billCurrent == null) ? 1 : Integer.parseInt(billCurrent.getCode().substring(2)) + 1;
        String billCode = "HD" + String.format("%03d", nextCode);

        // Tạo hóa đơn mới
        Bill bill = new Bill();
        bill.setCode(billCode);
        bill.setCreateDate(LocalDateTime.now());
        bill.setUpdateDate(LocalDateTime.now());
        bill.setInvoiceType(InvoiceType.OFFLINE);
        if (orderDto.getIsShipping() != null && orderDto.getIsShipping()) {
            bill.setStatus(BillStatus.CHO_XAC_NHAN);
        } else {
            bill.setStatus(BillStatus.HOAN_THANH);
        }
        bill.setPromotionPrice(orderDto.getPromotionPrice());
        bill.setReturnStatus(false);
        bill.setBillingAddress(orderDto.getBillingAddress() != null ? orderDto.getBillingAddress() : "");

        if (orderDto.getIsShipping() != null && orderDto.getIsShipping()) {
            bill.setIsShipping(true);
            bill.setShippingFee(orderDto.getShippingFee() != null ? orderDto.getShippingFee() : BigDecimal.ZERO);

            if (orderDto.getCustomer() != null && orderDto.getCustomer().getId() != null) {
                // Có customerId
                bill.setCustomer(customerRepository.findById(orderDto.getCustomer().getId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng")));

                if (orderDto.getShippingAddressId() != null) {
                    // TH1: Chọn từ AddressShipping
                    AddressShipping address = addressShippingRepository.findById(orderDto.getShippingAddressId())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ giao hàng"));
                    bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");
                } else {
                    // TH2: Nhập địa chỉ mới
                    if (orderDto.getShippingName() == null || orderDto.getShippingName().isEmpty()) {
                        throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên người nhận!");
                    }
                    if (orderDto.getShippingPhone() == null || orderDto.getShippingPhone().isEmpty()) {
                        throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập số điện thoại giao hàng!");
                    }
                    bill.setCustomerAddress(orderDto.getShippingFullAddress());
                    bill.setCustomerName(orderDto.getShippingName());
                    bill.setCustomerPhoneNumber(orderDto.getShippingPhone());
                    bill.setShippingNote(orderDto.getShippingNote() != null ? orderDto.getShippingNote() : "N/A");
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
            bill.setCustomerName(orderDto.getCustomer() != null && orderDto.getCustomer().getName() != null ?
                    orderDto.getCustomer().getName() : "Khách lẻ");
            bill.setCustomerPhoneNumber("");
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
            discount.setMaximumUsage(discount.getMaximumUsage() - 1);
            discountRepository.save(discount);
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
    public List<CartDto> getAllCartByAccountId() {
        Account account = UserLoginUtil.getCurrentLogin();
        if (account == null || account.getCustomer() == null) {
            throw new ShoesApiException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập hoặc tài khoản không hợp lệ");
        }
        List<Cart> cartList = cartRepository.findAllByCustomer_Id(account.getCustomer().getId()); // hoặc account.getId() nếu là 1

        List<CartDto> cartDtos = new ArrayList<>();

        for (Cart cart : cartList) {
            List<CartDetail> cartDetails = cartDetailRepository.findAllByCart_Id(cart.getId());

            for (CartDetail cartDetail : cartDetails) {
                ProductDetail productDetail = cartDetail.getProductDetail();
                Product product = productDetail.getProduct();

                // Set thông tin sản phẩm
                ProductCart productCart = new ProductCart();
                productCart.setProductId(product.getId());
                productCart.setName(product.getName());
                productCart.setCode(product.getCode());
                productCart.setDescribe(product.getDescription());

                // Lấy ảnh đầu tiên từ ProductDetail
                List<Image> images = productDetail.getImages();
                if (images != null && !images.isEmpty()) {
                    productCart.setImageUrl(images.get(0).getLink());
                } else {
                    productCart.setImageUrl(null); // hoặc gán ảnh mặc định
                }

                // Set thông tin chi tiết sản phẩm
                ProductDetailDto productDetailDto = new ProductDetailDto();
                productDetailDto.setId(productDetail.getId());
                productDetailDto.setProductId(product.getId());
                productDetailDto.setPrice(productDetail.getPrice());
                productDetailDto.setQuantity(productDetail.getQuantity());
                productDetailDto.setSizeName(productDetail.getSize().getName()); // cần chắc chắn getName() tồn tại
                productDetailDto.setColorName(productDetail.getColor().getName()); // cần chắc chắn getName() tồn tại
                productDetailDto.setProductName(product.getName());
                productDetailDto.setProductDescription(product.getDescription());
                productDetailDto.setBrandName(product.getBrand().getName());
                productDetailDto.setMaterialName(product.getMaterial().getName());
                productDetailDto.setCategoryName(product.getCategory().getName());

                // Thêm ảnh vào DTO
                List<ImageDto> imageDtos = images.stream().map(img -> {
                    ImageDto dto = new ImageDto();
                    dto.setLink(img.getLink());
                    dto.setId(img.getId());
                    return dto;
                }).toList();
                productDetailDto.setImages(imageDtos);

                // Nếu có khuyến mãi
                ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
                if (productDiscount != null) {
                    productDetailDto.setDiscountedAmount(productDiscount.getDiscountedAmount());
                }

                // Tạo DTO giỏ hàng
                CartDto cartDto = new CartDto();
                cartDto.setId(cart.getId());
                cartDto.setQuantity(cartDetail.getQuantity());
                cartDto.setCreateDate(cartDetail.getCreateDate());
                cartDto.setUpdatedDate(cartDetail.getUpdateDate());
                cartDto.setCustomerId(cart.getCustomer().getId()); // hoặc account.getId()
                cartDto.setProduct(productCart);
                cartDto.setDetail(productDetailDto);

                cartDtos.add(cartDto);
            }
        }

        return cartDtos;
    }


}


