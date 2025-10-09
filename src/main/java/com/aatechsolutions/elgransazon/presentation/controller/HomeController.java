package com.aatechsolutions.elgransazon.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for home/dashboard views
 */
@Controller
@Slf4j
public class HomeController {

    /**
     * Display home/dashboard page
     * 
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model
     * @return home view name
     */
    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            String username = authentication.getName();
            log.info("User {} accessed home page", username);
            model.addAttribute("username", username);
        }
        
        return "home";
    }
}
