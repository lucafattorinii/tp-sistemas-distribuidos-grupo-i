package com.empuje.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/health")
    public String health(Model model) {
        model.addAttribute("status", "ok");
        return "health";
    }
}
