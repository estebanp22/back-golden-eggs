package com.goldeneggs.Pay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Customer.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Represents a payment made by a customer for a bill.
 */
@Entity
@Table(name = "pays")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pay implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer who made the payment.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("pays")
    private Customer customer;

    /**
     * Bill associated with this payment.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnoreProperties("pays")
    private Bill bill;

    /**
     * Amount of money paid.
     */
    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;

    /**
     * Method used to pay (e.g., cash, card).
     */
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
}
