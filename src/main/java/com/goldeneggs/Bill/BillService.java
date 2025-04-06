package com.goldeneggs.Bill;

import java.util.List;

/**
 * Interface for Bill service operations.
 */
public interface BillService {

    List<Bill> getAll();
    Bill get(Long id);
    Bill save(Bill bill);
    Bill update(Long id, Bill updatedBill);
    void delete(Long id);
}
