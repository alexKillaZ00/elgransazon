package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.Supplier;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Supplier business logic
 */
public interface SupplierService {

    /**
     * Get all suppliers ordered by name
     * @return List of all suppliers
     */
    List<Supplier> getAllSuppliers();

    /**
     * Get all active suppliers ordered by name
     * @return List of active suppliers
     */
    List<Supplier> getAllActiveSuppliers();

    /**
     * Get a supplier by its ID
     * @param id the supplier ID
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> getSupplierById(Long id);

    /**
     * Get a supplier by its name
     * @param name the supplier name
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> getSupplierByName(String name);

    /**
     * Search suppliers by term (name, contact person, email)
     * @param searchTerm the search term
     * @return List of matching suppliers
     */
    List<Supplier> searchSuppliers(String searchTerm);

    /**
     * Get suppliers by rating
     * @param rating the rating (1-5)
     * @return List of suppliers with the specified rating
     */
    List<Supplier> getSuppliersByRating(Integer rating);

    /**
     * Get top rated suppliers (4-5 stars)
     * @return List of highly rated suppliers
     */
    List<Supplier> getTopRatedSuppliers();

    /**
     * Create a new supplier
     * @param supplier the supplier to create
     * @return the created supplier
     * @throws IllegalArgumentException if supplier name already exists
     */
    Supplier createSupplier(Supplier supplier);

    /**
     * Update an existing supplier
     * @param id the ID of the supplier to update
     * @param supplier the updated supplier data
     * @return the updated supplier
     * @throws IllegalArgumentException if supplier not found or name already exists
     */
    Supplier updateSupplier(Long id, Supplier supplier);

    /**
     * Delete a supplier (soft delete by setting active to false)
     * @param id the ID of the supplier to delete
     * @throws IllegalArgumentException if supplier not found
     */
    void deleteSupplier(Long id);

    /**
     * Hard delete a supplier from database
     * @param id the ID of the supplier to permanently delete
     * @throws IllegalArgumentException if supplier not found
     */
    void permanentlyDeleteSupplier(Long id);

    /**
     * Activate a supplier
     * @param id the ID of the supplier to activate
     * @throws IllegalArgumentException if supplier not found
     */
    void activateSupplier(Long id);

    /**
     * Check if a supplier name already exists
     * @param name the supplier name to check
     * @return true if exists, false otherwise
     */
    boolean supplierNameExists(String name);

    /**
     * Get count of active suppliers
     * @return number of active suppliers
     */
    long countActiveSuppliers();

    /**
     * Get count of inactive suppliers
     * @return number of inactive suppliers
     */
    long countInactiveSuppliers();
}
