package com.goldeneggs.OrderEgg;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Order.Order;

public class OrderEggValidator {

    public static boolean validateQuantity(int quantity) {return quantity > 0;}

    public static boolean validateUnitPrice(double unitPrice) {return unitPrice >= 0;}

    public static boolean validateSubtotal(double subtotal, int quantity, double unitPrice) {return subtotal == quantity*unitPrice;}

    public static boolean validateOrder(Order order){return order != null;}

    public static boolean validateEgg(Egg egg){return egg != null;}
}
