package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.RestaurantTable;
import com.aatechsolutions.elgransazon.domain.entity.TableStatus;
import com.aatechsolutions.elgransazon.domain.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of RestaurantTableService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Override
    public List<RestaurantTable> findAll() {
        log.debug("Fetching all restaurant tables");
        return tableRepository.findAll();
    }

    @Override
    public List<RestaurantTable> findAllOrderByTableNumber() {
        log.debug("Fetching all restaurant tables ordered by table number");
        return tableRepository.findAllOrderByTableNumber();
    }

    @Override
    public Optional<RestaurantTable> findById(Long id) {
        log.debug("Finding restaurant table by ID: {}", id);
        return tableRepository.findById(id);
    }

    @Override
    public Optional<RestaurantTable> findByTableNumber(Integer tableNumber) {
        log.debug("Finding restaurant table by table number: {}", tableNumber);
        return tableRepository.findByTableNumber(tableNumber);
    }

    @Override
    @Transactional
    public RestaurantTable create(RestaurantTable table, String username) {
        log.info("Creating new restaurant table: {}", table.getTableNumber());

        // Validate table number is unique
        if (tableRepository.existsByTableNumber(table.getTableNumber())) {
            String error = "El número de mesa " + table.getTableNumber() + " ya existe";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        // Validate capacity
        if (table.getCapacity() == null || table.getCapacity() < 1) {
            String error = "La capacidad debe ser al menos 1 persona";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        // Set audit fields
        table.setCreatedBy(username);
        table.setUpdatedBy(username);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());

        // Set default status if not provided
        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }

        RestaurantTable saved = tableRepository.save(table);
        log.info("Restaurant table created successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public RestaurantTable update(Long id, RestaurantTable table, String username) {
        log.info("Updating restaurant table with ID: {}", id);

        RestaurantTable existing = tableRepository.findById(id)
                .orElseThrow(() -> {
                    String error = "Mesa no encontrada con ID: " + id;
                    log.error(error);
                    return new IllegalArgumentException(error);
                });

        // Validate table number is unique (excluding current table)
        if (!existing.getTableNumber().equals(table.getTableNumber()) &&
            tableRepository.existsByTableNumber(table.getTableNumber())) {
            String error = "El número de mesa " + table.getTableNumber() + " ya existe";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        // Validate capacity
        if (table.getCapacity() == null || table.getCapacity() < 1) {
            String error = "La capacidad debe ser al menos 1 persona";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        // Update fields
        existing.setTableNumber(table.getTableNumber());
        existing.setCapacity(table.getCapacity());
        existing.setLocation(table.getLocation());
        existing.setStatus(table.getStatus());
        existing.setComments(table.getComments());
        existing.setUpdatedBy(username);
        existing.setUpdatedAt(LocalDateTime.now());

        RestaurantTable updated = tableRepository.save(existing);
        log.info("Restaurant table updated successfully: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public RestaurantTable changeStatus(Long id, TableStatus status, String username) {
        log.info("Changing status of table ID {} to {}", id, status);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> {
                    String error = "Mesa no encontrada con ID: " + id;
                    log.error(error);
                    return new IllegalArgumentException(error);
                });

        table.setStatus(status);
        table.setUpdatedBy(username);
        table.setUpdatedAt(LocalDateTime.now());

        RestaurantTable updated = tableRepository.save(table);
        log.info("Table status changed successfully: {} -> {}", id, status);
        return updated;
    }

    @Override
    public List<RestaurantTable> findByStatus(TableStatus status) {
        log.debug("Finding tables by status: {}", status);
        return tableRepository.findByStatus(status);
    }

    @Override
    public List<RestaurantTable> findAvailableTables() {
        log.debug("Finding available tables");
        return tableRepository.findAvailableTablesOrderByCapacity();
    }

    @Override
    public List<RestaurantTable> findByLocation(String location) {
        log.debug("Finding tables by location: {}", location);
        return tableRepository.findByLocation(location);
    }

    @Override
    public List<RestaurantTable> findByMinimumCapacity(Integer capacity) {
        log.debug("Finding tables with minimum capacity: {}", capacity);
        return tableRepository.findByCapacityGreaterThanEqual(capacity);
    }

    @Override
    public long countByStatus(TableStatus status) {
        log.debug("Counting tables by status: {}", status);
        return tableRepository.countByStatus(status);
    }

    @Override
    public long countAll() {
        log.debug("Counting all tables");
        return tableRepository.count();
    }

    @Override
    public boolean existsByTableNumber(Integer tableNumber) {
        return tableRepository.existsByTableNumber(tableNumber);
    }

    @Override
    public boolean existsByTableNumberAndIdNot(Integer tableNumber, Long excludeId) {
        return tableRepository.existsByTableNumberAndIdNot(tableNumber, excludeId);
    }

    @Override
    public List<String> getDistinctLocations() {
        log.debug("Fetching distinct locations");
        return tableRepository.findDistinctLocations();
    }
}
