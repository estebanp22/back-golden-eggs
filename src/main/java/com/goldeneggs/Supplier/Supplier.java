package com.goldeneggs.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldeneggs.Egg.Egg;
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
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Egg> eggs;
}
