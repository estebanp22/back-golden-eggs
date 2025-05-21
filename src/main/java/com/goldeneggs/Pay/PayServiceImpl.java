package com.goldeneggs.Pay;

import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
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
}
