package com.goldeneggs.Order;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
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
import org.mockito.MockedStatic;
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

    @InjectMocks
    private OrderServiceImpl orderService;


    private User user;
    private OrderEgg orderEgg;
    private OrderEgg orderEgg2;
    private Order order;

    @BeforeEach
    void setUp() {
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
        orderEgg = OrderEgg.builder()
                .id(1L)
                .egg(egg)
                .quantity(10)
                .unitPrice(12000.0)
                .subtotal(120000.0)
                .build();

        //Create egg of order
        orderEgg2 = OrderEgg.builder()
                .id(2L)
                .egg(egg)
                .quantity(5)
                .unitPrice(12000.0)
                .subtotal(60000.0)
                .build();

        //Create order
        order = Order.builder()
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

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.updateOrder(1L, updated);
        });

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

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.deleteOrder(1L);
        });

        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveOrder_Succes() {
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.saveOrder(order);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getAllOrder_ShouldReturnListOfOrder(){
        Order order2 = Order.builder()
                .id(2L)
                .user(user)
                .orderEggs(List.of(orderEgg, orderEgg2))
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
    void testSaveOrder_InvalidUser_ShouldThrow() {
        try (MockedStatic<OrderValidator> mock = mockStatic(OrderValidator.class)) {
            mock.when(() -> OrderValidator.validateUser(any())).thenReturn(false);
            mock.when(() -> OrderValidator.validateOrderEggs(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateTotalPrice(anyDouble())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderDate(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateState(anyString())).thenReturn(true);

            OrderServiceImpl service = new OrderServiceImpl(mock(OrderRepository.class));
            Order order = crearOrderValido();

            InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class,
                    () -> service.saveOrder(order));
            assertEquals("User is not valid.", ex.getMessage());
        }
    }

    @Test
    void testSaveOrder_InvalidOrderEgg_ShouldThrow() {
        try (MockedStatic<OrderValidator> mock = mockStatic(OrderValidator.class)) {
            mock.when(() -> OrderValidator.validateUser(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderEggs(any())).thenReturn(false);
            mock.when(() -> OrderValidator.validateTotalPrice(anyDouble())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderDate(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateState(anyString())).thenReturn(true);

            OrderServiceImpl service = new OrderServiceImpl(mock(OrderRepository.class));
            Order order = crearOrderValido();

            InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class,
                    () -> service.saveOrder(order));
            assertEquals("Order Eggs are not valid.", ex.getMessage());
        }
    }

    @Test
    void testSaveOrder_InvalidTotalPrice_ShouldThrow() {
        try (MockedStatic<OrderValidator> mock = mockStatic(OrderValidator.class)) {
            mock.when(() -> OrderValidator.validateUser(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderEggs(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateTotalPrice(anyDouble())).thenReturn(false);
            mock.when(() -> OrderValidator.validateOrderDate(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateState(anyString())).thenReturn(true);

            OrderServiceImpl service = new OrderServiceImpl(mock(OrderRepository.class));
            Order order = crearOrderValido();

            InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class,
                    () -> service.saveOrder(order));
            assertEquals("Total price is not valid.", ex.getMessage());
        }
    }

    @Test
    void testSaveOrder_InvalidDate_ShouldThrow() {
        try (MockedStatic<OrderValidator> mock = mockStatic(OrderValidator.class)) {
            mock.when(() -> OrderValidator.validateUser(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderEggs(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateTotalPrice(anyDouble())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderDate(any())).thenReturn(false);
            mock.when(() -> OrderValidator.validateState(anyString())).thenReturn(true);

            OrderServiceImpl service = new OrderServiceImpl(mock(OrderRepository.class));
            Order order = crearOrderValido();

            InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class,
                    () -> service.saveOrder(order));
            assertEquals("Order date is not valid.", ex.getMessage());
        }
    }

    @Test
    void testSaveOrder_InvalidState_ShouldThrow() {
        try (MockedStatic<OrderValidator> mock = mockStatic(OrderValidator.class)) {
            mock.when(() -> OrderValidator.validateUser(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderEggs(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateTotalPrice(anyDouble())).thenReturn(true);
            mock.when(() -> OrderValidator.validateOrderDate(any())).thenReturn(true);
            mock.when(() -> OrderValidator.validateState(anyString())).thenReturn(false);

            OrderServiceImpl service = new OrderServiceImpl(mock(OrderRepository.class));
            Order order = crearOrderValido();

            InvalidOrderDataException ex = assertThrows(InvalidOrderDataException.class,
                    () -> service.saveOrder(order));
            assertEquals("Order state is not valid.", ex.getMessage());
        }
    }
    // Helper para crear orden válida
    private Order crearOrderValido() {
        Order order = new Order();
        User user = new User();
        user.setId(1L);

        OrderEgg egg = new OrderEgg();
        egg.setId(1L);

        order.setUser(user);
        order.setOrderEggs(List.of(egg));
        order.setTotalPrice(10000.0);
        order.setState("Confirmado");
        return order;
    }
}
