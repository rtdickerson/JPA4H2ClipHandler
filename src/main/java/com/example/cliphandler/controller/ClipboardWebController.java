package com.example.cliphandler.controller;

import com.example.cliphandler.service.ClipboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ClipboardWebController {

    private final ClipboardService service;

    public ClipboardWebController(ClipboardService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String username) {
        boolean filtered = username != null && !username.isBlank();
        model.addAttribute("items", filtered ? service.findByUsername(username) : service.findAll());
        model.addAttribute("filterUser", filtered ? username : "");
        return "index";
    }

    @PostMapping("/clear/{username}/{name}")
    public String clearItem(
            @PathVariable String username,
            @PathVariable String name,
            @RequestParam(required = false, defaultValue = "") String filterUser) {
        service.delete(username, name);
        return filterUser.isBlank() ? "redirect:/" : "redirect:/?username=" + filterUser;
    }
}
