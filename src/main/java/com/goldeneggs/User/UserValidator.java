package com.goldeneggs.User;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Exception.InvalidUserDataException;
import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

/**
 * Utility class for validating User entities and related DTOs
 */
public class UserValidator {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{7,10}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$");

    /**
     * Validates a RegisterDto object
     * @param dto the RegisterDto to validate
     * @throws InvalidUserDataException if validation fails
     */
    public static void validateRegisterDto(RegisterDto dto) {
        if (dto == null) {
            throw new InvalidUserDataException("User data cannot be null");
        }

        validateId(dto.getId());
        validateName(dto.getName());
        validatePhoneNumber(dto.getPhoneNumber());
        validateEmail(dto.getEmail());
        validateUsername(dto.getUsername());
        validatePassword(dto.getPassword());
        validateAddress(dto.getAddress());
    }

    /**
     * Validates an UpdateUserDto object
     * @param dto the UpdateUserDto to validate
     * @throws InvalidUserDataException if validation fails
     */
    public static void validateUpdateUserDto(UpdateUserDto dto) {
        if (dto == null) {
            throw new InvalidUserDataException("User data cannot be null");
        }

        if (dto.getName() != null) validateName(dto.getName());
        if (dto.getPhoneNumber() != null) validatePhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) validateEmail(dto.getEmail());
        if (dto.getUsername() != null) validateUsername(dto.getUsername());
        if (dto.getAddress() != null) validateAddress(dto.getAddress());
    }

    /**
     * Validates the given ID to ensure it is a positive number.
     * Throws an InvalidUserDataException if the ID is null or not a positive value.
     *
     * @param id the ID to validate
     * @throws InvalidUserDataException if the ID is null or less than or equal to zero
     */
    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserDataException("ID must be a positive number");
        }
    }

    /**
     * Validates the given name to ensure it meets the required conditions.
     * The name must not be null, empty, or exceed 100 characters in length.
     * If the validation fails, an InvalidUserDataException is thrown.
     *
     * @param name the name to validate
     * @throws InvalidUserDataException if the name is null, empty, or exceeds 100 characters
     */
    public static void validateName(String name) {
        if (!StringUtils.hasText(name) || name.length() > 100) {
            throw new InvalidUserDataException("Name must be between 1 and 100 characters");
        }
    }

    /**
     * Validates the given phone number to ensure it meets the required conditions.
     * The phone number must not be null, empty, and must match a valid format defined by the
     * PHONE_PATTERN (e.g., 7-10 digits). If the validation fails, an
     * InvalidUserDataException is thrown.
     *
     * @param phoneNumber the phone number to validate
     * @throws InvalidUserDataException if the phone number is null, empty, or invalid in format
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber) || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidUserDataException("Phone number must be valid (7-10 digits)");
        }
    }

    /**
     * Validates the provided email address to ensure it meets specific requirements.
     * The email must be non-null, non-empty, valid in format, and no longer than 255 characters.
     * If the email is invalid, an InvalidUserDataException is thrown.
     *
     * @param email the email address to validate
     * @throws InvalidUserDataException if the email is null, empty, invalid in format, or exceeds 255 characters
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email).matches() || email.length() > 255) {
            throw new InvalidUserDataException("Email must be valid and less than 255 characters");
        }
    }

    /**
     * Validates the provided username to ensure it adheres to the defined constraints.
     * The username must be between 3 and 20 characters in length, and consist of
     * letters, numbers, dots (.), underscores (_), or hyphens (-).
     *
     * @param username the username to validate
     * @throws InvalidUserDataException if the username is null, empty, or does not match the required pattern
     */
    public static void validateUsername(String username) {
        if (!StringUtils.hasText(username) || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidUserDataException("Username must be 3-20 characters (letters, numbers, ., _, -)");
        }
    }

    /**
     * Validates the provided password to ensure it meets the required security standards.
     * The password must:
     * - Be at least 6 characters long.
     * - Contain at least 1 uppercase letter.
     * - Contain at least 1 lowercase letter.
     * - Contain at least 1 number.
     *
     * If the validation fails, an InvalidUserDataException is thrown.
     *
     * @param password the password to validate
     * @throws InvalidUserDataException if the password is null, empty, or does not meet the required criteria
     */
    public static void validatePassword(String password) {
        if (!StringUtils.hasText(password) || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidUserDataException("Password must be at least 6 characters with 1 uppercase, 1 lowercase and 1 number");
        }
    }

    /**
     * Validates the provided address to ensure it adheres to the specified constraints.
     * The address must not be null, empty, or exceed 255 characters in length.
     * If the address is invalid, an InvalidUserDataException is thrown.
     *
     * @param address the address to validate
     * @throws InvalidUserDataException if the address is null, empty, or exceeds 255 characters
     */
    public static void validateAddress(String address) {
        if (!StringUtils.hasText(address) || address.length() > 255) {
            throw new InvalidUserDataException("Address must be between 1 and 255 characters");
        }
    }
}
