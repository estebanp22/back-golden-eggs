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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private OrderRepository orderRepository;
    @Mock private BillRepository billRepository;
    @Mock private PayRepository payRepository;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private RegisterDto registerDto;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        registerDto = new RegisterDto();
        registerDto.setId(1L);
        registerDto.setName("Esteban");
        registerDto.setPhoneNumber("1234567");
        registerDto.setEmail("test@example.com");
        registerDto.setUsername("esteban123");
        registerDto.setPassword("Password1");
        registerDto.setAddress("Calle Falsa 123");
        registerDto.setRoleId(role.getId());

        user = new User();
        user.setId(registerDto.getId());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setName(registerDto.getName());
        user.setAddress(registerDto.getAddress());
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singletonList(role));
        user.setEnabled(true);
    }

    // ----------- save(RegisterDto)  ------------

    @Test
    void saveUser_Success() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsById(registerDto.getId())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerDto.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(registerDto);

        assertNotNull(savedUser);
        assertEquals(registerDto.getUsername(), savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_ThrowsUserAlreadyExistsException_WhenUsernameExists() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.save(registerDto));

        assertEquals("Username '" + registerDto.getUsername() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ThrowsUserAlreadyExistsException_WhenIdExists() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsById(registerDto.getId())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.save(registerDto));

        assertEquals("User with ID " + registerDto.getId() + " already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ThrowsUserAlreadyExistsException_WhenEmailExists() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsById(registerDto.getId())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.save(registerDto));

        assertEquals("Email '" + registerDto.getEmail() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ThrowsUserAlreadyExistsException_WhenPhoneExists() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsById(registerDto.getId())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerDto.getPhoneNumber())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.save(registerDto));

        assertEquals("Phone number '" + registerDto.getPhoneNumber() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ThrowsResourceNotFoundException_WhenRoleNotFound() {
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsById(registerDto.getId())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerDto.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.save(registerDto));

        assertEquals("Role not found with ID: " + role.getId(), ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ----------- getUserByUsername(String) ------------

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername("esteban123")).thenReturn(Optional.of(user));

        UserDataDto foundUser = userService.getUserByUsername("esteban123");

        assertNotNull(foundUser);
        assertEquals("esteban123", foundUser.getUsername());
    }

    @Test
    void getUserByUsername_ThrowsResourceNotFoundException_WhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("unknown"));

        assertTrue(ex.getMessage().contains("User not found with Username:"));
    }

    // ----------- updateUser(Long, UpdateUserDto) ------------
    @Test
    void updateUser_updateRole_success() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setRoleId(10L);

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setRoles(Collections.emptyList());

        Role role = new Role();
        role.setId(10L);
        role.setName("EMPLOYEE");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        doReturn(role).when(userService).getRoleOrThrow(10L);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(id, dto);

        assertEquals(1, updated.getRoles().size());
        assertEquals("EMPLOYEE", updated.getRoles().get(0).getName());
        verify(userRepository).save(updated);
    }

    @Test
    void updateUser_ThrowsResourceNotFoundException_WhenUserNotFound() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("newusername");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateDto));

        assertTrue(ex.getMessage().contains("User not found with ID"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ThrowsUserAlreadyExistsException_WhenUsernameExists() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("duplicateUsername");
        updateDto.setEmail("newemail@example.com");
        updateDto.setPhoneNumber("7654321");
        updateDto.setPassword("NewPass1");
        updateDto.setAddress("New Address");
        updateDto.setRoleId(role.getId());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDto.getUsername())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));

        assertEquals("Username '" + updateDto.getUsername() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ThrowsUserAlreadyExistsException_WhenEmailExists() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("newusername");
        updateDto.setEmail("duplicate@example.com");
        updateDto.setPhoneNumber("7654321");
        updateDto.setPassword("NewPass1");
        updateDto.setAddress("New Address");
        updateDto.setRoleId(role.getId());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));

        assertEquals("Email '" + updateDto.getEmail() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ThrowsUserAlreadyExistsException_WhenPhoneExists() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("newusername");
        updateDto.setEmail("newemail@example.com");
        updateDto.setPhoneNumber("123456789");
        updateDto.setPassword("NewPass1");
        updateDto.setAddress("New Address");
        updateDto.setRoleId(role.getId());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));

        assertEquals("Phone number '" + updateDto.getPhoneNumber() + "' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ThrowsResourceNotFoundException_WhenRoleNotFound() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setUsername("newusername");
        updateDto.setEmail("newemail@example.com");
        updateDto.setPhoneNumber("7654321");
        updateDto.setPassword("NewPass1");
        updateDto.setAddress("New Address");
        updateDto.setRoleId(999L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findById(updateDto.getRoleId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateDto));

        assertEquals("Role not found with ID: " + updateDto.getRoleId(), ex.getMessage());
        verify(userRepository, never()).save(any());
    }

