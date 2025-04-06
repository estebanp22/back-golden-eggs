package com.goldeneggs.Supplier;

import java.util.List;

/**
 * Service interface for supplier operations.
 */
public interface SupplierService {
    List<Supplier> getAll();
    Supplier get(Long id);
    Supplier save(Supplier supplier);
    Supplier update(Long id, Supplier updated);
    void delete(Long id);
}
