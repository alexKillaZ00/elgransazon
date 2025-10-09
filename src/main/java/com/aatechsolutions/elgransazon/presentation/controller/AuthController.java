package com.aatechsolutions.elgransazon.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for authentication-related views
 * Handles login and logout pages
 */
@Controller
@Slf4j
public class AuthController {

    /**
     * Display login page
     * 
     * @param error indicates if there was a login error
     * @param logout indicates if user just logged out
     * @param model Spring MVC model
     * @return login view name
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        
        if (error != null) {
            log.warn("Login attempt failed");
            model.addAttribute("error", "Invalid username or password");
        }
        
        if (logout != null) {
            log.info("User logged out");
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        return "auth/login";
    }
}
