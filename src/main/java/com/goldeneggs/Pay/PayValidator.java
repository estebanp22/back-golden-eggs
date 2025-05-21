package com.goldeneggs.Pay;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.User.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Component
public class PayValidator {

    private final List<String> VALID_PAYMENT_METHODS = List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA");

    public boolean validateUser(User user) {return user != null;}

    public boolean validateBill(Bill bill) {return bill != null;}

    public boolean validateAmountPaid(double amountPaid) {return amountPaid > 0;}

    public boolean validatePaymentMethod(String paymentMethod) {return paymentMethod != null && VALID_PAYMENT_METHODS.contains(paymentMethod);}

}
