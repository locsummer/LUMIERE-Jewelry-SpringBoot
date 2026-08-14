package com.agile.jewelryshop.service;

import com.agile.jewelryshop.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;
    @Value("${app.mail.from:no-reply@lumiere.local}")
    private String from;

    public MailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean sendOrderConfirmation(Order order) {
        if (!enabled) {
            log.info("Email đang tắt. Đơn {} vẫn được lưu thành công.", order.getOrderCode());
            return false;
        }
        try {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender == null) {
                log.warn("Email đã bật nhưng chưa có cấu hình MAIL_USERNAME/MAIL_PASSWORD.");
                return false;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(order.getCustomerEmail());
            message.setSubject("Xác nhận đơn hàng " + order.getOrderCode());
            message.setText("Xin chào " + order.getCustomerName() + ",\n\nĐơn hàng " + order.getOrderCode()
                    + " đã được ghi nhận. Tổng tiền: " + order.getTotalAmount() + " VNĐ.\n"
                    + "Trạng thái: " + order.getStatus().getLabel() + ".\n\nCảm ơn bạn đã đặt sản phẩm!");
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            log.warn("Không gửi được email cho đơn {}: {}", order.getOrderCode(), ex.getMessage());
            return false;
        }
    }
}
