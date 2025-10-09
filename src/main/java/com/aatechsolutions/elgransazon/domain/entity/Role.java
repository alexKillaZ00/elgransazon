package com.aatechsolutions.elgransazon.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity representing different roles in the POS system
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"idRol"})
@ToString(exclude = {"employees"})
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    /**
     * Role name constants for easy reference
     */
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String WAITER = "ROLE_WAITER";
    public static final String CHEF = "ROLE_CHEF";

    /**
     * Constructor for creating roles with just the name
     */
    public Role(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
