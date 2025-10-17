package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.RestaurantTable;
import com.aatechsolutions.elgransazon.domain.entity.TableStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for RestaurantTable management
 */
public interface RestaurantTableService {

    /**
     * Find all tables
     */
    List<RestaurantTable> findAll();

    /**
     * Find all tables ordered by table number
     */
    List<RestaurantTable> findAllOrderByTableNumber();

    /**
     * Find table by ID
     */
    Optional<RestaurantTable> findById(Long id);

    /**
     * Find table by table number
     */
    Optional<RestaurantTable> findByTableNumber(Integer tableNumber);

    /**
     * Create a new table
     */
    RestaurantTable create(RestaurantTable table, String username);

    /**
     * Update an existing table
     */
    RestaurantTable update(Long id, RestaurantTable table, String username);

    /**
     * Change table status
     */
    RestaurantTable changeStatus(Long id, TableStatus status, String username);

    /**
     * Find tables by status
     */
    List<RestaurantTable> findByStatus(TableStatus status);

    /**
     * Find available tables
     */
    List<RestaurantTable> findAvailableTables();

    /**
     * Find tables by location
     */
    List<RestaurantTable> findByLocation(String location);

    /**
     * Find tables with minimum capacity
     */
    List<RestaurantTable> findByMinimumCapacity(Integer capacity);

    /**
     * Count tables by status
     */
    long countByStatus(TableStatus status);

    /**
     * Count all tables
     */
    long countAll();

    /**
     * Check if table number exists
     */
    boolean existsByTableNumber(Integer tableNumber);

    /**
     * Check if table number exists excluding a specific id (for updates)
     */
    boolean existsByTableNumberAndIdNot(Integer tableNumber, Long excludeId);

    /**
     * Get all distinct locations
     */
    List<String> getDistinctLocations();
}
