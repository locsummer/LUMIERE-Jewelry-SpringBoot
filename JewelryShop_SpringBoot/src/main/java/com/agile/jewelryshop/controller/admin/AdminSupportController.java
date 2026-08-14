package com.agile.jewelryshop.controller.admin;

import com.agile.jewelryshop.model.*;
import com.agile.jewelryshop.repository.SupportTicketRepository;
import com.agile.jewelryshop.service.CurrentUserService;
import com.agile.jewelryshop.service.SupportService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/support")
public class AdminSupportController {
    private final SupportTicketRepository ticketRepository;
    private final CurrentUserService currentUserService;
    private final SupportService supportService;

    public AdminSupportController(SupportTicketRepository ticketRepository,
                                  CurrentUserService currentUserService,
                                  SupportService supportService) {
        this.ticketRepository = ticketRepository;
        this.currentUserService = currentUserService;
        this.supportService = supportService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) SupportStatus status,
                       @RequestParam(required = false) SupportCategory category,
                       @RequestParam(required = false) String q, Model model) {
        var tickets = ticketRepository.findAllByOrderByUpdatedAtDesc();
        if (status != null) tickets = tickets.stream().filter(t -> t.getStatus() == status).toList();
        if (category != null) tickets = tickets.stream().filter(t -> t.getCategory() == category).toList();
        if (q != null && !q.isBlank()) {
            String keyword = q.trim().toLowerCase();
            tickets = tickets.stream().filter(t -> t.getTicketCode().toLowerCase().contains(keyword)
                    || t.getSubject().toLowerCase().contains(keyword)
                    || t.getCustomer().getFullName().toLowerCase().contains(keyword)
                    || t.getCustomer().getEmail().toLowerCase().contains(keyword)).toList();
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("statuses", SupportStatus.values());
        model.addAttribute("categories", SupportCategory.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", q);
        model.addAttribute("openCount", ticketRepository.countByStatus(SupportStatus.OPEN));
        model.addAttribute("processingCount", ticketRepository.countByStatus(SupportStatus.IN_PROGRESS));
        model.addAttribute("waitingCount", ticketRepository.countByStatus(SupportStatus.WAITING_CUSTOMER));
        return "admin/support/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", requireTicket(id));
        model.addAttribute("statuses", SupportStatus.values());
        return "admin/support/detail";
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam String content,
                        Authentication authentication, RedirectAttributes ra) {
        try {
            supportService.staffReply(requireTicket(id), currentUserService.require(authentication), content);
            ra.addFlashAttribute("success", "Đã gửi phản hồi cho khách hàng");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/support/" + id;
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, @RequestParam SupportStatus status,
                         Authentication authentication, RedirectAttributes ra) {
        supportService.updateStatus(requireTicket(id), currentUserService.require(authentication), status);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái yêu cầu");
        return "redirect:/admin/support/" + id;
    }

    private SupportTicket requireTicket(Long id) {
        return ticketRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ"));
    }
}
