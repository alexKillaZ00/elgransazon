package com.aatechsolutions.elgransazon.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Administrator role views
 * Handles all admin-related pages and operations
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    /**
     * Display admin dashboard
     * 
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model
     * @return admin dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Admin {} accessed dashboard", username);
        
        model.addAttribute("username", username);
        model.addAttribute("role", "Administrator");
        
        return "admin/dashboard";
    }
     @GetMapping("/reservations")
    public String reservations(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Admin {} accessed reservations", username);

        model.addAttribute("username", username);
        model.addAttribute("role", "Administrator");

        return "admin/reservations";
    }
    
    @GetMapping("/inventory")
    public String inventory(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Admin {} accessed inventory", username);

        model.addAttribute("username", username);
        model.addAttribute("role", "Administrator");

        return "admin/inventory";
    }
}
