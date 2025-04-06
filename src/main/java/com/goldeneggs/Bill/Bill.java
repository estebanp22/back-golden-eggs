package com.goldeneggs.Bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goldeneggs.Order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Represents a bill issued for an order.
 */
@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated order for this bill.
     */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("bill")
    private Order order;

    /**
     * Date when the bill was issued.
     */
    @Column(name = "issue_date", nullable = false)
    private Date issueDate;

    /**
     * Total price to be paid.
     */
    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    /**
     * Whether the bill has been paid.
     */
    @Column(name = "paid", nullable = false)
    private boolean paid;
}
