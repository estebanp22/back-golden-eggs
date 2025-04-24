package com.goldeneggs.Order;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.User.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a customer order.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer who placed the order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;

    /**
     * List of eggs included in the order.
     */
    @ManyToMany
    @JoinTable(
            name = "order_eggs",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "egg_id")
    )
    private List<Egg> eggs;

    /**
     * Total price of the order.
     */
    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    /**
     * Date when the order was placed.
     */
    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    /**
     * Current state of the order.
     */
    @Column(nullable = false)
    private String state;
}
