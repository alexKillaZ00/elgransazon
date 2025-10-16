package com.aatechsolutions.elgransazon.infrastructure.security;

import com.aatechsolutions.elgransazon.domain.entity.Employee;
import com.aatechsolutions.elgransazon.domain.repository.EmployeeRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter to validate user enabled status on each request
 * Invalidates session if user is disabled
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidationFilter extends OncePerRequestFilter {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Only validate if user is authenticated
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            
            String username = authentication.getName();
            
            // Check if user still exists and is enabled
            Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
            
            if (employeeOpt.isEmpty() || !employeeOpt.get().getEnabled()) {
                log.warn("User {} is disabled or doesn't exist. Invalidating session.", username);
                
                // Invalidate session and clear security context
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                
                // Redirect to login without message
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // Don't filter login page, static resources, and logout
        return path.startsWith("/login") || 
               path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/images/") ||
               path.equals("/logout") ||
               path.equals("/perform_login");
    }
}
