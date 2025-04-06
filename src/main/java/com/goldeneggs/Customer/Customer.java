package com.goldeneggs.Customer;


import jakarta.persistence.*;
import lombok.*;

/**
 * This class represents a customer entity in the system.
 * It stores information such as name, phone number, login credentials, and address.
 */

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * Unique identifier for the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the customer.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Contact phone number of the customer.
     */
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * Username used for login.
     */
    @Column(name = "username", unique = true, nullable = false)
    private String user;

    /**
     * Password used for login.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Physical address of the customer.
     */
    @Column(name = "address", nullable = false)
    private String address;
}