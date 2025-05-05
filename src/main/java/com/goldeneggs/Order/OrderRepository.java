package com.goldeneggs.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves a list of orders placed within the current month.
     *
     * @param start The start date of the current month.
     * @param end The end date of the current month.
     * @return A list of orders that have been placed between the specified start and end dates.
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    List<Order> findOrdersInCurrentMonth(@Param("start") Date start, @Param("end") Date end);

    /**
     * Counts the number of orders placed within the specified date range for the current month.
     *
     * @param start The start date of the range (inclusive).
     * @param end The end date of the range (inclusive).
     * @return The total count of orders placed within the specified date range.
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    Long countOrdersInCurrentMonth(@Param("start") Date start, @Param("end") Date end);


}
