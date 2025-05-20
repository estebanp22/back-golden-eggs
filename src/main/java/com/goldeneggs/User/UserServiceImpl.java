package com.goldeneggs.User;


import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Dto.UserDataDto;
import com.goldeneggs.Exception.InvalidUserDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Exception.UserAlreadyExistsException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Order.OrderRepository;
import com.goldeneggs.Pay.PayRepository;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Role.RoleRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PayRepository payRepository;

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
        // Validate input data first
        try{
            UserValidator.validateRegisterDto(registerDto);
        } catch (InvalidUserDataException ex){
            throw new InvalidUserDataException(ex.getMessage());
        }

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + registerDto.getUsername() + "' already exists");
        }

        if (userRepository.existsById(registerDto.getId())) {
            throw new UserAlreadyExistsException("User with ID " + registerDto.getId() + " already exists");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + registerDto.getEmail() + "' already exists");
        }

        if (userRepository.existsByPhoneNumber(registerDto.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone number '" + registerDto.getPhoneNumber() + "' already exists");
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
     * Deletes a user from the system along with all associated orders, bills,
     * and payment records.
     *
     * This method performs the following operations:
     * - Retrieves the user by their ID. If the user does not exist, a
     *   ResourceNotFoundException is thrown.
     * - Searches for all orders associated with the user.
     * - Iterates through each order and finds associated bills. For each bill,
     *   all payments linked to the bill are deleted, followed by the deletion of the bill itself.
     * - Deletes all orders associated with the user.
     * - Deletes the user.
     *
     * This method is transactional to ensure atomicity and consistency of the deletion
     * process.
     *
     * @param userId the unique identifier of the user to be deleted
     * @throws ResourceNotFoundException if no user is found with the provided ID
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .toList();

        for (Order order : orders) {
            billRepository.findAll().stream()
                    .filter(bill -> bill.getOrder().getId().equals(order.getId()))
                    .findFirst()
                    .ifPresent(bill -> {
                        payRepository.deleteAllByBill(bill);
                        billRepository.delete(bill);
                    });

            orderRepository.delete(order);
        }
        userRepository.delete(user);
    }


    /**
     * Updates an existing user with the provided details.
     *
     * @param id the unique identifier of the user to be updated
     * @param updateUserDto the data transfer object containing the updated user details
     * @return the updated User object after persisting changes to the repository
     * @throws UserAlreadyExistsException if the updated attributes (e.g., username, email, phone number)
     *         conflict with existing users
     * @throws InvalidUserDataException if the provided data is invalid
     * @throws ResourceNotFoundException if no user is found with the specified ID
     */
    @Override
    public User updateUser(Long id, UpdateUserDto updateUserDto) {

        try{
            UserValidator.validateId(id);
            UserValidator.validateUpdateUserDto(updateUserDto);
        }  catch (InvalidUserDataException ex){
            throw new InvalidUserDataException(ex.getMessage());
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (updateUserDto.getUsername() != null && !user.getUsername().equals(updateUserDto.getUsername())) {
            if (userRepository.existsByUsername(updateUserDto.getUsername())) {
                throw new UserAlreadyExistsException("Username '" + updateUserDto.getUsername() + "' already exists");
            }
            user.setUsername(updateUserDto.getUsername());
        }

        if (updateUserDto.getEmail() != null && !user.getEmail().equals(updateUserDto.getEmail())) {
            if (userRepository.existsByEmail(updateUserDto.getEmail())) {
                throw new UserAlreadyExistsException("Email '" + updateUserDto.getEmail() + "' already exists");
            }
            user.setEmail(updateUserDto.getEmail());
        }

        if (updateUserDto.getPhoneNumber() != null && !user.getPhoneNumber().equals(updateUserDto.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(updateUserDto.getPhoneNumber())) {
                throw new UserAlreadyExistsException("Phone number '" + updateUserDto.getPhoneNumber() + "' already exists");
            }
            user.setPhoneNumber(updateUserDto.getPhoneNumber());
        }

        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
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

    /**
     * Retrieves all users that have the CUSTOMER role.
     *
     * @return list of users with role CUSTOMER
     */
    @Override
    public List<User> getAllCustomers() {
        return userRepository.findAllByRoleNameAndEnabledIsTrue("CUSTOMER");
    }


    /**
     * Counts the number of users with the role "Customer".
     *
     * @return The total count of clients as a Long value.
     */
    @Override
    public Long countClients() {
        return userRepository.countUsersByRoleName("Customer");
    }

    /**
     * Counts the number of users with the role "Employee".
     *
     * @return The total count of employees as a Long value.
     */
    @Override
    public Long countEmployees() {
        return userRepository.countUsersByRoleName("Employee");
    }

}
