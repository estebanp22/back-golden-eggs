package com.goldeneggs.User;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param registerDto the data for registering a new user.
     * @return the created user.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            return ResponseEntity.ok(userService.save(registerDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user with the specified ID.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Updates a user with the provided data.
     *
     * @param id            the ID of the user to update.
     * @param updateUserDto the updated user information.
     * @return the updated user.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDto));
    }

    /**
     * Updates the password for a user.
     *
     * @param id          the ID of the user.
     * @param newPassword the new password.
     * @return the updated user with the new password.
     */
    @PatchMapping("/updatepass/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(id, newPassword));
    }

    /**
     * Disables a user by ID.
     *
     * @param id the ID of the user to disable.
     * @return the disabled user.
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<User> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.disableUser(id));
    }

    /**
     * Enables a user by ID.
     *
     * @param id the ID of the user to enable.
     * @return the enabled user.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete.
     * @return HTTP 204 No Content status.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

