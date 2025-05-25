package com.goldeneggs.OrderEgg;

import com.goldeneggs.Egg.Egg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing order egg entities.
 */
@Repository
public interface OrderEggRepository extends JpaRepository<OrderEgg, Long> {


    @Query("SELECT COUNT(oe) > 0 FROM OrderEgg oe " +
            "WHERE oe.type = :type AND oe.color = :color " +
            "AND oe.order.state <> :excludedState")
    boolean existsByTypeAndColorInActiveOrders(String type, String color, String excludedState);

}
