package com.empuje.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
public class ExternalEventsController {

    @GetMapping("/externos")
    public String eventosExternos(Model model) {
        // Aquí se consultarían los eventos externos desde el servicio de mensajería
        model.addAttribute("title", "Eventos Externos");
        model.addAttribute("page", "events/externos");
        return "layout";
    }
}
