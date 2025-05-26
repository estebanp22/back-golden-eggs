package com.goldeneggs.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user role.
 * Each role defines a set of permissions or access level for users.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "roles")
@NoArgsConstructor
public class Role {

    /**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Name of the role. Must be unique and not null.
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;
}
