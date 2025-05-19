package com.goldeneggs.Bill;

import com.goldeneggs.Order.Order;

import java.sql.Date;

/**
 * Utility class for validating Bill data before persistence.
 */
public class BillValidator {

    /**
     * Validate the order of bill
     * @param order the orderr asociate a bill
     * @return true if order exist.
     */
    public static boolean validateOrder(Order order){return order != null;}

    /**
     * Validate the date of bill
     * @param issueDate date of the bill
     * @return true if date meets filters
     */
    public static boolean validateIssueDate(Date issueDate){return issueDate != null &&  (issueDate.getTime() <= System.currentTimeMillis());}

    /**
     * Validate the total price of the bill
     * @param totalPrice the price to validate
     * @return true if the total price is greater than zero
     */
    public static boolean validateTotalPrice(double totalPrice){return totalPrice >= 0;}
}
