package com.aatechsolutions.elgransazon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Employee entity representing employees who can access the POS system
 */
@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"idEmpleado"})
@ToString(exclude = {"roles"})
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "id_empleado"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Returns the full name of the employee
     */
    public String getFullName() {
        return nombre + " " + apellido;
    }

    /**
     * Check if employee has a specific role
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getNombreRol().equals(roleName));
    }

    /**
     * Get the primary role (first role in the set)
     * Used for determining which page to redirect to
     */
    public String getPrimaryRole() {
        return roles.stream()
                .findFirst()
                .map(Role::getNombreRol)
                .orElse("ROLE_EMPLOYEE");
    }
}
