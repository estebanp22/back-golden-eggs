package com.goldeneggs.Egg;

import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.Supplier.SupplierRepository;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.TypeEgg.TypeEggRepository;

import java.util.Date;

public class EggValidator {

    private final TypeEggRepository typeEggRepository;
    private final SupplierRepository supplierRepository;

    public EggValidator(TypeEggRepository typeEggRepository, SupplierRepository supplierRepository) {
        this.typeEggRepository = typeEggRepository;
        this.supplierRepository = supplierRepository;
    }

    public boolean validateTypeEgg(TypeEgg typeEgg) {
        return typeEgg != null && typeEgg.getId() != null && typeEggRepository.existsById(typeEgg.getId());
    }

    public static boolean validateColor(String color){return color != null;}

    public static boolean validateBuyPrice(Double buyPrice){return buyPrice != null && buyPrice > 0;}

    public static boolean validateSalePrice(Double buyPrice, Double salePrcie){return salePrcie != null && buyPrice <= salePrcie;}

    public static boolean validateExpirationDate(Date expirationDate){return expirationDate != null  && expirationDate.getTime() >= System.currentTimeMillis();}

    public boolean validateSupplier(Supplier supplier) {
        return supplier != null && supplier.getId() != null && supplierRepository.existsById(supplier.getId());
    }

    public static boolean validateAviableQuantity(int aviableQuantity){return aviableQuantity < 0;}
}
