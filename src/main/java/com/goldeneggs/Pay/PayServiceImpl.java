package com.goldeneggs.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the PayService interface.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayRepository payRepository;

    @Override
    public List<Pay> getAll() {
        return payRepository.findAll();
    }

    @Override
    public Pay get(Long id) {
        return payRepository.findById(id).orElse(null);
    }

    @Override
    public Pay save(Pay pay) {
        return payRepository.save(pay);
    }

    @Override
    public Pay update(Long id, Pay updatedPay) {
        Pay existing = get(id);
        if (existing == null) return null;

        existing.setCustomer(updatedPay.getCustomer());
        existing.setBill(updatedPay.getBill());
        existing.setAmountPaid(updatedPay.getAmountPaid());
        existing.setPaymentMethod(updatedPay.getPaymentMethod());

        return payRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        payRepository.deleteById(id);
    }
}
