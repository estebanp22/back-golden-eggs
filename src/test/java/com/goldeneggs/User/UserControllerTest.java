package com.goldeneggs.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Dto.UserDataDto;
import com.goldeneggs.Exception.InvalidUserDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Exception.UserAlreadyExistsException;
import com.goldeneggs.Role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private RegisterDto registerDto;
    private UpdateUserDto updateUserDto;
    private UserDataDto userDataDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEnabled(true);

        registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setPassword("password123");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setUsername("updateduser");

        userDataDto = new UserDataDto();
        userDataDto.setUsername("testuser");
        userDataDto.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegisterUser_Success() throws Exception {
        when(userService.save(any(RegisterDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegisterUser_Conflict() throws Exception {
        when(userService.save(any(RegisterDto.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));


    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegisterUser_BadRequest() throws Exception {
        when(userService.save(any(RegisterDto.class))).thenThrow(new InvalidUserDataException("Invalid data"));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/get/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserByUsername_Success() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(userDataDto);

        mockMvc.perform(get("/api/v1/users/getByUsername/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserByUsername_NotFound() throws Exception {
        when(userService.getUserByUsername("testuser")).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/getByUsername/testuser"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserDto.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateUser_NotFound() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateUser_BadRequest() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserDto.class)))
                .thenThrow(new InvalidUserDataException("Invalid update"));

        mockMvc.perform(put("/api/v1/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid update"));


    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdatePassword_Success() throws Exception {
        Map<String, String> body = Map.of("newPassword", "newpass123");

        when(userService.updatePassword(1L, "newpass123")).thenReturn(user);

        mockMvc.perform(patch("/api/v1/users/updatepass/password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdatePassword_NotFound() throws Exception {
        Map<String, String> body = Map.of("newPassword", "newpass123");

        when(userService.updatePassword(1L, "newpass123"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(patch("/api/v1/users/updatepass/password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDisableUser_Success() throws Exception {
        when(userService.disableUser(1L)).thenReturn(user);

        mockMvc.perform(patch("/api/v1/users/1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDisableUser_NotFound() throws Exception {
        when(userService.disableUser(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(patch("/api/v1/users/1/disable"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testActivateUser_Success() throws Exception {
        when(userService.activateUser(1L)).thenReturn(user);

        mockMvc.perform(patch("/api/v1/users/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testActivateUser_NotFound() throws Exception {
        when(userService.activateUser(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(patch("/api/v1/users/1/activate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updatePassword_ShouldReturnBadRequest_WhenPasswordIsInvalid() throws Exception {
        Long userId = 1L;
        String newPassword = " ";
        Map<String, String> body = Map.of("newPassword", newPassword);

        when(userService.updatePassword(eq(userId), eq(newPassword)))
                .thenThrow(new IllegalArgumentException("New password must not be null or blank."));

        mockMvc.perform(patch("/api/v1/users/updatepass/password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New password must not be null or blank."));

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void countClients_ShouldReturnOkWithCount() throws Exception {
        when(userService.countClients()).thenReturn(12L);

        mockMvc.perform(get("/api/v1/users/count/clients"))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void countEmployees_ShouldReturnOkWithCount() throws Exception {
        when(userService.countEmployees()).thenReturn(5L);

        mockMvc.perform(get("/api/v1/users/count/employees"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllCustomers_ShouldReturnListOfCustomers() throws Exception {
        User customer = new User();
        customer.setUsername("customer1");

        when(userService.getAllCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/v1/users/getAllCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("customer1"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllCustomers_ShouldReturnNoContent() throws Exception {
        when(userService.getAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/getAllCustomers"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployee_ReturnsOk() throws Exception {
        Role role = Role.builder().name("EMPLOYEE").build();
        User user = new User();
        user.setId(1L);
        user.setUsername("employee1");
        user.setRoles(List.of(role));

        when(userService.getAllEmployee()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users/getAllEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("employee1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployee_ReturnsNoContent() throws Exception {
        when(userService.getAllEmployee()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/getAllEmployee"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployeeDisabled_ReturnsOk() throws Exception {
        Role role = Role.builder().name("EMPLOYEE").build();

        User user = new User();
        user.setId(2L);
        user.setUsername("employee2");
        user.setEnabled(false);
        user.setRoles(List.of(role));

        when(userService.getAllDisabledEmployess()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users/getAllEmployeeDisabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("employee2"))
                .andExpect(jsonPath("$[0].enabled").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployeeDisabled_ReturnsNoContent() throws Exception {
        when(userService.getAllDisabledEmployess()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/getAllEmployeeDisabled"))
                .andExpect(status().isNoContent());
    }


}
