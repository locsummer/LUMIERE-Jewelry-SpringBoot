package com.agile.jewelryshop.service;

import com.agile.jewelryshop.dto.SupportTicketForm;
import com.agile.jewelryshop.model.*;
import com.agile.jewelryshop.repository.OrderRepository;
import com.agile.jewelryshop.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SupportService {
    private final SupportTicketRepository ticketRepository;
    private final OrderRepository orderRepository;

    public SupportService(SupportTicketRepository ticketRepository, OrderRepository orderRepository) {
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public SupportTicket create(User customer, SupportTicketForm form) {
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode(generateCode());
        ticket.setCustomer(customer);
        ticket.setCategory(form.getCategory());
        ticket.setSubject(form.getSubject().trim());
        ticket.setPriority(priorityFor(form.getCategory()));
        ticket.setStatus(SupportStatus.OPEN);
        if (form.getOrderId() != null) {
            ticket.setOrder(orderRepository.findByIdAndUserId(form.getOrderId(), customer.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng được chọn không hợp lệ")));
        }
        SupportMessage message = new SupportMessage();
        message.setSender(customer);
        message.setStaffMessage(false);
        message.setContent(form.getMessage().trim());
        ticket.addMessage(message);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void customerReply(SupportTicket ticket, User customer, String content) {
        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền phản hồi yêu cầu này");
        }
        if (ticket.getStatus() == SupportStatus.CLOSED) {
            throw new IllegalArgumentException("Yêu cầu đã đóng và không thể phản hồi thêm");
        }
        SupportMessage message = message(customer, content, false);
        ticket.addMessage(message);
        ticket.setStatus(SupportStatus.OPEN);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void staffReply(SupportTicket ticket, User staff, String content) {
        if (staff.getRole() != Role.ADMIN && staff.getRole() != Role.STAFF) {
            throw new IllegalArgumentException("Tài khoản không có quyền xử lý hỗ trợ");
        }
        if (ticket.getStatus() == SupportStatus.CLOSED) {
            throw new IllegalArgumentException("Yêu cầu đã đóng và không thể phản hồi thêm");
        }
        SupportMessage message = message(staff, content, true);
        ticket.addMessage(message);
        ticket.setAssignedTo(staff);
        ticket.setStatus(SupportStatus.WAITING_CUSTOMER);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void updateStatus(SupportTicket ticket, User staff, SupportStatus status) {
        ticket.setAssignedTo(staff);
        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }

    private SupportMessage message(User sender, String content, boolean staffMessage) {
        String clean = content == null ? "" : content.trim();
        if (clean.length() < 2 || clean.length() > 3000) {
            throw new IllegalArgumentException("Nội dung phản hồi phải từ 2 đến 3.000 ký tự");
        }
        SupportMessage message = new SupportMessage();
        message.setSender(sender);
        message.setContent(clean);
        message.setStaffMessage(staffMessage);
        return message;
    }

    private SupportPriority priorityFor(SupportCategory category) {
        if (category == SupportCategory.PAYMENT || category == SupportCategory.REFUND) {
            return SupportPriority.URGENT;
        }
        if (category == SupportCategory.ORDER || category == SupportCategory.DELIVERY
                || category == SupportCategory.FOOD) {
            return SupportPriority.HIGH;
        }
        return SupportPriority.NORMAL;
    }

    private String generateCode() {
        return "CSKH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(10, 99);
    }
}
