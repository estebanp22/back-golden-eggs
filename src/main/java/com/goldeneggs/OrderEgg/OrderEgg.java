package com.goldeneggs.OrderEgg;

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
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_egg")
    private Egg egg;

    @Column(nullable = false)
    private int quiantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double subtotal;//quantity * unitPrice
}
