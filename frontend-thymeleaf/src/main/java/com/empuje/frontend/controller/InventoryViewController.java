package com.empuje.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventoryViewController {

    @GetMapping("/inventory")
    public String inventoryPage() {
        return "inventory";
    }
}
