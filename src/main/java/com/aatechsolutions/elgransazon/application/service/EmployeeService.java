package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.Employee;
import com.aatechsolutions.elgransazon.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Employee management
 * Handles business logic for employee operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Find all employees
     * 
     * @return List of all employees
     */
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        log.debug("Finding all employees");
        return employeeRepository.findAll();
    }

    /**
     * Find employee by ID
     * 
     * @param id Employee ID
     * @return Optional containing the employee if found
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        log.debug("Finding employee by id: {}", id);
        return employeeRepository.findById(id);
    }

    /**
     * Find employee by username
     * 
     * @param username Employee's username
     * @return Optional containing the employee if found
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findByUsername(String username) {
        log.debug("Finding employee by username: {}", username);
        return employeeRepository.findByUsername(username);
    }

    /**
     * Find employee by email
     * 
     * @param email Employee's email
     * @return Optional containing the employee if found
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findByEmail(String email) {
        log.debug("Finding employee by email: {}", email);
        return employeeRepository.findByEmail(email);
    }

    /**
     * Create a new employee
     * Encodes the password before saving
     * 
     * @param employee Employee to create
     * @return Created employee
     * @throws IllegalArgumentException if employee with same nombre already exists
     */
    @Transactional
    public Employee create(Employee employee) {
        log.info("Creating new employee: {}", employee.getUsername());

        if (employeeRepository.existsByUsername(employee.getUsername())) {
            log.error("Employee with username {} already exists", employee.getUsername());
            throw new IllegalArgumentException("Employee with username '" + employee.getUsername() + "' already exists");
        }

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            log.error("Employee with email {} already exists", employee.getEmail());
            throw new IllegalArgumentException("Employee with email '" + employee.getEmail() + "' already exists");
        }

        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(employee.getContrasenia());
        employee.setContrasenia(encodedPassword);

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getIdEmpleado());
        
        return savedEmployee;
    }

    /**
     * Update an existing employee
     * 
     * @param id Employee ID to update
     * @param employeeDetails Updated employee details
     * @return Updated employee
     * @throws IllegalArgumentException if employee not found
     */
    @Transactional
    public Employee update(Long id, Employee employeeDetails) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new IllegalArgumentException("Employee not found with id: " + id);
                });

        // Check if username is being changed and if it's already taken
        if (!employee.getUsername().equals(employeeDetails.getUsername()) &&
            employeeRepository.existsByUsername(employeeDetails.getUsername())) {
            log.error("Username {} already exists", employeeDetails.getUsername());
            throw new IllegalArgumentException("Username '" + employeeDetails.getUsername() + "' already exists");
        }

        // Check if email is being changed and if it's already taken
        if (!employee.getEmail().equals(employeeDetails.getEmail()) &&
            employeeRepository.existsByEmail(employeeDetails.getEmail())) {
            log.error("Email {} already exists", employeeDetails.getEmail());
            throw new IllegalArgumentException("Email '" + employeeDetails.getEmail() + "' already exists");
        }

        employee.setUsername(employeeDetails.getUsername());
        employee.setNombre(employeeDetails.getNombre());
        employee.setApellido(employeeDetails.getApellido());
        employee.setEmail(employeeDetails.getEmail());
        employee.setEnabled(employeeDetails.getEnabled());

        // Only update password if it's provided and different
        if (employeeDetails.getContrasenia() != null && 
            !employeeDetails.getContrasenia().isEmpty() &&
            !employee.getContrasenia().equals(employeeDetails.getContrasenia())) {
            String encodedPassword = passwordEncoder.encode(employeeDetails.getContrasenia());
            employee.setContrasenia(encodedPassword);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", updatedEmployee.getIdEmpleado());
        
        return updatedEmployee;
    }

    /**
     * Delete an employee
     * 
     * @param id Employee ID to delete
     * @throws IllegalArgumentException if employee not found
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.error("Employee not found with id: {}", id);
            throw new IllegalArgumentException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully: {}", id);
    }

    /**
     * Change employee password
     * 
     * @param id Employee ID
     * @param newPassword New password (plain text)
     * @throws IllegalArgumentException if employee not found
     */
    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new IllegalArgumentException("Employee not found with id: " + id);
                });

        String encodedPassword = passwordEncoder.encode(newPassword);
        employee.setContrasenia(encodedPassword);
        
        employeeRepository.save(employee);
        log.info("Password changed successfully for employee: {}", id);
    }

    /**
     * Enable or disable an employee
     * 
     * @param id Employee ID
     * @param enabled Enable status
     * @throws IllegalArgumentException if employee not found
     */
    @Transactional
    public void setEnabled(Long id, boolean enabled) {
        log.info("Setting enabled status to {} for employee with id: {}", enabled, id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new IllegalArgumentException("Employee not found with id: " + id);
                });

        employee.setEnabled(enabled);
        employeeRepository.save(employee);
        
        log.info("Employee enabled status updated: {}", id);
    }
}
