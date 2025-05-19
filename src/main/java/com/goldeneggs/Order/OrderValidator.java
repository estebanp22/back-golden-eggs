package com.goldeneggs.Order;

import com.goldeneggs.User.User;

import java.util.Date;
import java.util.List;

public class OrderValidator {

    public static boolean validateUser(User user) {return user != null;}

    public static boolean validateOrderEggs(List<?> orderEggs) {return orderEggs != null && !orderEggs.isEmpty();}

    public static boolean validateTotalPrice(double totalPrice) { return totalPrice > 0; }

    public static boolean validateOrderDate(Date orderDate) { return orderDate != null && orderDate.getTime() >= System.currentTimeMillis(); }

    public static boolean validateState(String state) { return state != null; }
}
