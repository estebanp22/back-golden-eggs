package com.goldeneggs.Order;

import com.goldeneggs.User.User;
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
     * Deletes all orders associated with the specified user from the repository.
     *
     * @param user The user whose associated orders are to be deleted.
     */
    void deleteAllByUser(User user);

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
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.state != 'INVENTORY'")
    Long countOrdersInCurrentMonth(@Param("start") Date start, @Param("end") Date end);

    /**
     * Counts the total number of orders placed by a specific customer.
     *
     * @param customerId The ID of the customer whose orders are to be counted.
     * @return The total number of orders placed by the specified customer.
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :customerId")
    Long countOrdersByCustomerId(@Param("customerId") Long customerId);

    /**
     * List of all orders by customer
     * @param id The ID of the customer whose order
     * @return The list of orders placed by the specified customer
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :id")
    List<Order> getOrdersByUserId(Long id);


}
