package com.aatechsolutions.elgransazon.presentation.controller;

import com.aatechsolutions.elgransazon.application.service.ReservationService;
import com.aatechsolutions.elgransazon.application.service.RestaurantTableService;
import com.aatechsolutions.elgransazon.domain.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller for Reservation management
 */
@Controller
@RequestMapping("/admin/reservations")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantTableService tableService;

    /**
     * Show list of all reservations
     */
    @GetMapping
    public String listReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) ReservationStatus status,
            Model model) {
        log.debug("Displaying reservations list - date: {}, status: {}", date, status);

        List<Reservation> reservations;

        if (date != null) {
            reservations = reservationService.findByDate(date);
        } else if (status != null) {
            reservations = reservationService.findByStatus(status);
        } else {
            reservations = reservationService.findAllOrderByDateTimeDesc();
        }

        // Statistics
        long totalCount = reservations.size();
        long todayCount = reservationService.countTodayReservations();
        long todayActiveCount = reservationService.countTodayActiveReservations();
        long reservedCount = reservationService.countByStatus(ReservationStatus.RESERVED);
        long occupiedCount = reservationService.countByStatus(ReservationStatus.OCCUPIED);
        long completedCount = reservationService.countByStatus(ReservationStatus.COMPLETED);
        long cancelledCount = reservationService.countByStatus(ReservationStatus.CANCELLED);
        long noShowCount = reservationService.countByStatus(ReservationStatus.NO_SHOW);

        model.addAttribute("reservations", reservations);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("todayActiveCount", todayActiveCount);
        model.addAttribute("reservedCount", reservedCount);
        model.addAttribute("occupiedCount", occupiedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("noShowCount", noShowCount);
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStatus", status);

        return "admin/reservations/list";
    }

    /**
     * Show form to create a new reservation
     */
    @GetMapping("/new")
    public String newReservationForm(Model model) {
        log.debug("Displaying new reservation form");

        Reservation reservation = new Reservation();
        reservation.setReservationDate(LocalDate.now());

        List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();

        model.addAttribute("reservation", reservation);
        model.addAttribute("tables", tables);
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("isEdit", false);

        return "admin/reservations/form";
    }

    /**
     * Show form to edit an existing reservation
     */
    @GetMapping("/{id}/edit")
    public String editReservationForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Displaying edit form for reservation: {}", id);

        try {
            Reservation reservation = reservationService.findByIdOrThrow(id);

            if (!reservation.isEditable()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se puede editar una reservación en estado: " + reservation.getStatusDisplayName());
                return "redirect:/admin/reservations";
            }

            List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();

            model.addAttribute("reservation", reservation);
            model.addAttribute("tables", tables);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("isEdit", true);

            return "admin/reservations/form";
        } catch (IllegalArgumentException e) {
            log.error("Reservation not found: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/reservations";
        }
    }

    /**
     * Create a new reservation
     */
    @PostMapping
    public String createReservation(
            @Valid @ModelAttribute("reservation") Reservation reservation,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Creating new reservation for customer: {}", reservation.getCustomerName());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors creating reservation: {}", bindingResult.getAllErrors());
            List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();
            model.addAttribute("tables", tables);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("isEdit", false);
            return "admin/reservations/form";
        }

        try {
            String username = authentication.getName();
            Reservation created = reservationService.create(reservation, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Reservación creada exitosamente para " + created.getCustomerName());
            return "redirect:/admin/reservations";
        } catch (Exception e) {
            log.error("Error creating reservation", e);
            model.addAttribute("errorMessage", e.getMessage());
            List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();
            model.addAttribute("tables", tables);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("isEdit", false);
            return "admin/reservations/form";
        }
    }

    /**
     * Update an existing reservation
     */
    @PostMapping("/{id}")
    public String updateReservation(
            @PathVariable Long id,
            @Valid @ModelAttribute("reservation") Reservation reservation,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Updating reservation: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors updating reservation: {}", bindingResult.getAllErrors());
            List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();
            model.addAttribute("reservation", reservation);
            model.addAttribute("tables", tables);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("isEdit", true);
            return "admin/reservations/form";
        }

        try {
            String username = authentication.getName();
            reservationService.update(id, reservation, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Reservación actualizada exitosamente");
            return "redirect:/admin/reservations";
        } catch (Exception e) {
            log.error("Error updating reservation: {}", id, e);
            model.addAttribute("errorMessage", e.getMessage());
            List<RestaurantTable> tables = tableService.findAllOrderByTableNumber();
            model.addAttribute("reservation", reservation);
            model.addAttribute("tables", tables);
            model.addAttribute("statuses", ReservationStatus.values());
            model.addAttribute("isEdit", true);
            return "admin/reservations/form";
        }
    }

    /**
     * Check-in reservation (AJAX)
     */
    @PostMapping("/{id}/checkin")
    @ResponseBody
    public Map<String, Object> checkInReservation(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = authentication.getName();
            Reservation checkedIn = reservationService.checkIn(id, username);
            response.put("success", true);
            response.put("message", "Cliente registrado exitosamente");
            response.put("status", checkedIn.getStatusDisplayName());
            log.info("Reservation {} checked-in by user: {}", id, username);
        } catch (Exception e) {
            log.error("Error checking-in reservation: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Check-out reservation (AJAX)
     */
    @PostMapping("/{id}/checkout")
    @ResponseBody
    public Map<String, Object> checkOutReservation(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = authentication.getName();
            Reservation checkedOut = reservationService.checkOut(id, username);
            response.put("success", true);
            response.put("message", "Reservación completada exitosamente");
            response.put("status", checkedOut.getStatusDisplayName());
            log.info("Reservation {} checked-out by user: {}", id, username);
        } catch (Exception e) {
            log.error("Error checking-out reservation: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Cancel reservation (AJAX)
     */
    @PostMapping("/{id}/cancel")
    @ResponseBody
    public Map<String, Object> cancelReservation(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = authentication.getName();
            Reservation cancelled = reservationService.cancel(id, username);
            response.put("success", true);
            response.put("message", "Reservación cancelada exitosamente");
            response.put("status", cancelled.getStatusDisplayName());
            log.info("Reservation {} cancelled by user: {}", id, username);
        } catch (Exception e) {
            log.error("Error cancelling reservation: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Mark reservation as no-show (AJAX)
     */
    @PostMapping("/{id}/no-show")
    @ResponseBody
    public Map<String, Object> markAsNoShow(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = authentication.getName();
            Reservation noShow = reservationService.markAsNoShow(id, username);
            response.put("success", true);
            response.put("message", "Reservación marcada como 'No se presentó'");
            response.put("status", noShow.getStatusDisplayName());
            log.info("Reservation {} marked as no-show by user: {}", id, username);
        } catch (Exception e) {
            log.error("Error marking reservation as no-show: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Get reservation details for modal display (AJAX)
     */
    @GetMapping("/{id}/details")
    @ResponseBody
    public Map<String, Object> getReservationDetails(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Reservation reservation = reservationService.findByIdOrThrow(id);

            response.put("success", true);
            response.put("id", reservation.getId());
            response.put("customerName", reservation.getCustomerName());
            response.put("customerPhone", reservation.getCustomerPhone());
            response.put("customerEmail", reservation.getCustomerEmail() != null ? reservation.getCustomerEmail() : "N/A");
            response.put("numberOfGuests", reservation.getGuestsDisplay());
            response.put("reservationDate", reservation.getFormattedReservationDate());
            response.put("reservationTime", reservation.getFormattedReservationTime());
            response.put("reservationDateTime", reservation.getFormattedReservationDateTime());
            response.put("table", reservation.getTableDisplayName());
            response.put("status", reservation.getStatusDisplayName());
            response.put("specialRequests", reservation.getSpecialRequests() != null ? reservation.getSpecialRequests() : "Ninguna");
            response.put("isOccupied", reservation.getIsOccupied());
            response.put("createdBy", reservation.getCreatedBy());
            response.put("createdAt", reservation.getFormattedCreatedAt());
            response.put("updatedBy", reservation.getUpdatedBy() != null ? reservation.getUpdatedBy() : "N/A");
            response.put("updatedAt", reservation.getUpdatedAt() != null ? reservation.getFormattedUpdatedAt() : "N/A");

            log.debug("Retrieved details for reservation: {}", id);
        } catch (Exception e) {
            log.error("Error getting reservation details: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }
}
