package com.goldeneggs.Pay;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.User.User;

import java.util.List;

public class PayValidator {

    private static final List<String> VALID_PAYMENT_METHODS = List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA");

    public static boolean validateUser(User user) {return user != null;}

    public static boolean validateBill(Bill bill) {return bill != null;}

    public static boolean validateAmountPaid(double amountPaid) {return amountPaid > 0;}

    public static boolean validatePaymentMethod(String paymentMethod) {return paymentMethod != null && VALID_PAYMENT_METHODS.contains(paymentMethod);}

}
