package com.goldeneggs.Order;

import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.User.User;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Date;
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
     * Possible states of an order.
     */
    public static final String STATE_CANCELED = "CANCELADA";
    public static final String STATE_PENDING = "PENDIENTE";
    public static final String STATE_COMPLETED = "COMPLETADA";

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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEgg> orderEggs;

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
