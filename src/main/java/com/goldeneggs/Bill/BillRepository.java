package com.goldeneggs.Bill;

import com.goldeneggs.Order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository for managing Bill entities.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * Deletes a bill associated with the specified order.
     *
     * @param order the order whose associated bill is to be deleted.
     */
    void deleteByOrder(Order order);


    /**
     * Counts the number of bills for customers issued within a specific date range.
     *
     * @param start The start date (inclusive).
     * @param end   The end date (inclusive).
     * @return The total number of customer bills issued in the given range.
     */
    @Query("SELECT COUNT(b) FROM Bill b WHERE b.issueDate BETWEEN :start AND :end " +
            "AND EXISTS (SELECT r FROM b.order.user.roles r WHERE r.name = 'CUSTOMER')")
    Long countCustomerBillsInCurrentMonth(@Param("start") Date start, @Param("end") Date end);

    /**
     * Retrieves all bills associated with a specific customer.
     *
     * @param customerId the ID of the customer whose bills should be retrieved.
     * @return a list of {@link Bill} entities associated with the specified customer ID.
     */
    @Query("SELECT b FROM Bill b WHERE b.order.user.id = :customerId")
    List<Bill> findAllByCustomerId(@Param("customerId") Long customerId);


}
