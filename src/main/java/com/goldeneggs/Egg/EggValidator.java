package com.goldeneggs.Egg;

import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;


import java.util.Date;

public class EggValidator {

    public static boolean validateTypeEgg(TypeEgg typeEgg) {return typeEgg != null && typeEgg.getId() != null;}

    public static boolean validateColor(String color){return color != null;}

    public static boolean validateBuyPrice(Double buyPrice){return buyPrice != null && buyPrice > 0;}

    public static boolean validateSalePrice(Double buyPrice, Double salePrcie){return salePrcie != null && buyPrice <= salePrcie;}

    public static boolean validateExpirationDate(Date expirationDate){return expirationDate != null  && expirationDate.getTime() > System.currentTimeMillis();}

    public static boolean validateSupplier(Supplier supplier) {return supplier != null && supplier.getId() != null;}

    public static boolean validateAviableQuantity(int aviableQuantity){return aviableQuantity > 0 && aviableQuantity % 30 == 0;}
}
