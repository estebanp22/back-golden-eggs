package com.goldeneggs.Pay;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return payRepository.save(pay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pay update(Long id, Pay updatedPay) {
        Pay existing = get(id); // will throw ResourceNotFoundException if not found

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
}
