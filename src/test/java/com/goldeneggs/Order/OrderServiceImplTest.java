package com.goldeneggs.Order;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillServiceImpl;
import com.goldeneggs.Dto.Order.CartItemDTO;
import com.goldeneggs.Dto.Order.OrderDTO;
import com.goldeneggs.Dto.Order.OrderRequestDTO;
import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.Pay.PayServiceImpl;
import com.goldeneggs.Role.Role;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillServiceImpl billService;

    @Mock
    private PayServiceImpl payService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    OrderEgg oe1;
    OrderEgg oe2;
    CartItemDTO item1;
    CartItemDTO item2;

    @BeforeEach
    void setUp() {
        // Create test role
        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        // Create test user
        user = new User();
        user.setId(1L);
        user.setUsername("felipe123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567");
        user.setName("Felipe");
        user.setAddress("Calle Falsa 123");
        user.setPassword("encodedPassword");
        user.setRoles(List.of(role));
        user.setEnabled(true);

        // Create orderEggs
        oe1 = new OrderEgg();
        oe1.setType("AA");
        oe1.setColor("Blanco");
        oe1.setQuantity(10);
        oe1.setUnitPrice(12000.0);
        oe1.setSubtotal(120000.0);

        oe2 = new OrderEgg();
        oe2.setType("AA");
        oe2.setColor("Blanco");
        oe2.setQuantity(5);
        oe2.setUnitPrice(12000.0);
        oe2.setSubtotal(60000.0);

        // Create order
        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderEggs(List.of(oe1, oe2));
        order.setTotalPrice(180000.0);
        order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
        order.setState("ENVIADO");

        // DTO
        item1 = CartItemDTO.builder()
                .name("AA")
                .color("Blanco")
                .quantity(10)
                .price(12000.0)
                .build();

        item2 = CartItemDTO.builder()
                .name("AA")
                .color("Blanco")
                .quantity(5)
                .price(12000.0)
                .build();
    }



    @Test
    void countCustomerBillsInCurrentMonth_ShouldReturnCount() {
        when(orderRepository.countOrdersByCustomerId(any(Long.class)))
                .thenReturn(5L);

        Long result = orderService.countOrdersByCustomerId(user.getId());

        assertEquals(5L, result);
    }

    @Test
    void getOrderById_existingBill_returnsOrder() {
        Order orderAux = new Order();
        orderAux.setId(5L);

        when(orderRepository.findById(5L)).thenReturn(Optional.of(orderAux));

        Order found = orderService.getOrderById(5L);

        assertEquals(orderAux, found);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(99L));

        assertTrue(ex.getMessage().contains("Order not found with ID:"));
    }

    @Test
    void testUpdateOrder_ShouldUpdateOrderSuccessfully() {
        // Simular bill existente
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        long now = System.currentTimeMillis();
        Date futureDate = new Date(now);
        Order updated = Order.builder()
                .id(1L)
                .orderDate(futureDate)
                .totalPrice(99999.0)
                .state("RECIBIDO")
                .build();

        Order result = orderService.updateOrder(1L, updated);

        assertEquals(updated.getTotalPrice(), result.getTotalPrice());
        assertEquals(updated.getState(), result.getState());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void update_ShouldThrowException_WhenBillNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Order updated = new Order();

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, updated));

        verify(orderRepository).findById(1L);
    }

    @Test
    void delete_ShouldDeleteOrder_WhenExists() {
        // Create a mock order (adjust fields as needed)
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        // Mock repository behavior
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // Call the service
        orderService.deleteOrder(1L);

        // Verify the correct delete method was called
        verify(orderRepository).delete(mockOrder);  // Not deleteById(1L)
    }

    @Test
    void delete_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));

        verify(orderRepository, never()).deleteById(anyLong());
    }


    @Test
    void getAllOrder_ShouldReturnListOfOrder(){
        Order order2 = Order.builder()
                .id(2L)
                .user(user)
                .orderEggs(List.of(oe1, oe2))
                .totalPrice(1800000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
                .state("RECIBIDO")
                .build();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order2,order));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());

        Order orderAux = result.get(1);
        assertEquals(order.getId(), orderAux.getId());
        assertEquals(order.getUser(), orderAux.getUser());
        assertEquals(order.getState(), orderAux.getState());
        assertEquals(order.getOrderDate(), orderAux.getOrderDate());

        verify(orderRepository).findAll();
    }

    @Test
    void testGetOrdersInCurrentMonth_ReturnsListOfOrders() {
        List<Order> mockOrders = List.of(order);
        LocalDate now = LocalDate.now();
        java.util.Date start = java.util.Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.util.Date end = java.util.Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        when(orderRepository.findOrdersInCurrentMonth(start, end))
                .thenReturn(mockOrders);

        List<Order> result = orderService.getOrdersInCurrentMonth();

        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        verify(orderRepository).findOrdersInCurrentMonth(start, end);
    }

    @Test
    void testCountOrdersInCurrentMonth_ReturnsCount() {
        LocalDate now = LocalDate.now();
        java.util.Date start = java.util.Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.util.Date end = java.util.Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(orderRepository.countOrdersInCurrentMonth(eq(start), eq(end)))
                .thenReturn(5L);

        Long result = orderService.countOrdersInCurrentMonth();

        assertEquals(5L, result);
    }

    @Test
    void testCountOrdersInCurrentMonth_ReturnsZeroWhenNull() {
        LocalDate now = LocalDate.now();
        java.util.Date start = java.util.Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.util.Date end = java.util.Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(orderRepository.countOrdersInCurrentMonth(start, end))
                .thenReturn(null);

        Long result = orderService.countOrdersInCurrentMonth();

        assertEquals(0L, result);
    }

    @Test
    void testValidateOrderOrThrow_WithValidOrder_DoesNotThrow() {
        assertDoesNotThrow(() -> {
            var method = OrderServiceImpl.class.getDeclaredMethod("validateOrderOrThrow", Order.class);
            method.setAccessible(true);
            method.invoke(orderService, order);
        });
    }

    @Test
    void getAllOrdersDTO_ShouldReturnMappedDTOs() {
        Order order2 = Order.builder()
                .id(2L)
                .user(user)
                .orderEggs(List.of(oe1, oe2))
                .totalPrice(1800000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
                .state("RECIBIDO")
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order2, order));

        List<OrderDTO> result = orderService.getAllAsDTO();

        assertEquals(2, result.size());

        OrderDTO dto = result.get(1);
        assertEquals(order.getId(), dto.getId());
        assertEquals(user.getName(), dto.getCustomerName());
        assertEquals(order.getState(), dto.getStatus());
        assertEquals(order.getTotalPrice(), dto.getTotal());
        assertEquals(order.getOrderDate().toString(), dto.getDate());

        assertEquals(order.getOrderEggs().size(), dto.getItems().size());

        verify(orderRepository).findAll();
    }

    @Test
    void validateOrderOrThrow_shouldThrowIfUserIsNull() {
        Order order = new Order();
        order.setUser(null);

        assertThrows(InvalidOrderDataException.class, () -> orderService.validateOrderOrThrow(order));
    }

    @Test
    void cancelOrder_shouldSetStatusToCancelled() {
        Order order = new Order();
        order.setId(1L);
        order.setState(Order.STATE_PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(Order.STATE_CANCELED, order.getState());
        verify(orderRepository).save(order);
    }

    @Test
    void processOrder_shouldSetStatusAndCreateBill() {
        Order order = new Order();
        order.setId(5L);
        order.setState(Order.STATE_PENDING);

        Bill bill = new Bill();

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(billService.createBillForOrder(order)).thenReturn(bill);

        orderService.processOrder(5L, "Efectivo");

        assertEquals(Order.STATE_COMPLETED, order.getState());
        verify(billService).createBillForOrder(order);
        verify(payService).createPayForBill(bill, "Efectivo");
        verify(orderRepository).save(order);
    }

    @Test
    public void testSaveOrder() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setIdCustomer(1L);
        dto.setTotalPrice(180000.0);
        dto.setOrderDate(new Date(System.currentTimeMillis()));
        dto.setState("PENDING");

        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setName("Tipo A");
        cartItem.setColor("Blanco");
        cartItem.setQuantity(10);
        cartItem.setPrice(2.5);
        dto.setCartItem(Collections.singletonList(cartItem));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order saved = orderService.saveOrder(dto);

        assertEquals("Felipe", saved.getUser().getName());
        assertEquals(2, saved.getOrderEggs().size());
        assertEquals(180000.0, saved.getTotalPrice());
    }

    @Test
    public void testValidateOrderOrThrow_Invalid() {
        order.setTotalPrice(-1);
        assertThrows(InvalidOrderDataException.class, () -> orderService.validateOrderOrThrow(order));
    }

    @Test
    public void testValidateOrderOrThrow_Valid() {
        assertDoesNotThrow(() -> orderService.validateOrderOrThrow(order));
    }

    @Test
    public void testConstructor() {
        OrderServiceImpl service = new OrderServiceImpl(orderRepository, billService, payService, userRepository);
        assertNotNull(service);
    }

    @Test
    public void testLambdaSaveOrder1() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setIdCustomer(1L);
        dto.setTotalPrice(25.0);
        dto.setOrderDate(new Date(System.currentTimeMillis()));
        dto.setState("PENDING");

        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setName("Tipo A");
        cartItem.setColor("Blanco");
        cartItem.setQuantity(10);
        cartItem.setPrice(2.5);
        dto.setCartItem(Collections.singletonList(cartItem));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.saveOrder(dto);

        assertNotNull(result);
        assertEquals("Tipo A", result.getOrderEggs().get(0).getType());
    }

    @Test
    public void testLambdaSaveOrder2() {
        CartItemDTO item = new CartItemDTO();
        item.setName("Tipo B");
        item.setColor("Rojo");
        item.setQuantity(5);
        item.setPrice(3.0);

        OrderEgg oe = new OrderEgg();
        oe.setType(item.getName());
        oe.setColor(item.getColor());
        oe.setQuantity(item.getQuantity());
        oe.setUnitPrice(item.getPrice());
        oe.setSubtotal(item.getQuantity() * item.getPrice());

        assertEquals(15.0, oe.getSubtotal());
    }

    @Test
    public void testLambdaSaveOrder3() {
        OrderEgg oe = new OrderEgg();
        oe.setOrder(order);
        order.setOrderEggs(Collections.singletonList(oe));
        oe.setType("A");
        oe.setQuantity(1);
        oe.setUnitPrice(2.0);
        oe.setSubtotal(2.0);

        assertEquals(order, oe.getOrder());
    }

    @Test
    public void testCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(1L);
        verify(orderRepository).save(order);
        assertEquals("CANCELADA", order.getState());
    }

    @Test
    public void testProcessOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Bill bill = new Bill();
        when(billService.createBillForOrder(order)).thenReturn(bill);
        doNothing().when(payService).createPayForBill(bill, "CASH");

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.processOrder(1L, "CASH");

        assertEquals("COMPLETADA", order.getState());
    }


    @Test
    public void testValidateOrderOrThrow_InvalidOrderEggs_ThrowsException() {
        Order order = new Order();
        order.setUser(new User());
        order.setOrderEggs(Collections.emptyList());
        order.setTotalPrice(100.0);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setState("PENDING");

        InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class, () ->
                orderService.validateOrderOrThrow(order)
        );
        assertEquals("Order Eggs are not valid.", ex.getMessage());
    }

    @Test
    public void testValidateOrderOrThrow_InvalidOrderDate_ThrowsException() {
        Order order = new Order();
        order.setUser(new User());
        order.setOrderEggs(List.of(new OrderEgg()));
        order.setTotalPrice(100.0);
        order.setOrderDate(Date.valueOf(LocalDate.now().minusDays(1))); // inválido
        order.setState("PENDING");

        InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class, () ->
                orderService.validateOrderOrThrow(order)
        );
        assertEquals("Order date is not valid.", ex.getMessage());
    }

    @Test
    public void testValidateOrderOrThrow_InvalidOrderState_ThrowsException() {
        Order order = new Order();
        order.setUser(new User());
        order.setOrderEggs(List.of(new OrderEgg()));
        order.setTotalPrice(100.0);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setState(null); // inválido

        InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class, () ->
                orderService.validateOrderOrThrow(order)
        );
        assertEquals("Order state is not valid.", ex.getMessage());
    }

    @Test
    public void testCancelOrder_OrderNotFound_ThrowsException() {
        Long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                orderService.cancelOrder(orderId)
        );

        assertEquals("Order with ID 99 not found.", ex.getMessage());
    }

    @Test
    public void testProcessOrder_OrderNotFound_ThrowsException() {
        Long orderId = 100L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                orderService.processOrder(orderId, "Efectivo")
        );

        assertEquals("Order with ID 100 not found.", ex.getMessage());
    }

    @Test
    public void testSaveOrder_UserNotFound_ThrowsException() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setIdCustomer(42L); // ID inexistente

        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.saveOrder(dto)
        );

        assertEquals("Usuario no encontrado", ex.getMessage());
    }


}
