package com.goldeneggs.TypeEgg;

/**
 * Utility class for validating TypeEgg entities.
 */
public class TypeEggValidator {

    /**
     * Validates the egg type string.
     *
     * @param type the type string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidType(String type) {
        return type != null && !type.trim().isEmpty() && type.length() <= 100;
    }

    /**
     * Validates the entire TypeEgg entity.
     *
     * @param typeEgg the entity to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(TypeEgg typeEgg) {
        return typeEgg != null && isValidType(typeEgg.getType());
    }
}
