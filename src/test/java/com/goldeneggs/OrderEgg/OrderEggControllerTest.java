package com.goldeneggs.OrderEgg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidOrderEggDataException;
import com.goldeneggs.Exception.InvalidTypeEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.TypeEgg.TypeEggService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderEggControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderEggService orderEggService;
    
    @InjectMocks
    private OrderEggController orderEggController;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private OrderEgg orderEgg;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderEggController).build();

        orderEgg = new OrderEgg();
        orderEgg.setId(1L);
        orderEgg.setQuantity(5);
        orderEgg.setUnitPrice(10.0);
        orderEgg.setSubtotal(50.0);
        orderEgg.setOrder(new Order());
        orderEgg.setEgg(new Egg());
    }

    @Test
    void testSave_Success() throws Exception {
        when(orderEggService.save(any(OrderEgg.class))).thenReturn(orderEgg);

        mockMvc.perform(post("/api/v1/orderEggs/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderEgg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderEgg.getId()));
    }

    @Test
    void testSave_Invalid() throws Exception {
        when(orderEggService.save(any(OrderEgg.class)))
                .thenThrow(new InvalidOrderEggDataException("Invalid data"));

        mockMvc.perform(post("/api/v1/orderEggs/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderEgg)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    void testGetById_Success() throws Exception {
        when(orderEggService.get(1L)).thenReturn(orderEgg);

        mockMvc.perform(get("/api/v1/orderEggs/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEgg.getId()));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(orderEggService.get(1L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/orderEggs/get/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        when(orderEggService.getAll()).thenReturn(List.of(orderEgg));

        mockMvc.perform(get("/api/v1/orderEggs/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderEgg.getId()));
    }


    @Test
    void testUpdate_Success() throws Exception {
        when(orderEggService.update(eq(1L), any(OrderEgg.class)))
                .thenReturn(orderEgg);

        mockMvc.perform(put("/api/v1/orderEggs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderEgg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEgg.getId()));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        when(orderEggService.update(eq(1L), any(OrderEgg.class)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/orderEggs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderEgg)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Invalid() throws Exception {
        when(orderEggService.update(eq(1L), any(OrderEgg.class)))
                .thenThrow(new InvalidOrderEggDataException("Invalid"));

        mockMvc.perform(put("/api/v1/orderEggs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderEgg)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/orderEggs/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found"))
                .when(orderEggService).delete(1L);

        mockMvc.perform(delete("/api/v1/orderEggs/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }
}
