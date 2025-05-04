package com.goldeneggs.User;


import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Dto.UserDataDto;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Exception.UserAlreadyExistsException;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user if the username and ID do not already exist.
     *
     * @param registerDto Data transfer object containing user registration data.
     * @return The saved User entity.
     * @throws UserAlreadyExistsException if the username or ID already exists.
     * @throws ResourceNotFoundException if the specified role is not found.
     */
    @Override
    public User save(RegisterDto registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException("A user with the username '" + registerDto.getUsername() + "' already exists.");
        }

        if (userRepository.existsById(registerDto.getId())) {
            throw new UserAlreadyExistsException("A user with the ID '" + registerDto.getId() + "' already exists.");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setId(registerDto.getId());
        user.setAddress(registerDto.getAddress());
        user.setEmail(registerDto.getEmail());
        user.setEnabled(true);
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setName(registerDto.getName());

        Role role = getRoleOrThrow(registerDto.getRoleId());
        user.setRoles(Collections.singletonList(role));

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user.
     * @return The User entity.
     * @throws ResourceNotFoundException if no user with the given ID is found.
     */
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public UserDataDto getUserByUsername(String username) {
        User user =userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Username: " + username));
        
        UserDataDto userDataDto = new UserDataDto();
        userDataDto.setUsername(user.getUsername());
        userDataDto.setId(user.getId());
        userDataDto.setName(user.getName());
        userDataDto.setPhoneNumber(user.getPhoneNumber());
        userDataDto.setEmail(user.getEmail());
        userDataDto.setAddress(user.getAddress());
        userDataDto.setRoles(user.getRoles());

        return userDataDto;
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId The ID of the user to delete.
     * @throws ResourceNotFoundException if no user with the given ID is found.
     */
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.deleteById(userId);
    }

    /**
     * Updates an existing user's information. Only non-null and non-blank fields will be updated.
     *
     * @param id The ID of the user to update.
     * @param updateUserDto DTO containing the new data.
     * @return The updated User entity.
     * @throws ResourceNotFoundException if no user or specified role is found.
     */
    @Override
    public User updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().isBlank()) {
            if (userRepository.existsByUsername(updateUserDto.getUsername()) && !user.getUsername().equals(updateUserDto.getUsername())) {
                throw new UserAlreadyExistsException("A user with the username '" + updateUserDto.getUsername() + "' already exists.");
            }
            user.setUsername(updateUserDto.getUsername());
        }

        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        if (updateUserDto.getName() != null && !updateUserDto.getName().isBlank()) {
            user.setName(updateUserDto.getName());
        }

        if (updateUserDto.getPhoneNumber() != null && !updateUserDto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(updateUserDto.getPhoneNumber());
        }

        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isBlank()) {
            user.setEmail(updateUserDto.getEmail());
        }

        if (updateUserDto.getAddress() != null && !updateUserDto.getAddress().isBlank()) {
            user.setAddress(updateUserDto.getAddress());
        }

        if (updateUserDto.getRoleId() != null) {
            Role newRole = getRoleOrThrow(updateUserDto.getRoleId());
            user.setRoles(Collections.singletonList(newRole));
        }

        return userRepository.save(user);
    }

    /**
     * Disables a user account.
     *
     * @param id The ID of the user to disable.
     * @return The updated User entity with the disabled status.
     * @throws ResourceNotFoundException if no user with the given ID is found.
     */
    @Override
    public User disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.disabled();
        return userRepository.save(user);
    }

    /**
     * Activates a previously disabled user account.
     *
     * @param id The ID of the user to activate.
     * @return The updated User entity with the enabled status.
     * @throws ResourceNotFoundException if no user with the given ID is found.
     */
    @Override
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.enabled();
        return userRepository.save(user);
    }

    /**
     * Updates the password of an existing user.
     *
     * @param userId The ID of the user whose password will be updated.
     * @param newPassword The new password to be set.
     * @return The updated User entity.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws IllegalArgumentException if the new password is null or blank.
     */
    @Override
    public User updatePassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be null or blank.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A list of all User entities.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Helper method to find a role by ID or throw an exception if not found.
     *
     * @param roleId The ID of the role.
     * @return The Role entity.
     * @throws ResourceNotFoundException if the role is not found.
     */
    private Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
    }
}
