package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.Employee;
import com.aatechsolutions.elgransazon.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security
 * Loads user details from the Employee entity
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== Starting authentication for user: {} ===", username);
        
        try {
            Employee employee = employeeRepository.findByNombre(username)
                    .orElseThrow(() -> {
                        log.error("Employee not found with username: {}", username);
                        return new UsernameNotFoundException("Employee not found with username: " + username);
                    });

            log.info("Employee found: {} (ID: {})", employee.getFullName(), employee.getIdEmpleado());
            log.info("Employee enabled: {}", employee.getEnabled());
            
            // Try to access roles
            try {
                log.info("Attempting to load roles...");
                int rolesCount = employee.getRoles() != null ? employee.getRoles().size() : 0;
                log.info("Roles loaded successfully. Count: {}", rolesCount);
                
                if (rolesCount > 0) {
                    employee.getRoles().forEach(role -> 
                        log.info("  - Role: {} (ID: {})", role.getNombreRol(), role.getIdRol())
                    );
                } else {
                    log.warn("WARNING: Employee {} has NO ROLES assigned!", username);
                }
            } catch (Exception e) {
                log.error("ERROR loading roles: {}", e.getMessage(), e);
                throw e;
            }

            log.info("Building UserDetails...");
            UserDetails userDetails = User.builder()
                    .username(employee.getNombre())
                    .password(employee.getContrasenia())
                    .disabled(!employee.getEnabled())
                    .authorities(getAuthorities(employee))
                    .build();
            
            log.info("=== Authentication successful for user: {} ===", username);
            return userDetails;
            
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", username);
            throw e;
        } catch (Exception e) {
            log.error("CRITICAL ERROR during authentication for user {}: {}", username, e.getMessage(), e);
            log.error("Exception type: {}", e.getClass().getName());
            if (e.getCause() != null) {
                log.error("Caused by: {}", e.getCause().getMessage(), e.getCause());
            }
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

    /**
     * Returns the authorities/roles for the user from their assigned roles
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Employee employee) {
        if (employee.getRoles().isEmpty()) {
            log.warn("Employee {} has no roles assigned, granting default EMPLOYEE role", employee.getNombre());
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        }

        return employee.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombreRol()))
                .toList();
    }
}
