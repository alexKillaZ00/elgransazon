package com.aatechsolutions.elgransazon.infrastructure.security;

import com.aatechsolutions.elgransazon.domain.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler
 * Redirects users to role-specific pages after successful login
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        log.info("User {} logged in successfully", authentication.getName());
        
        String targetUrl = determineTargetUrl(authentication);
        log.debug("Redirecting user {} to {}", authentication.getName(), targetUrl);
        
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
