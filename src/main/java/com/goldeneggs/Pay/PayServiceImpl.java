package com.goldeneggs.Pay;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link PayService} interface.
 * <p>
 * Provides methods for retrieving, creating, updating, and deleting payments.
 * </p>
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayRepository payRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository  billRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pay> getAll() {
        return payRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pay get(Long id) {
        return payRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pay save(Pay pay) {
        validatePayOrThrow(pay);
        return payRepository.save(pay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pay update(Long id, Pay updatedPay) {
        Pay existing = payRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Pay with ID " + id + " not found"));

        validatePayOrThrow(updatedPay);
        existing.setUser(updatedPay.getUser());
        existing.setBill(updatedPay.getBill());
        existing.setAmountPaid(updatedPay.getAmountPaid());
        existing.setPaymentMethod(updatedPay.getPaymentMethod());

        return payRepository.save(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!payRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Payment not found with ID: " + id);
        }
        payRepository.deleteById(id);
    }

    /**
     * Calculates the total income from all payments in the system.
     * If no payments exist, returns 0.0.
     *
     * @return The total sum of all payments as a {@code Double}, or 0.0 if no payments exist.
     */
    @Override
    public Double totalIncome() {
        Double total = payRepository.sumAllByAmountPaid();
        return total != null ? total : 0.0;
    }

    /**
     * Calculates the total income from all payments made in the last month.
     * If no payments exist for the specified month, returns 0.0.
     *
     * @return The total sum of payments made in the last month as a {@code Double}, or 0.0 if no payments exist.
     */
    @Override
    public Double totalIncomeCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date startOfMonth = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date today = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Double total = payRepository.sumAmountPaidInCurrentMonth(startOfMonth, today);

        return total != null ? total : 0.0;
    }

    /**
     * Calculates the total income from all payments made in the last month.
     * If no payments exist for the specified month, returns 0.0.
     *
     * @return The total sum of payments made in the last month as a {@code Double}, or 0.0 if no payments exist.
     */

    @Override
    public Double totalExpensesCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date startOfMonth = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date today = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Double total = payRepository.sumAmountSaleInCurrentMonth(startOfMonth, today);
        total = total/30;

        return total != null ? total : 0.0;
    }

    /**
     * Validates the provided Pay object and throws an exception if any of its data is invalid.
     * This includes checks for user validity, user existence, bill validity, bill existence,
     * payment amount, and payment method.
     *
     * @param pay The Pay object to validate. It contains information about the user, bill,
     *            payment amount, and payment method that will be validated.
     * @throws InvalidPayDataException if the Pay object contains invalid data, such as:
     *                                 - Invalid or null user
     *                                 - User does not exist in the repository
     *                                 - Invalid or null bill
     *                                 - Bill does not exist in the repository
     *                                 - Invalid payment amount (e.g., zero or negative)
     *                                 - Invalid payment method
     */
    private void validatePayOrThrow(Pay pay) {
        if (!PayValidator.validateUser(pay.getUser())) {
            throw new InvalidPayDataException("Invalid user");
        }
        if(!userRepository.existsById(pay.getUser().getId())){
            throw new InvalidPayDataException("User does not exist");
        }
        if (!PayValidator.validateBill(pay.getBill())) {
            throw new InvalidPayDataException("Invalid bill");
        }
        if(!billRepository.existsById(pay.getBill().getId())){
            throw new InvalidPayDataException("User does not exist");
        }
        if (!PayValidator.validateAmountPaid(pay.getAmountPaid())) {
            throw new InvalidPayDataException("Invalid amount");
        }
        if (!PayValidator.validatePaymentMethod(pay.getPaymentMethod())) {
            throw new InvalidPayDataException("Invalid payment method");
        }
    }

    /**
     * Creates a payment record for the provided bill using the specified payment method.
     * The payment is initialized with the total price of the bill and saved in the system.
     *
     * @param bill The bill for which the payment is being created. Must not be null and should contain
     *             valid information including the total price.
     * @param paymentMethod The method of payment used for the bill (e.g., cash, credit card, etc.).
     *                       Must not be null or empty.
     */
    @Override
    public void createPayForBill(Bill bill, String paymentMethod) {
       Pay pay = new Pay();
       pay.setBill(bill);
       pay.setUser(bill.getOrder().getUser());
       pay.setAmountPaid(bill.getTotalPrice());
       pay.setPaymentMethod(paymentMethod);
       save(pay);
    }
}
