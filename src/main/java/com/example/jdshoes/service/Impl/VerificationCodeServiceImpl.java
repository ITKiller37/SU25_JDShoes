package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.VerificationCode;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.VerificationRepository;
import com.example.jdshoes.service.EmailService;
import com.example.jdshoes.service.VerificationCodeService;
import com.example.jdshoes.utils.MailServices;
import com.example.jdshoes.utils.RandomUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationRepository verificationRepository;

    private final AccountRepository accountRepository;

    private final EmailService emailService;

    @Autowired
    private MailServices mailServices;

    public VerificationCodeServiceImpl(VerificationRepository verificationRepository, AccountRepository accountRepository, EmailService emailService) {
        this.verificationRepository = verificationRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    @Override
    public VerificationCode createVerificationCode(String email) throws MessagingException {
        // Tạo mã xác nhận ngẫu nhiên
        String verificationCodeValue = generateRandomCode();

        // Thiết lập thời gian hết hạn cho mã xác nhận
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        Account account = accountRepository.findByEmail(email);
        if(account == null) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Không tìm thấy tài khoản có địa chỉ email của bạn");
        }

        // Tạo đối tượng VerificationCode và lưu vào cơ sở dữ liệu
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setAccount(account);
        verificationCode.setCode(verificationCodeValue);
        verificationCode.setExpiryTime(expiryTime);



        StringBuilder str = new StringBuilder();
        str.append("Mã xác nhận của bạn là: ");
        str.append("<b>");
        str.append(verificationCodeValue);
        str.append("</b>");

        String subject = "Xác nhận đặt lại mật khẩu";
//        emailService.sendEmail(account.getEmail(), subject, str.toString());
        mailServices.sendEmail(account.getEmail(), subject, str.toString(), false, true);
        return verificationRepository.save(verificationCode);
    }

    @Override
    public Account verifyCode(String code) {
        // Tìm mã xác nhận trong cơ sở dữ liệu
        VerificationCode verificationCode = verificationRepository.findByCode(code);

        if (verificationCode != null && isValid(verificationCode)) {
            // Mã xác nhận hợp lệ, trả về người dùng liên kết
            return verificationCode.getAccount();
        }

        // Mã xác nhận không hợp lệ hoặc đã hết hạn
        return null;
    }

    private boolean isValid(VerificationCode verificationCode) {
        // Kiểm tra xem mã xác nhận có hợp lệ và chưa hết hạn không
        LocalDateTime now = LocalDateTime.now();
        return verificationCode.getExpiryTime().isAfter(now);
    }

    private String generateRandomCode() {
        // Logic để tạo mã xác nhận ngẫu nhiên (ví dụ: sử dụng thư viện RandomStringUtils)
        return RandomUtils.randomAlphanumeric(6);
    }
}
