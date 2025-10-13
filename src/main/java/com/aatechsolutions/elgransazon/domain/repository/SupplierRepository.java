package com.aatechsolutions.elgransazon.domain.repository;

import com.aatechsolutions.elgransazon.domain.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Supplier entity
 * Provides CRUD operations and custom queries for suppliers
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find a supplier by its name
     * @param name the supplier name
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> findByName(String name);

    /**
     * Find all active suppliers ordered by name
     * @return List of active suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE s.active = true ORDER BY s.name ASC")
    List<Supplier> findAllActiveOrderedByName();

    /**
     * Find all suppliers ordered by name
     * @return List of all suppliers
     */
    @Query("SELECT s FROM Supplier s ORDER BY s.name ASC")
    List<Supplier> findAllOrderedByName();

    /**
     * Check if a supplier name already exists (case-insensitive)
     * @param name the supplier name to check
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Count active suppliers
     * @return number of active suppliers
     */
    long countByActiveTrue();

    /**
     * Search suppliers by name, contact person, or email
     * @param searchTerm the search term
     * @return List of matching suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY s.name ASC")
    List<Supplier> searchSuppliers(@Param("searchTerm") String searchTerm);

    /**
     * Find suppliers by rating
     * @param rating the rating (1-5)
     * @return List of suppliers with the specified rating
     */
    @Query("SELECT s FROM Supplier s WHERE s.rating = :rating AND s.active = true ORDER BY s.name ASC")
    List<Supplier> findByRating(@Param("rating") Integer rating);

    /**
     * Find top rated suppliers (4-5 stars)
     * @return List of highly rated suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE s.rating >= 4 AND s.active = true ORDER BY s.rating DESC, s.name ASC")
    List<Supplier> findTopRatedSuppliers();

    /**
     * Count inactive suppliers
     * @return number of inactive suppliers
     */
    long countByActiveFalse();
}
