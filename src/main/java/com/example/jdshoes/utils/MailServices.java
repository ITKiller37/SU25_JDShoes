package com.example.jdshoes.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@EnableAsync
public class MailServices {

    @Value("${app.logo.url}")
    private String logoUrl;

    @Value("${app.login.url}")
    private String loginUrl;

    private final Logger log = LoggerFactory.getLogger(MailServices.class);

    @Autowired
    private JavaMailSender javaMailSender;


    final static String username = "quangducnguyen2107@gmail.com";

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug(
                "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart,
                isHtml,
                to,
                subject,
                content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(username);
            message.setSubject(subject);
            System.out.println("subject: "+subject);
            message.setText(content, isHtml);
            System.out.println("content: "+content);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }


    public String buildEmailTemplate(String username, String email, String pass) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                <div style="text-align: center;">
                    <img src="%s" alt="Logo" style="max-width: 150px; margin-bottom: 20px;" />
                </div>
                <h2 style="color: #333;">Chào mừng %s!</h2>
                <p style="color: #555;">Bạn đã đăng ký tài khoản thành công trên hệ thống của chúng tôi.</p>
                
                 <p style="margin-top: 20px;"><strong>Thông tin đăng nhập của bạn:</strong></p>
                <ul style="color: #555; line-height: 1.6;">
                    <li><strong>Tài khoản:</strong> %s</li>
                    <li><strong>Mật khẩu:</strong> %s</li>
                </ul>
                <p>Để đăng nhập, vui lòng bấm vào nút dưới đây:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="display: inline-block; background-color: #007bff; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px;">
                        Đăng nhập
                    </a>
                </div>
                <p style="font-size: 13px; color: #999;">Nếu bạn không tạo tài khoản, vui lòng bỏ qua email này.</p>
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                <div style="font-size: 14px; color: #666;">
                    <strong>Tên cửa hàng:</strong> Cửa hàng JDShoes<br/>
                    <strong>Điện thoại:</strong> 0123 456 789<br/>
                    <strong>Địa chỉ:</strong> 123 Đường ABC, Quận XYZ, TP. HCM
                </div>
            </div>
        </body>
        </html>
        """.formatted(logoUrl, username, email, pass, loginUrl);
    }
}
