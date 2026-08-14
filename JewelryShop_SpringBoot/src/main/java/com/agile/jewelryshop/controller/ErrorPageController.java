package com.agile.jewelryshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorPageController {
    @GetMapping("/error/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden(@RequestParam(required = false) String reason, Model model) {
        model.addAttribute("csrfError", "csrf".equals(reason));
        return "error/403";
    }
}
