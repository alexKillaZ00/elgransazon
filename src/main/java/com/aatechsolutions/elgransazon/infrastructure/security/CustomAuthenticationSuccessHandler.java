package com.aatechsolutions.elgransazon.infrastructure.security;

import com.aatechsolutions.elgransazon.application.service.EmployeeService;
import com.aatechsolutions.elgransazon.domain.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler
 * Redirects users to role-specific pages after successful login
 * and updates last access timestamp
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeService employeeService;
    
    public CustomAuthenticationSuccessHandler(@Lazy EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        log.info("User {} logged in successfully", username);
        
        // Update last access timestamp
        try {
            employeeService.updateLastAccess(username);
            log.debug("Updated last access for user {}", username);
        } catch (Exception e) {
            log.error("Error updating last access for user {}", username, e);
            // Don't fail login if last access update fails
        }
        
        String targetUrl = determineTargetUrl(authentication);
        log.debug("Redirecting user {} to {}", username, targetUrl);
        
        response.sendRedirect(targetUrl);
    }

    /**
     * Determine the target URL based on user's roles
     * Priority: ADMIN > CHEF > WAITER > default
     */
    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            log.debug("Checking role: {}", role);
            
            if (Role.ADMIN.equals(role)) {
                return "/admin/dashboard";
            } else if (Role.CHEF.equals(role)) {
                return "/chef/dashboard";
            } else if (Role.WAITER.equals(role)) {
                return "/waiter/dashboard";
            }
        }
        
        // Default redirect
        log.warn("No specific role found for user {}, redirecting to default home", authentication.getName());
        return "/home";
    }
}
