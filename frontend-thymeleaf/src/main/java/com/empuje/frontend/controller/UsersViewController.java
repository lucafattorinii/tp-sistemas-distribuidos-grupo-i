package com.empuje.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersViewController {

    @GetMapping("/users")
    public String usersPage() {
        return "users";
    }
}
