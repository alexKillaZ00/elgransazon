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
     * Find an employee by their name (used as username for login)
     * 
     * @param nombre the employee's first name
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByNombre(String nombre);

    /**
     * Check if an employee exists by name
     * 
     * @param nombre the employee's first name
     * @return true if employee exists, false otherwise
     */
    boolean existsByNombre(String nombre);
}
