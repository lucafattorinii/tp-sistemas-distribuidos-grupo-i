package com.empuje.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventsViewController {

    @GetMapping("/events")
    public String eventsPage() {
        return "events";
    }

    @GetMapping("/events/externos")
    public String eventosExternos() {
        return "events/externos";
    }
}
