package com.goldeneggs.User;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Dto.UserDataDto;
import com.goldeneggs.Exception.InvalidUserDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user using the provided registration details.
     * Handles exceptions for invalid data, user conflicts, and other unexpected issues.
     *
     * @param registerDto the data transfer object containing user registration details
     * @return a {@link ResponseEntity} containing the created user on success,
     *         a conflict message if the user already exists,
     *         a bad request message for invalid data,
     *         or an internal server error message for unexpected errors
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            User savedUser = userService.save(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException | InvalidUserDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     * Handles exceptions for when the user is not found or other unexpected errors occur.
     *
     * @param id the unique identifier of the user to be retrieved
     * @return a {@code ResponseEntity} containing the user object if found,
     *         a not-found message if the user does not exist,
     *         or an internal server error message in case of an unexpected error
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a user's data based on their username.
     * Handles exceptions for when the user is not found or other unexpected errors occur.
     *
     * @param username the username of the user to be retrieved
     * @return a {@code ResponseEntity} containing the user's data as {@code UserDataDto} if found,
     *         a not-found message if the user does not exist,
     *         or an internal server error message in case of an unexpected error
     */
    @GetMapping("/getByUsername/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            UserDataDto userDataDto = userService.getUserByUsername(username);
            return ResponseEntity.ok(userDataDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a {@code ResponseEntity} containing a list of users as a {@code List<User>} object
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Updates the details of an existing user based on the provided user ID and update information.
     * Handles exceptions for a user not being found, conflicting or invalid data, and other unexpected errors.
     *
     * @param id the unique identifier of the user to be updated
     * @param updateUserDto the data transfer object containing updated user details
     * @return a {@code ResponseEntity} containing the updated user object on success,
     *         a not-found message if the user does not exist,
     *         a bad request message for invalid or conflicting data,
     *         or an internal server error message for unexpected errors
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        try {
            User updatedUser = userService.updateUser(id, updateUserDto);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserAlreadyExistsException | IllegalArgumentException | InvalidUserDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Updates the password of a user identified by their unique user ID.
     * Validates that the new password is not empty or blank.
     * Handles exceptions for user not found, invalid input, or other unexpected issues.
     *
     * @param id the unique identifier of the user whose password is to be updated
     * @param body a map containing the new password with the key "newPassword"
     * @return a {@code ResponseEntity} containing the updated user object if successful,
     *         a bad request message if the input is invalid,
     *         a not-found message if the user does not exist,
     *         or an internal server error message in case of unexpected errors
     */
    @PatchMapping("/updatepass/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            User user = userService.updatePassword(id, newPassword);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Disables a user identified by their unique ID, marking them as inactive.
     * Handles exceptions for when the user is not found or other unexpected errors occur.
     *
     * @param id the unique identifier of the user to be disabled
     * @return a {@code ResponseEntity} containing the updated user object if successful,
     *         a not-found message if the user does not exist,
     *         or an internal server error message for unexpected errors
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        try {
            User user = userService.disableUser(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Activates a user identified by their unique ID, marking them as active.
     * Handles exceptions for when the user is not found or other unexpected errors.
     *
     * @param id the unique identifier of the user to be activated
     * @return a {@code ResponseEntity} containing the updated user object if successful,
     *         a not-found message if the user does not exist,
     *         or an internal server error message for unexpected errors
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            User user = userService.activateUser(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Deletes a user identified by their unique ID.
     * Handles exceptions for when the user is not found or other unexpected errors.
     *
     * @param id the unique identifier of the user to be deleted
     * @return a {@code ResponseEntity} with no content if the deletion is successful,
     *         a not-found message if the user does not exist,
     *         or an internal server error message in case of unexpected errors
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Handles HTTP GET requests to count the total number of clients.
     *
     * @return a ResponseEntity containing the total count of clients as a Long.
     */
    @GetMapping("/count/clients")
    public ResponseEntity<Long> countClients() {
        return ResponseEntity.ok(userService.countClients());
    }

    /**
     * Counts the total number of employees.
     *
     * @return a ResponseEntity containing the total count of employees as a Long.
     */
    @GetMapping("/count/employees")
    public ResponseEntity<Long> countEmployees() {
        return ResponseEntity.ok(userService.countEmployees());
    }

    /**
     * Retrieves all users with the role CUSTOMER.
     *
     * @return list of customers
     */
    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customers);
    }

}

