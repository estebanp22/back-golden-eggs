package com.goldeneggs.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Dto.Order.OrderDTO;
import com.goldeneggs.Dto.Order.OrderItemDTO;
import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
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

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private Order order;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Configura MockMvc manualmente
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        User user = new User();
        user.setId(1L);
        user.setName("Felipe");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalPrice(15000.0);
        order.setOrderDate(Date.valueOf("2025-05-19"));
        order.setState("Pendiente");
        order.setOrderEggs(Collections.emptyList());
    }

    @Test
    void testGetAllOrders_ReturnsEmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders/getAll"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllOrders_ReturnsList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/v1/orders/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()));
    }

    @Test
    void testGetOrderById_Found() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(1L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/get/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveOrder_Valid() throws Exception {
        when(orderService.saveOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void testSaveOrder_Invalid() throws Exception {
        when(orderService.saveOrder(any(Order.class)))
                .thenThrow(new InvalidOrderDataException("Invalid order"));

        mockMvc.perform(post("/api/v1/orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order"));
    }

    @Test
    void testUpdateOrder_Valid() throws Exception {
        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(order);

        mockMvc.perform(put("/api/v1/orders/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void testUpdateOrder_NotFound() throws Exception {
        when(orderService.updateOrder(eq(1L), any(Order.class)))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(put("/api/v1/orders/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOrder_Valid() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/v1/orders/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteOrder_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found"))
                .when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/v1/orders/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetOrdersInCurrentMonth_Empty() throws Exception {
        when(orderService.getOrdersInCurrentMonth()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders/currentMonth"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetOrdersInCurrentMonth_NotEmpty() throws Exception {
        when(orderService.getOrdersInCurrentMonth()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/v1/orders/currentMonth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()));
    }

    @Test
    void testCountOrdersInCurrentMonth() throws Exception {
        when(orderService.countOrdersInCurrentMonth()).thenReturn(3L);

        mockMvc.perform(get("/api/v1/orders/countCurrentMonth"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testCountOrdersByCustomer() throws Exception {
        when(orderService.countOrdersByCustomerId(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/orders/countByCustomer/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testUpdateOrder_InvalidData() throws Exception {
        Long orderId = 1L;

        when(orderService.updateOrder(eq(orderId), any(Order.class)))
                .thenThrow(new InvalidOrderDataException("Invalid order data"));

        mockMvc.perform(put("/api/v1/orders/update/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order data"));
    }

    @Test
    void testGetAllOrdersDTO_ReturnsList() throws Exception {
        OrderDTO dto = OrderDTO.builder()
                .id(1L)
                .customerName("Juan Pérez")
                .status("PAID")
                .total(50000.0)
                .date("2025-05-24")
                .items(List.of(
                        OrderItemDTO.builder()
                                .productName("Huevo A")
                                .quantity(10)
                                .unitPrice(5000.0)
                                .build()
                ))
                .build();

        when(orderService.getAllAsDTO()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/orders/getAll/dto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].customerName").value(dto.getCustomerName()))
                .andExpect(jsonPath("$[0].status").value(dto.getStatus()))
                .andExpect(jsonPath("$[0].total").value(dto.getTotal()))
                .andExpect(jsonPath("$[0].date").value(dto.getDate()))
                .andExpect(jsonPath("$[0].items[0].productName").value("Huevo A"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(10))
                .andExpect(jsonPath("$[0].items[0].unitPrice").value(5000.0));

        verify(orderService).getAllAsDTO();
    }

}
