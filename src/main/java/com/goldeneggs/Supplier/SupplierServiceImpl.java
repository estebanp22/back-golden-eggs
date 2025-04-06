package com.goldeneggs.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of SupplierService interface.
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier get(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    @Override
    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier update(Long id, Supplier updated) {
        Supplier existing = get(id);
        if (existing == null) return null;

        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());

        return supplierRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }
}