// --- Activate User ---
    @Test
    void activateUser_Success() {
    user.setEnabled(false);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User activatedUser = userService.activateUser(1L);

    assertTrue(activatedUser.isEnabled());
    verify(userRepository).save(user);
}

    @Test
    void activateUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.activateUser(999L));

        assertTrue(ex.getMessage().contains("User not found with ID"));
        verify(userRepository, never()).save(any());
    }

    // --- Disable User ---
    @Test
    void disableUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User disabledUser = userService.disableUser(1L);

        assertFalse(disabledUser.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void disableUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.disableUser(999L));

        assertTrue(ex.getMessage().contains("User not found with ID"));
        verify(userRepository, never()).save(any());
    }

    // --- Change Password ---
    @Test
    void updatePassword_Success() {
        Long userId = 1L;
        String newPassword = "NewStrongPassword123";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updatePassword(userId, newPassword);

        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_ThrowsResourceNotFoundException() {
        Long userId = 999L;
        String newPassword = "NewStrongPassword123";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.updatePassword(userId, newPassword));

        assertTrue(ex.getMessage().contains("User not found with ID"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_userExists_deletesEverything() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Order order1 = new Order();
        order1.setId(10L);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(20L);
        order2.setUser(user);

        // Mock user found
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock orders list (filter in service streams)
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Bills for orders
        var bill1 = mock(com.goldeneggs.Bill.Bill.class);
        var bill2 = mock(com.goldeneggs.Bill.Bill.class);

        when(billRepository.findAll()).thenReturn(List.of(bill1, bill2));

        // bill1 linked to order1, bill2 linked to order2
        when(bill1.getOrder()).thenReturn(order1);
        when(bill2.getOrder()).thenReturn(order2);

        // payRepository.deleteAllByBill should be called
        doNothing().when(payRepository).deleteAllByBill(bill1);
        doNothing().when(payRepository).deleteAllByBill(bill2);

        // billRepository.delete should be called
        doNothing().when(billRepository).delete(bill1);
        doNothing().when(billRepository).delete(bill2);

        // orderRepository.delete should be called
        doNothing().when(orderRepository).delete(order1);
        doNothing().when(orderRepository).delete(order2);

        // userRepository.delete should be called
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);

        verify(payRepository).deleteAllByBill(bill1);
        verify(payRepository).deleteAllByBill(bill2);
        verify(billRepository).delete(bill1);
        verify(billRepository).delete(bill2);
        verify(orderRepository).delete(order1);
        verify(orderRepository).delete(order2);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(99L));

        assertEquals("User not found with ID: 99", ex.getMessage());
    }

    @Test
    void updateUser_userNotFound_throwsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(1L, new UpdateUserDto()));

        assertTrue(ex.getMessage().contains("User not found with ID"));
    }

    @Test
    void save_validRegisterDto_savesUser() {
        RegisterDto dto = new RegisterDto();
        dto.setId(1L);
        dto.setUsername("username");
        dto.setPassword("Pass123");
        dto.setEmail("email@example.com");
        dto.setPhoneNumber("12345678");
        dto.setAddress("address");
        dto.setName("Name");
        dto.setRoleId(1L);

        Role role = new Role();
        role.setId(1L);

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsById(dto.getId())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findById(dto.getRoleId())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.save(dto);

        assertEquals(dto.getUsername(), savedUser.getUsername());
        assertEquals("encodedPass", savedUser.getPassword());
        assertEquals(dto.getId(), savedUser.getId());
        assertEquals(dto.getEmail(), savedUser.getEmail());
        assertEquals(dto.getPhoneNumber(), savedUser.getPhoneNumber());
        assertEquals(role, savedUser.getRoles().get(0));
    }

    @Test
    void save_existingUsername_throwsException() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("username");

        assertThrows(InvalidUserDataException.class,() -> userService.save(dto));
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertEquals(user, found);
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));

        assertTrue(ex.getMessage().contains("User not found with ID"));
    }

    @Test
    void updatePassword_ShouldUpdatePassword_WhenValidInput() {
        Long userId = 1L;
        String newPassword = "newPassword123";
        User user = new User();
        user.setId(userId);
        user.setPassword("oldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updatePassword(userId, newPassword);

        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_ShouldThrowException_WhenPasswordIsNull() {
        Long userId = 1L;
        String newPassword = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(userId, newPassword));

        assertEquals("New password must not be null or blank.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_ShouldThrowException_WhenPasswordIsBlank() {
        Long userId = 1L;
        String newPassword = "   ";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(userId, newPassword));

        assertEquals("New password must not be null or blank.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_ShouldThrowException_WhenUserNotFound() {
        Long userId = 99L;
        String newPassword = "password";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updatePassword(userId, newPassword));

        assertEquals("User not found with ID: 99", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void countClients_ShouldReturnClientCount() {
        when(userRepository.countUsersByRoleName("Customer")).thenReturn(7L);

        Long count = userService.countClients();

        assertEquals(7L, count);
        verify(userRepository).countUsersByRoleName("Customer");
    }

    @Test
    void countEmployees_ShouldReturnEmployeeCount() {
        when(userRepository.countUsersByRoleName("Employee")).thenReturn(4L);

        Long count = userService.countEmployees();

        assertEquals(4L, count);
        verify(userRepository).countUsersByRoleName("Employee");
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        User customer1 = new User();
        customer1.setUsername("customer1");
        User customer2 = new User();
        customer2.setUsername("customer2");

        Role customerRole = new Role();
        customerRole.setName("CUSTOMER");
        customer1.setRoles(List.of(customerRole));
        customer2.setRoles(List.of(customerRole));

        when(userRepository.findAllByRoleNameAndEnabledIsTrue("CUSTOMER"))
                .thenReturn(List.of(customer1, customer2));

        List<User> result = userService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("customer1", result.get(0).getUsername());
        verify(userRepository).findAllByRoleNameAndEnabledIsTrue("CUSTOMER");
    }

    @Test
    void testGetAllEmployee_ReturnsList() {
        Role role = Role.builder().name("EMPLOYEE").build();

        User user = new User();
        user.setId(1L);
        user.setUsername("employee1");
        user.setEnabled(true);
        user.setRoles(List.of(role));

        when(userRepository.findAllByRoleNameAndEnabledIsTrue("EMPLOYEE"))
                .thenReturn(List.of(user));

        List<User> result = userService.getAllEmployee();

        assertEquals(1, result.size());
        assertEquals("employee1", result.get(0).getUsername());
        verify(userRepository).findAllByRoleNameAndEnabledIsTrue("EMPLOYEE");
    }

    @Test
    void testGetAllEmployee_ReturnsEmptyList() {
        when(userRepository.findAllByRoleNameAndEnabledIsTrue("EMPLOYEE"))
                .thenReturn(Collections.emptyList());

        List<User> result = userService.getAllEmployee();

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByRoleNameAndEnabledIsTrue("EMPLOYEE");
    }

    @Test
    void testGetAllDisabledEmployees_ReturnsList() {
        Role role = Role.builder().name("EMPLOYEE").build();
        User user = new User();
        user.setId(2L);
        user.setUsername("employee2");
        user.setEnabled(false);
        user.setRoles(List.of(role));
        when(userRepository.findAllByRoleNameAndDisabledIsTrue("EMPLOYEE"))
                .thenReturn(List.of(user));

        List<User> result = userService.getAllDisabledEmployess();

        assertEquals(1, result.size());
        assertEquals("employee2", result.get(0).getUsername());
        verify(userRepository).findAllByRoleNameAndDisabledIsTrue("EMPLOYEE");
    }

    @Test
    void testGetAllDisabledEmployees_ReturnsEmptyList() {
        when(userRepository.findAllByRoleNameAndDisabledIsTrue("EMPLOYEE"))
                .thenReturn(Collections.emptyList());

        List<User> result = userService.getAllDisabledEmployess();

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByRoleNameAndDisabledIsTrue("EMPLOYEE");
    }

    @Test
    void updateUser_invalidData_throwsInvalidUserDataException() {
        Long invalidId = null;
        UpdateUserDto invalidDto = null;

        try (MockedStatic<UserValidator> mockedValidator = mockStatic(UserValidator.class)) {
            mockedValidator.when(() -> UserValidator.validateId(invalidId))
                    .thenThrow(new InvalidUserDataException("Invalid data"));

            mockedValidator.when(() -> UserValidator.validateUpdateUserDto(invalidDto))
                    .thenThrow(new InvalidUserDataException("Invalid data"));

            assertThrows(InvalidUserDataException.class, () -> userService.updateUser(invalidId, invalidDto));
        }
    }

    @Test
    void updateUser_userNotFound_throwsResourceNotFoundException() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void updateUser_updateNameAndAddress_success() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("New Name");
        dto.setAddress("New Address");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setName("Old Name");
        existingUser.setAddress("Old Address");
        existingUser.setUsername("user");
        existingUser.setEmail("email@test.com");
        existingUser.setPhoneNumber("12345");
        existingUser.setRoles(Collections.emptyList());

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(id, dto);

        assertEquals("New Name", updated.getName());
        assertEquals("New Address", updated.getAddress());
        verify(userRepository).save(updated);
    }

    @Test
    void updateUser_updateUsername_alreadyExists_throwsUserAlreadyExistsException() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername("existingUsername");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setUsername("oldUsername");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void updateUser_updateEmail_alreadyExists_throwsUserAlreadyExistsException() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("existingEmail@test.com");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail("oldEmail@test.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existingEmail@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void updateUser_updatePhoneNumber_alreadyExists_throwsUserAlreadyExistsException() {
        Long id = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPhoneNumber("1234567");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setPhoneNumber("9999999");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByPhoneNumber("1234567")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(id, dto));
    }
}
