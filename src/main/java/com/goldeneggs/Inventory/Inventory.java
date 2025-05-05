package com.goldeneggs.Inventory;

import com.goldeneggs.Egg.Egg;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents a stock entry in the inventory system.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name or reference for this inventory entry (e.g., "Entrada 15/05/2025").
     */
    @Column(name = "name_product", nullable = false)
    private String nameProduct;

    /**
     * Date the products were added to the inventory.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "entry_date", nullable = false)
    private Date entryDate;

    /**
     * List of eggs associated with this inventory entry.
     */
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Egg> eggs;

    /**
     * Returns the total quantity of eggs in this inventory entry.
     */
    public int getTotalQuantity() {
        return eggs != null
                ? eggs.stream().mapToInt(Egg::getQuantity).sum()
                : 0;
    }
}
