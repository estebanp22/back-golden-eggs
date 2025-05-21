package com.goldeneggs.Pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Role.Role;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class PayControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PayService payService;

    @InjectMocks
    private PayController payController;

    private ObjectMapper objectMapper;
    private Pay pay;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(payController).build();
        objectMapper = new ObjectMapper();

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

        pay = new Pay();
        pay.setId(1L);
        pay.setUser(user);
        pay.setBill(new Bill());
        pay.setAmountPaid(100.0);
        pay.setPaymentMethod("EFECTIVO");
    }

    @Test
    void testGetAllPays_ReturnsList() throws Exception {
        when(payService.getAll()).thenReturn(List.of(pay));

        mockMvc.perform(get("/api/v1/payments/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pay.getId()));
    }


    @Test
    void testGetPayById_Found() throws Exception {
        when(payService.get(1L)).thenReturn(pay);

        mockMvc.perform(get("/api/v1/payments/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pay.getId()));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(payService.get(1L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/payments/get/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSavePay_Valid() throws Exception {
        when(payService.save(any(Pay.class))).thenReturn(pay);

        mockMvc.perform(post("/api/v1/payments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pay.getId()));
    }

    @Test
    void testSavePay_Invalid() throws Exception {
        when(payService.save(any(Pay.class)))
                .thenThrow(new InvalidPayDataException("Invalid pay"));

        mockMvc.perform(post("/api/v1/payments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pay)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid pay"));
    }

    @Test
    void testUpdatePay_Valid() throws Exception {
        when(payService.update(eq(1L), any(Pay.class))).thenReturn(pay);

        mockMvc.perform(put("/api/v1/payments/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pay)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pay.getId()));
    }

    @Test
    void testUpdatePay_NotFound() throws Exception {
        when(payService.update(eq(1L), any(Pay.class)))
                .thenThrow(new ResourceNotFoundException("Pay not found"));

        mockMvc.perform(put("/api/v1/payments/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pay)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePay_InvalidData() throws Exception {
        Long payId = 1L;

        when(payService.update(eq(payId), any(Pay.class)))
                .thenThrow(new InvalidPayDataException("Invalid pay data"));

        mockMvc.perform(put("/api/v1/payments/update/{id}", payId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pay)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid pay data"));
    }

    @Test
    void testDelete_Success() throws Exception {
        doNothing().when(payService).delete(1L);

        mockMvc.perform(delete("/api/v1/payments/delete/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Pay not found"))
                .when(payService).delete(1L);

        mockMvc.perform(delete("/api/v1/payments/delete/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTotalIncome() throws Exception {
        when(payService.totalIncome()).thenReturn(5000.0);

        mockMvc.perform(get("/api/v1/payments/totalIncome"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.0"));

        verify(payService).totalIncome();
    }

    @Test
    void testGetTotalIncomeThisMonth() throws Exception {
        when(payService.totalIncomeCurrentMonth()).thenReturn(1500.0);

        mockMvc.perform(get("/api/v1/payments/totalIncomeCurrentMonth"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));

        verify(payService).totalIncomeCurrentMonth();
    }
}
