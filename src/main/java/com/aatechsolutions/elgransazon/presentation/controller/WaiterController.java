package com.aatechsolutions.elgransazon.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Waiter role views
 * Handles waiter-related pages for taking orders and managing tables
 */
@Controller
@RequestMapping("/waiter")
@Slf4j
public class WaiterController {

    /**
     * Display waiter dashboard
     * 
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model
     * @return waiter dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Waiter {} accessed dashboard", username);
        
        model.addAttribute("username", username);
        model.addAttribute("role", "Waiter");
        
        return "waiter/dashboard";
    }
}
