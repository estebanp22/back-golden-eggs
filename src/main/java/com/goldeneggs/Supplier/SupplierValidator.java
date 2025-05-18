package com.goldeneggs.Supplier;

/**
 * Utility class for validating Supplier data before persistence.
 */
public class SupplierValidator {

    /**
     * Validates the supplier's name.
     *
     * @param name the supplier name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    /**
     * Validates the supplier's address.
     *
     * @param address the address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty() && address.length() <= 255;
    }

    /**
     * Validates the supplier object.
     *
     * @param supplier the supplier to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(Supplier supplier) {
        return supplier != null &&
                isValidName(supplier.getName()) &&
                isValidAddress(supplier.getAddress());
    }
}

