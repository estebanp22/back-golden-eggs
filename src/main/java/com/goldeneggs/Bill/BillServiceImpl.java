package com.goldeneggs.Bill;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing {@link Bill} entities.
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    /**
     * Retrieves all bills from the repository.
     *
     * @return a list of all {@link Bill} entities.
     */
    @Override
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    /**
     * Retrieves a bill by its ID.
     *
     * @param id the ID of the bill to retrieve.
     * @return the {@link Bill} with the given ID.
     * @throws ResourceNotFoundException if no bill is found with the given ID.
     */
    @Override
    public Bill get(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + id + " not found"));
    }

    /**
     * Saves a new bill in the repository.
     *
     * @param bill the bill to save.
     * @return the saved {@link Bill}.
     */
    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Updates an existing bill.
     *
     * @param id the ID of the bill to update.
     * @param updatedBill the updated bill data.
     * @return the updated {@link Bill}.
     * @throws ResourceNotFoundException if the bill does not exist.
     */
    @Override
    public Bill update(Long id, Bill updatedBill) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + id + " not found"));

        existing.setOrder(updatedBill.getOrder());
        existing.setIssueDate(updatedBill.getIssueDate());
        existing.setTotalPrice(updatedBill.getTotalPrice());
        existing.setPaid(updatedBill.isPaid());

        return billRepository.save(existing);
    }

    /**
     * Deletes a bill by its ID.
     *
     * @param id the ID of the bill to delete.
     * @throws ResourceNotFoundException if the bill does not exist.
     */
    @Override
    public void delete(Long id) {
        if (!billRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bill with ID " + id + " not found");
        }
        billRepository.deleteById(id);
    }
}