package com.aatechsolutions.elgransazon.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Chef role views
 * Handles chef-related pages for managing kitchen orders
 */
@Controller
@RequestMapping("/chef")
@Slf4j
public class ChefController {

    /**
     * Display chef dashboard
     * 
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model
     * @return chef dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Chef {} accessed dashboard", username);
        
        model.addAttribute("username", username);
        model.addAttribute("role", "Chef");
        
        return "chef/dashboard";
    }
}
