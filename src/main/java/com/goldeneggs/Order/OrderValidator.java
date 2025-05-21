package com.goldeneggs.Order;

import com.goldeneggs.User.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class OrderValidator {

    public static boolean validateUser(User user) {return user != null;}

    public static boolean validateOrderEggs(List<?> orderEggs) {return orderEggs != null && !orderEggs.isEmpty();}

    public static boolean validateTotalPrice(double totalPrice) { return totalPrice > 0; }

    public static boolean validateOrderDate(Date orderDate) {
        if (orderDate == null) {
            return false;
        }

        // Convertir java.sql.Date a LocalDate
        LocalDate today = LocalDate.now();
        LocalDate orderLocalDate = orderDate.toLocalDate();

        return orderLocalDate.equals(today);
    }
    public static boolean validateState(String state) { return state != null; }
}
