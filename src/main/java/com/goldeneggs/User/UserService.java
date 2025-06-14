package com.goldeneggs.User;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Dto.UserDataDto;

import java.util.List;

/**
 * Service interface for managing user operations.
 * Includes functionalities for creating, retrieving, updating, deleting users, and managing user status.
 */
public interface UserService {

    /**
     * Saves a new user based on the provided registration data.
     *
     * @param registerDto The DTO containing user registration details.
     * @return The saved User object.
     */
    User save(RegisterDto registerDto);

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The User object if found, or null if not found.
     */
    User getUserById(Long id);

    /**
     * Retrieves a user's data based on their username.
     *
     * @param username the username of the user to retrieve.
     * @return a UserDataDto containing the user's details if found, or null if no such user exists.
     */
    UserDataDto getUserByUsername(String username);

    /**
     * Retrieves all users in the system.
     *
     * @return A list of all User objects.
     */
    List<User> getAllUsers();

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    void deleteUser(Long id);

    /**
     * Updates a user based on the provided data.
     *
     * @param id The ID of the user to update.
     * @param updateUserDto The DTO containing the updated user information.
     * @return The updated User object.
     */
    User updateUser(Long id, UpdateUserDto updateUserDto);

    /**
     * Disables a user account, setting their 'enabled' field to false.
     *
     * @param id The ID of the user to disable.
     * @return The updated User object after being disabled.
     */
    User disableUser(Long id);

    /**
     * Updates the password for a user.
     *
     * @param userId The ID of the user whose password is being updated.
     * @param newPassword The new password to set.
     * @return The User object after the password is updated.
     */
    User updatePassword(Long userId, String newPassword);

    /**
     * Activates a user account, setting their 'enabled' field to true.
     *
     * @param id The ID of the user to activate.
     * @return The updated User object after being activated.
     */
    User activateUser(Long id);

    /**
     * Retrieves a list of all customers in the system.
     *
     * @return A list of User objects representing all customers.
     */
    List<User> getAllCustomers();

    /**
     * Counts the total number of clients in the system.
     *
     * @return the total number of clients as a Long value.
     */
    Long countClients();

    /**
     * Counts the total number of employees in the system.
     *
     * @return the total number of employees as a Long value.
     */
    Long countEmployees();

    /**
     * Retrieves a list of all employee in the system.
     *
     * @return A list of User objects representing all employee.
     */
    List<User> getAllEmployee();

    /**
     * Retrives a list of all employee disable in the system
     *
     * @return A list of user employee are disabled
     */
    List<User> getAllDisabledEmployess();
}
