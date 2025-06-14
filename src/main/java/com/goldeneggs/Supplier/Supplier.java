package com.goldeneggs.Supplier;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.goldeneggs.TypeEgg.TypeEgg;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a supplier who provides eggs to the company.
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the supplier.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Supplier's address.
     */
    @Column(nullable = false)
    private String address;

    /**
     * List of eggs provided by the supplier.
     */
    @ManyToMany
    @JoinTable(
            name = "supplier_type_egg",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "type_egg_id")
    )
    @JsonIdentityReference(alwaysAsId = true)
    private List<TypeEgg> typeEggs;

}
