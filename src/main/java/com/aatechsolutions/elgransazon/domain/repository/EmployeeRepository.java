package com.aatechsolutions.elgransazon.domain.repository;

import com.aatechsolutions.elgransazon.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Employee entity
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find an employee by their username (used for login)
     * 
     * @param username the employee's username
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByUsername(String username);

    /**
     * Find an employee by their email
     * 
     * @param email the employee's email
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if an employee exists by username
     * 
     * @param username the employee's username
     * @return true if employee exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if an employee exists by email
     * 
     * @param email the employee's email
     * @return true if employee exists, false otherwise
     */
    boolean existsByEmail(String email);
}
