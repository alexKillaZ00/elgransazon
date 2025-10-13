package com.aatechsolutions.elgransazon.presentation.controller;

import com.aatechsolutions.elgransazon.application.service.SupplierService;
import com.aatechsolutions.elgransazon.domain.entity.Supplier;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for managing supplier operations
 * Only accessible by ADMIN role
 */
@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Display list of all suppliers
     */
    @GetMapping
    public String listSuppliers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            Model model) {
        
        log.debug("Displaying suppliers list with search: {}, rating: {}", search, rating);
        
        List<Supplier> suppliers;
        
        if (rating != null && rating >= 1 && rating <= 5) {
            suppliers = supplierService.getSuppliersByRating(rating);
        } else if (search != null && !search.trim().isEmpty()) {
            suppliers = supplierService.searchSuppliers(search);
        } else {
            suppliers = supplierService.getAllSuppliers();
        }
        
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("activeCount", supplierService.countActiveSuppliers());
        model.addAttribute("inactiveCount", supplierService.countInactiveSuppliers());
        model.addAttribute("search", search);
        model.addAttribute("rating", rating);
        
        return "admin/suppliers/list";
    }

    /**
     * Show form to create a new supplier
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.debug("Displaying create supplier form");
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        return "admin/suppliers/form";
    }

    /**
     * Process the creation of a new supplier
     */
    @PostMapping
    public String createSupplier(
            @Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Processing supplier creation: {}", supplier.getName());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors on supplier creation");
            model.addAttribute("isEdit", false);
            return "admin/suppliers/form";
        }

        try {
            // Check if supplier name already exists
            if (supplierService.supplierNameExists(supplier.getName())) {
                bindingResult.rejectValue("name", "error.supplier", "Supplier name already exists");
                model.addAttribute("isEdit", false);
                return "admin/suppliers/form";
            }

            supplierService.createSupplier(supplier);
            log.info("Supplier created successfully: {}", supplier.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Supplier created successfully!");
            return "redirect:/admin/suppliers";

        } catch (Exception e) {
            log.error("Error creating supplier: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error creating supplier: " + e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/suppliers/form";
        }
    }

    /**
     * Show form to edit an existing supplier
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Displaying edit form for supplier id: {}", id);

        return supplierService.getSupplierById(id)
                .map(supplier -> {
                    model.addAttribute("supplier", supplier);
                    model.addAttribute("isEdit", true);
                    return "admin/suppliers/form";
                })
                .orElseGet(() -> {
                    log.warn("Supplier not found with id: {}", id);
                    redirectAttributes.addFlashAttribute("errorMessage", "Supplier not found");
                    return "redirect:/admin/suppliers";
                });
    }

    /**
     * Process the update of an existing supplier
     */
    @PostMapping("/{id}")
    public String updateSupplier(
            @PathVariable Long id,
            @Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Processing supplier update for id: {}", id);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors on supplier update");
            model.addAttribute("isEdit", true);
            return "admin/suppliers/form";
        }

        try {
            supplierService.updateSupplier(id, supplier);
            log.info("Supplier updated successfully: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully!");
            return "redirect:/admin/suppliers";

        } catch (IllegalArgumentException e) {
            log.error("Error updating supplier: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            return "admin/suppliers/form";
        }
    }

    /**
     * Soft delete a supplier (set active to false)
     */
    @PostMapping("/{id}/delete")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Processing supplier deletion for id: {}", id);

        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier deactivated successfully!");
        } catch (IllegalArgumentException e) {
            log.error("Error deleting supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    /**
     * Activate a supplier
     */
    @PostMapping("/{id}/activate")
    public String activateSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Processing supplier activation for id: {}", id);

        try {
            supplierService.activateSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier activated successfully!");
        } catch (IllegalArgumentException e) {
            log.error("Error activating supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    /**
     * Permanently delete a supplier (hard delete)
     */
    @PostMapping("/{id}/permanent-delete")
    public String permanentlyDeleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.warn("Processing permanent deletion for supplier id: {}", id);

        try {
            supplierService.permanentlyDeleteSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier permanently deleted!");
        } catch (IllegalArgumentException e) {
            log.error("Error permanently deleting supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }
}
