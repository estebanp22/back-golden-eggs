package com.goldeneggs.InventoryMovement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class InventoryMovementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryMovementService movementService;

    @InjectMocks
    private InventoryMovementController movementController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private InventoryMovement movement;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(movementController).build();
        mockMvc = MockMvcBuilders.standaloneSetup(movementController).build();

        //Create test role
        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        //Create test user
        RegisterDto registerDto = new RegisterDto();
        registerDto.setId(1L);
        registerDto.setName("Felipe");
        registerDto.setPhoneNumber("1234567");
        registerDto.setEmail("test@example.com");
        registerDto.setUsername("felipe123");
        registerDto.setPassword("Password1");
        registerDto.setAddress("Calle Falsa 123");
        registerDto.setRoleId(role.getId());

        User user = new User();
        user.setId(registerDto.getId());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setName(registerDto.getName());
        user.setAddress(registerDto.getAddress());
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singletonList(role));
        user.setEnabled(true);

        //Crate test supplier
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("Avicola la floresta")
                .address("Km1 via Calarca-Armenia")
                .build();

        //Crete type egg test
        TypeEgg typeEgg = TypeEgg.builder()
                .id(1L)
                .type("AA")
                .build();

        //Create egg test
        // Usando java.sql.Date
        Egg egg = Egg.builder()
                .id(1L)
                .type(typeEgg)
                .color("Blanco")
                .buyPrice(12000.0)
                .salePrice(13000.0)
                .expirationDate(new java.sql.Date(System.currentTimeMillis()))  // Usando java.sql.Date
                .supplier(supplier)
                .avibleQuantity(1000)
                .movements(Collections.emptyList())
                .build();

        //Create egg of order
        OrderEgg orderEgg = OrderEgg.builder()
                .id(1L)
                .egg(egg)
                .quantity(10)
                .unitPrice(12000.0)
                .subtotal(120000.0)
                .build();

        //Create egg of order
        OrderEgg orderEgg2 = OrderEgg.builder()
                .id(2L)
                .egg(egg)
                .quantity(5)
                .unitPrice(12000.0)
                .subtotal(60000.0)
                .build();

        //Create order
        // Usando java.sql.Date
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .orderEggs(List.of(orderEgg, orderEgg2))
                .totalPrice(180000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))  // Usando java.sql.Date
                .state("ENVIADO")
                .build();

        //Put the order in orderEggs
        orderEgg.setOrder(order);
        orderEgg2.setOrder(order);

        movement = InventoryMovement.builder()
                .id(1L)
                .movementDate(new java.sql.Date(System.currentTimeMillis()))
                .combs(10)
                .egg(egg)
                .order(order)
                .user(user)
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        when(movementService.getAll()).thenReturn(List.of(movement));

        mockMvc.perform(get("/api/v1/inventories/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(movement.getId()));
    }

    @Test
    void testDeleteMovement_Success() throws Exception {
        Long moveId = 1L;

        doNothing().when(movementService).delete(moveId);

        mockMvc.perform(delete("/api/v1/inventories/delete/{id}", moveId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMovement_NotFound() throws Exception {
        Long moveId = 1L;

        doThrow(new ResourceNotFoundException("Movement not found")).when(movementService).delete(moveId);

        mockMvc.perform(delete("/api/v1/inventories/delete/{id}", moveId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMovementById_Success() throws Exception {
        Long moveId = 1L;

        when(movementService.get(moveId)).thenReturn(movement);

        mockMvc.perform(get("/api/v1/inventories/get/{id}", moveId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movement.getId()))
                .andExpect(jsonPath("$.combs").value(movement.getCombs()));
    }

    @Test
    void testGetMovementById_NotFound() throws Exception {
        Long moveId = 1L;

        when(movementService.get(moveId)).thenThrow(new ResourceNotFoundException("InventoryMovement not found"));

        mockMvc.perform(get("/api/v1/inventories/get/{id}", moveId))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSaveMovement_Success() throws Exception {
        when(movementService.save(any(InventoryMovement.class))).thenReturn(movement);

        mockMvc.perform(post("/api/v1/inventories/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(movement.getId()))
                .andExpect(jsonPath("$.combs").value(movement.getCombs()));
    }

    @Test
    void testSaveMovement_InvalidData() throws Exception {
        when(movementService.save(any(InventoryMovement.class)))
                .thenThrow(new InvalidInventoryMovementDataException("Datos inválidos"));

        mockMvc.perform(post("/api/v1/inventories/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movement)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Datos inválidos"));
    }

    @Test
    void testUpdateMovement_Success() throws Exception {
        Long moveId = 1L;

        when(movementService.update(eq(moveId), any(InventoryMovement.class))).thenReturn(movement);

        mockMvc.perform(put("/api/v1/inventories/update/{id}", moveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(movement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movement.getId()))
                .andExpect(jsonPath("$.combs").value(movement.getCombs()));
    }

    @Test
    void testUpdateMovement_NotFound() throws Exception {
        Long moveId = 1L;

        when(movementService.update(eq(moveId), any(InventoryMovement.class)))
                .thenThrow(new ResourceNotFoundException("Inventory movement not found"));

        mockMvc.perform(put("/api/v1/inventories/update/{id}", moveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(movement)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateMovemebt_InvalidData() throws Exception {
        Long moveId = 1L;

        when(movementService.update(eq(moveId), any(InventoryMovement.class)))
                .thenThrow(new InvalidInventoryMovementDataException("Invalid movement data"));

        mockMvc.perform(put("/api/v1/inventories/update/{id}", moveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(movement)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid movement data"));
    }
}
