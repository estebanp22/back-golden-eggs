package com.goldeneggs.Pay;

import com.goldeneggs.Bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;

/**
 * Repository for managing Pay entities.
 */
@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {

    /**
     * Deletes all payment records associated with a specific bill.
     *
     * @param bill The bill associated with the payments to be deleted.
     */
    void deleteAllByBill(Bill bill);

    /**
     * Calculates the total sum of all payments made.
     *
     * @return A {@code Double} value representing the sum of the {@code amountPaid} field
     *         for all payment records in the database.
     */
    @Query("SELECT SUM(p.amountPaid) FROM Pay p")
    Double sumAllByAmountPaid();

    /**
     * Calculates the total sum of all payments made within the specified date range.
     *
     * @param start The starting date of the range (inclusive).
     * @param end The ending date of the range (inclusive).
     * @return A {@code Double} value representing the sum of the {@code amountPaid} field
     *         for all payments made within the given date range, or {@code null} if no payments exist in the range.
     */
    @Query("SELECT SUM(p.amountPaid) FROM Pay p " +
            "WHERE p.bill.issueDate BETWEEN :start AND :end " +
            "AND p.bill.order.state <> 'INVENTORY'")
    Double sumAmountPaidInCurrentMonth(@Param("start") Date start, @Param("end") Date end);

    /**
     * Calculates the total sum of all payments made within the specified date range.
     *
     * @param start The starting date of the range (inclusive).
     * @param end The ending date of the range (inclusive).
     * @return A {@code Double} value representing the sum of the {@code amountPaid} field
     *         for all payments made within the given date range, or {@code null} if no payments exist in the range.
     */
    @Query("SELECT SUM(p.amountPaid) FROM Pay p " +
            "WHERE p.bill.issueDate BETWEEN :start AND :end " +
            "AND p.bill.order.state = 'INVENTORY'")
    Double sumAmountSaleInCurrentMonth(@Param("start") Date start, @Param("end") Date end);
}

