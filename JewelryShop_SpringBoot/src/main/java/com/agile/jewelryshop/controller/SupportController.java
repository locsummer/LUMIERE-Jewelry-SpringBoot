package com.agile.jewelryshop.controller;

import com.agile.jewelryshop.dto.SupportTicketForm;
import com.agile.jewelryshop.model.SupportCategory;
import com.agile.jewelryshop.model.SupportTicket;
import com.agile.jewelryshop.model.User;
import com.agile.jewelryshop.repository.OrderRepository;
import com.agile.jewelryshop.repository.SupportTicketRepository;
import com.agile.jewelryshop.service.CurrentUserService;
import com.agile.jewelryshop.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SupportController {
    private final CurrentUserService currentUserService;
    private final SupportTicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final SupportService supportService;

    public SupportController(CurrentUserService currentUserService,
                             SupportTicketRepository ticketRepository,
                             OrderRepository orderRepository,
                             SupportService supportService) {
        this.currentUserService = currentUserService;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.supportService = supportService;
    }

    @GetMapping("/support")
    public String center() { return "support/index"; }

    @GetMapping("/support/tickets")
    public String tickets(Authentication authentication, Model model) {
        User customer = currentUserService.require(authentication);
        model.addAttribute("tickets", ticketRepository.findByCustomerIdOrderByUpdatedAtDesc(customer.getId()));
        return "support/list";
    }

    @GetMapping("/support/tickets/new")
    public String createForm(Authentication authentication, Model model) {
        User customer = currentUserService.require(authentication);
        model.addAttribute("supportForm", new SupportTicketForm());
        populateForm(model, customer);
        return "support/form";
    }

    @PostMapping("/support/tickets/new")
    public String create(@Valid @ModelAttribute("supportForm") SupportTicketForm form,
                         BindingResult result, Authentication authentication, Model model,
                         RedirectAttributes ra) {
        User customer = currentUserService.require(authentication);
        if (result.hasErrors()) {
            populateForm(model, customer);
            return "support/form";
        }
        try {
            SupportTicket ticket = supportService.create(customer, form);
            ra.addFlashAttribute("success", "Đã gửi yêu cầu " + ticket.getTicketCode()
                    + ". Bộ phận chăm sóc khách hàng sẽ sớm phản hồi.");
            return "redirect:/support/tickets/" + ticket.getId();
        } catch (IllegalArgumentException ex) {
            result.reject("support", ex.getMessage());
            populateForm(model, customer);
            return "support/form";
        }
    }

    @GetMapping("/support/tickets/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        User customer = currentUserService.require(authentication);
        model.addAttribute("ticket", requireOwned(id, customer));
        return "support/detail";
    }

    @PostMapping("/support/tickets/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam String content,
                        Authentication authentication, RedirectAttributes ra) {
        User customer = currentUserService.require(authentication);
        try {
            supportService.customerReply(requireOwned(id, customer), customer, content);
            ra.addFlashAttribute("success", "Đã gửi phản hồi đến bộ phận chăm sóc khách hàng");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/support/tickets/" + id;
    }

    private SupportTicket requireOwned(Long id, User customer) {
        return ticketRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ"));
    }

    private void populateForm(Model model, User customer) {
        model.addAttribute("categories", SupportCategory.values());
        model.addAttribute("orders", orderRepository.findByUserIdOrderByCreatedAtDesc(customer.getId()));
    }
}
