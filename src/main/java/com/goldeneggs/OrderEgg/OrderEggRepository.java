package com.goldeneggs.OrderEgg;

import com.goldeneggs.Egg.Egg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing order egg entities.
 */
@Repository
public interface OrderEggRepository extends JpaRepository<OrderEgg, Long> {

    /**
     * Return if egg id exist in any order
     * @param eggId id of the egg
     * @return true if exist , or false if not exist
     */
    boolean existsByEgg_Id(Long eggId);

}
