package com.goldeneggs.Role;

/**
 * Utility class for validating Role entities.
 */
public class RoleValidator {

    /**
     * Validates that a role name is not null, not blank and has a reasonable length.
     *
     * @param name the role name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 50;
    }
}
