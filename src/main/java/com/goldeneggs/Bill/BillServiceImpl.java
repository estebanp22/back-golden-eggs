package com.goldeneggs.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the Bill service.
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Override
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @Override
    public Bill get(Long id) {
        return billRepository.findById(id).orElse(null);
    }

    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public Bill update(Long id, Bill updatedBill) {
        Bill existing = get(id);
        if (existing == null) return null;

        existing.setOrder(updatedBill.getOrder());
        existing.setIssueDate(updatedBill.getIssueDate());
        existing.setTotalPrice(updatedBill.getTotalPrice());
        existing.setPaid(updatedBill.isPaid());

        return billRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        billRepository.deleteById(id);
    }
}
