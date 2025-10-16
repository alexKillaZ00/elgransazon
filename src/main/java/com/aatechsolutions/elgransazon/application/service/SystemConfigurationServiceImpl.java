package com.aatechsolutions.elgransazon.application.service;

import com.aatechsolutions.elgransazon.domain.entity.DayOfWeek;
import com.aatechsolutions.elgransazon.domain.entity.PaymentMethodType;
import com.aatechsolutions.elgransazon.domain.entity.SystemConfiguration;
import com.aatechsolutions.elgransazon.domain.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementation of SystemConfigurationService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final SystemConfigurationRepository configurationRepository;

    @Override
    public SystemConfiguration getConfiguration() {
        log.debug("Fetching system configuration");
        Optional<SystemConfiguration> config = configurationRepository.findFirstConfiguration();
        if (config.isPresent()) {
            return config.get();
        }
        // Create default configuration in a new transaction if none exists
        return createDefaultConfiguration();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfiguration> getConfigurationById(Long id) {
        log.debug("Fetching configuration by ID: {}", id);
        return configurationRepository.findById(id);
    }

    @Override
    public SystemConfiguration updateConfiguration(SystemConfiguration configuration) {
        log.info("Updating system configuration");
        
        SystemConfiguration existingConfig = configurationRepository.findFirstConfiguration()
                .orElseThrow(() -> new IllegalStateException("System configuration not found"));
        
        // Update fields
        existingConfig.setRestaurantName(configuration.getRestaurantName());
        existingConfig.setSlogan(configuration.getSlogan());
        existingConfig.setLogoUrl(configuration.getLogoUrl());
        existingConfig.setAddress(configuration.getAddress());
        existingConfig.setPhone(configuration.getPhone());
        existingConfig.setEmail(configuration.getEmail());
        existingConfig.setTaxRate(configuration.getTaxRate());
        
        if (configuration.getWorkDays() != null) {
            existingConfig.setWorkDays(configuration.getWorkDays());
        }
        
        if (configuration.getPaymentMethods() != null) {
            existingConfig.setPaymentMethods(configuration.getPaymentMethods());
        }
        
        SystemConfiguration saved = configurationRepository.save(existingConfig);
        log.info("System configuration updated successfully");
        return saved;
    }

    @Override
    public SystemConfiguration createInitialConfiguration(SystemConfiguration configuration) {
        log.info("Creating initial system configuration");
        
        if (configurationExists()) {
            throw new IllegalStateException("System configuration already exists. Use update instead.");
        }
        
        // Initialize payment methods if not set
        if (configuration.getPaymentMethods() == null || configuration.getPaymentMethods().isEmpty()) {
            Map<PaymentMethodType, Boolean> paymentMethods = new HashMap<>();
            paymentMethods.put(PaymentMethodType.CASH, true);
            paymentMethods.put(PaymentMethodType.CREDIT_CARD, true);
            paymentMethods.put(PaymentMethodType.DEBIT_CARD, true);
            configuration.setPaymentMethods(paymentMethods);
        }
        
        SystemConfiguration saved = configurationRepository.save(configuration);
        log.info("Initial system configuration created successfully");
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean configurationExists() {
        return configurationRepository.existsConfiguration();
    }

    @Override
    public SystemConfiguration updateWorkDays(Set<DayOfWeek> workDays) {
        log.info("Updating work days");
        
        if (workDays == null || workDays.isEmpty()) {
            throw new IllegalArgumentException("Work days cannot be empty");
        }
        
        SystemConfiguration config = configurationRepository.findFirstConfiguration()
                .orElseThrow(() -> new IllegalStateException("System configuration not found"));
        config.setWorkDays(workDays);
        
        SystemConfiguration saved = configurationRepository.save(config);
        log.info("Work days updated successfully");
        return saved;
    }

    @Override
    public SystemConfiguration updatePaymentMethods(Map<PaymentMethodType, Boolean> paymentMethods) {
        log.info("Updating payment methods");
        
        if (paymentMethods == null || paymentMethods.isEmpty()) {
            throw new IllegalArgumentException("Payment methods cannot be empty");
        }
        
        SystemConfiguration config = configurationRepository.findFirstConfiguration()
                .orElseThrow(() -> new IllegalStateException("System configuration not found"));
        config.setPaymentMethods(paymentMethods);
        
        SystemConfiguration saved = configurationRepository.save(config);
        log.info("Payment methods updated successfully");
        return saved;
    }

    @Override
    public SystemConfiguration updateTaxRate(BigDecimal taxRate) {
        log.info("Updating tax rate to: {}", taxRate);
        
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 100");
        }
        
        SystemConfiguration config = configurationRepository.findFirstConfiguration()
                .orElseThrow(() -> new IllegalStateException("System configuration not found"));
        config.setTaxRate(taxRate);
        
        SystemConfiguration saved = configurationRepository.save(config);
        log.info("Tax rate updated successfully");
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWorkDay(DayOfWeek day) {
        return configurationRepository.findFirstConfiguration()
                .map(config -> config.isWorkDay(day))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentMethodEnabled(PaymentMethodType type) {
        return configurationRepository.findFirstConfiguration()
                .map(config -> config.isPaymentMethodEnabled(type))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public String getRestaurantName() {
        return configurationRepository.findFirstConfiguration()
                .map(SystemConfiguration::getRestaurantName)
                .orElse("Restaurante");
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTaxRate() {
        return configurationRepository.findFirstConfiguration()
                .map(SystemConfiguration::getTaxRate)
                .orElse(new BigDecimal("16.00"));
    }

    /**
     * Create a default configuration if none exists
     */
    private SystemConfiguration createDefaultConfiguration() {
        log.info("Creating default system configuration");
        
        Set<DayOfWeek> defaultWorkDays = new HashSet<>(Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        ));
        
        Map<PaymentMethodType, Boolean> defaultPaymentMethods = new HashMap<>();
        defaultPaymentMethods.put(PaymentMethodType.CASH, true);
        defaultPaymentMethods.put(PaymentMethodType.CREDIT_CARD, true);
        defaultPaymentMethods.put(PaymentMethodType.DEBIT_CARD, true);
        
        SystemConfiguration defaultConfig = SystemConfiguration.builder()
                .restaurantName("Mi Restaurante")
                .slogan("El mejor sabor de la ciudad")
                .address("Dirección no configurada")
                .phone("0000-0000")
                .email("contacto@restaurant.com")
                .taxRate(new BigDecimal("16.00"))
                .workDays(defaultWorkDays)
                .paymentMethods(defaultPaymentMethods)
                .build();
        
        return configurationRepository.save(defaultConfig);
    }
}
