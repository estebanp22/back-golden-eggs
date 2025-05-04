package com.goldeneggs.TypeEgg;

import com.goldeneggs.Supplier.Supplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a type of egg.
 */
@Entity
@Table(name = "type_eggs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeEgg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of egg (e.g., organic, cage-free).
     */
    @Column(nullable = false)
    private String type;

    @ManyToMany(mappedBy = "typeEggs")
    private List<Supplier> suppliers;

}
