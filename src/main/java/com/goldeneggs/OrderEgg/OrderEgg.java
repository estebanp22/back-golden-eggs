package com.goldeneggs.OrderEgg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_egg")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEgg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_egg")
    private Egg egg;

    @Column(nullable = false, name="quantity")
    private int quantity;

    @Column(nullable = false, name = "unit_price")
    private double unitPrice;

    @Column(nullable = false)
    private double subtotal;
}
