package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.Supplier;
import com.aatechsolutions.elgransazon.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of SupplierService
 * Handles business logic for supplier operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        log.debug("Fetching all suppliers");
        return supplierRepository.findAllOrderedByName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllActiveSuppliers() {
        log.debug("Fetching all active suppliers");
        return supplierRepository.findAllActiveOrderedByName();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(Long id) {
        log.debug("Fetching supplier with id: {}", id);
        return supplierRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByName(String name) {
        log.debug("Fetching supplier with name: {}", name);
        return supplierRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String searchTerm) {
        log.debug("Searching suppliers with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllSuppliers();
        }
        return supplierRepository.searchSuppliers(searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersByRating(Integer rating) {
        log.debug("Fetching suppliers with rating: {}", rating);
        return supplierRepository.findByRating(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getTopRatedSuppliers() {
        log.debug("Fetching top rated suppliers");
        return supplierRepository.findTopRatedSuppliers();
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        log.info("Creating new supplier: {}", supplier.getName());

        // Validate that supplier name doesn't already exist
        if (supplierRepository.existsByNameIgnoreCase(supplier.getName())) {
            log.error("Supplier name already exists: {}", supplier.getName());
            throw new IllegalArgumentException("Supplier with name '" + supplier.getName() + "' already exists");
        }

        // Set default values if not provided
        if (supplier.getActive() == null) {
            supplier.setActive(true);
        }

        // Validate rating range if provided
        if (supplier.getRating() != null && (supplier.getRating() < 1 || supplier.getRating() > 5)) {
            log.error("Invalid rating value: {}", supplier.getRating());
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier created successfully with id: {}", savedSupplier.getIdSupplier());
        return savedSupplier;
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplier) {
        log.info("Updating supplier with id: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", id);
                    return new IllegalArgumentException("Supplier not found with id: " + id);
                });

        // Check if name is being changed and if the new name already exists
        if (!existingSupplier.getName().equalsIgnoreCase(supplier.getName())) {
            if (supplierRepository.existsByNameIgnoreCase(supplier.getName())) {
                log.error("Supplier name already exists: {}", supplier.getName());
                throw new IllegalArgumentException("Supplier with name '" + supplier.getName() + "' already exists");
            }
        }

        // Validate rating range if provided
        if (supplier.getRating() != null && (supplier.getRating() < 1 || supplier.getRating() > 5)) {
            log.error("Invalid rating value: {}", supplier.getRating());
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Update fields
        existingSupplier.setName(supplier.getName());
        existingSupplier.setContactPerson(supplier.getContactPerson());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setNotes(supplier.getNotes());
        existingSupplier.setActive(supplier.getActive());
        existingSupplier.setRating(supplier.getRating());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        log.info("Supplier updated successfully: {}", updatedSupplier.getIdSupplier());
        return updatedSupplier;
    }

    @Override
    public void deleteSupplier(Long id) {
        log.info("Soft deleting supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", id);
                    return new IllegalArgumentException("Supplier not found with id: " + id);
                });

        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Supplier soft deleted successfully: {}", id);
    }

    @Override
    public void permanentlyDeleteSupplier(Long id) {
        log.warn("Permanently deleting supplier with id: {}", id);

        if (!supplierRepository.existsById(id)) {
            log.error("Supplier not found with id: {}", id);
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }

        supplierRepository.deleteById(id);
        log.info("Supplier permanently deleted: {}", id);
    }

    @Override
    public void activateSupplier(Long id) {
        log.info("Activating supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", id);
                    return new IllegalArgumentException("Supplier not found with id: " + id);
                });

        supplier.setActive(true);
        supplierRepository.save(supplier);
        log.info("Supplier activated successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean supplierNameExists(String name) {
        log.debug("Checking if supplier name exists: {}", name);
        return supplierRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveSuppliers() {
        log.debug("Counting active suppliers");
        return supplierRepository.countByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countInactiveSuppliers() {
        log.debug("Counting inactive suppliers");
        return supplierRepository.countByActiveFalse();
    }
}
