package com.aatechsolutions.elgransazon.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Supplier entity representing ingredient suppliers
 * Manages information about companies or individuals that provide ingredients
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"idSupplier"})
@ToString
public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_supplier")
    private Long idSupplier;

    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 150, message = "Supplier name must be between 2 and 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$", 
             message = "Invalid phone number format")
    @Column(name = "phone", length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 300, message = "Address cannot exceed 300 characters")
    @Column(name = "address", length = 300)
    private String address;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars rating

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to set updatedAt before update operations
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to set createdAt before persist operations
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Get supplier display name with contact info
     */
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder(name);
        if (contactPerson != null && !contactPerson.isEmpty()) {
            info.append(" (").append(contactPerson).append(")");
        }
        return info.toString();
    }

    /**
     * Check if supplier has complete contact information
     */
    public boolean hasCompleteContactInfo() {
        return phone != null && !phone.isEmpty() && 
               email != null && !email.isEmpty();
    }
}
