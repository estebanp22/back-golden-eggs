package com.goldeneggs.Bill;

import com.goldeneggs.Dto.BillDto;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidBillDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.User.User;
import com.goldeneggs.Order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private Role role;
    private User user;
    private OrderEgg orderEgg;
    private OrderEgg orderEgg2;
    private Order order;
    private Bill bill;

    @BeforeEach
    void setUp(){
        //Create test role
        role = new Role();
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
                .quiantity(10)
                .unitPrice(12000.0)
                .subtotal(120000.0)
                .build();

        //Create egg of order
        orderEgg2 = OrderEgg.builder()
                .id(2L)
                .egg(egg)
                .quiantity(5)
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

        //Create bill to taste
        bill = Bill.builder()
                .id(1L)
                .order(order)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(order.getTotalPrice())
                .paid(true)
                .build();
    }

    @Test
    void getAllBills_ShouldReturnListOfBills(){
        //User bill to the setUp
        Bill bill2 = Bill.builder()
                .id(2L)
                .order(order)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(order.getTotalPrice())
                .paid(false)
                .build();

        when(billRepository.findAll()).thenReturn(Arrays.asList(bill2,bill));

        List<BillDto> result = billService.getAll();

        assertEquals(2, result.size());

        BillDto dto1 = result.get(1);
        assertEquals(bill.getId(), dto1.getId());
        assertEquals(user.getName(), dto1.getCustomerName());
        assertEquals(order.getOrderDate().toString(), dto1.getOrderDate());
        assertEquals(order.getState(), dto1.getOrderState());
        assertEquals(order.getTotalPrice(), dto1.getTotalPrice());
        assertEquals(bill.isPaid(), dto1.isPaid());

        verify(billRepository).findAll();
    }

    @Test
    void getBillsByCustomer_ShouldReturnBillsForSpecificCustomer(){
        //Create other user
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Ana");
        otherUser.setEmail("ana@example.com");
        otherUser.setUsername("ana456");
        otherUser.setAddress("Otra calle");
        otherUser.setPassword("pass");
        otherUser.setPhoneNumber("7654321");
        otherUser.setRoles(Collections.singletonList(role));
        otherUser.setEnabled(true);

        // Crear una orden para el otro usuario
        Order otherOrder = Order.builder()
                .id(2L)
                .user(otherUser)
                .orderEggs(Collections.emptyList())
                .totalPrice(50000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
                .state("PENDIENTE")
                .build();

        // Crear una factura para el otro usuario
        Bill otherBill = Bill.builder()
                .id(2L)
                .order(otherOrder)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(50000.0)
                .paid(false)
                .build();

        when(billRepository.findAll()).thenReturn(Arrays.asList(bill, otherBill));

        List<BillDto> result = billService.getBillsByCustomer(user.getId());

        assertEquals(1, result.size());
        BillDto dto = result.get(0);

        assertEquals(bill.getId(), dto.getId());
        assertEquals(user.getName(), dto.getCustomerName());
        assertEquals(order.getOrderDate().toString(), dto.getOrderDate());
        assertEquals(order.getState(), dto.getOrderState());
        assertEquals(order.getTotalPrice(), dto.getTotalPrice());
        assertEquals(bill.isPaid(), dto.isPaid());

        verify(billRepository).findAll();
    }

    @Test
    void getAllBillsForCompany_ShouldReturnOnlyCompanyBills(){
        Role employeeRole = new Role();
        employeeRole.setId(2L);
        employeeRole.setName("EMPLOYEE");

        User employedUser = new User();
        employedUser.setId(2L);
        employedUser.setName("felipe123");
        employedUser.setRoles(Collections.singletonList(employeeRole));

        Order companyOrder = Order.builder()
                .id(2L)
                .user(employedUser)
                .orderEggs(List.of(orderEgg, orderEgg2))
                .totalPrice(180000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
                .state("ENVIADO")
                .build();

        Bill companyBill = Bill.builder()
                .id(2L)
                .order(companyOrder)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(order.getTotalPrice())
                .paid(true)
                .build();

        when(billRepository.findAll()).thenReturn(List.of(bill,companyBill));

        List<BillDto> result = billService.getAllBillsForCompany();

        assertEquals(1, result.size());
        assertEquals("felipe123", result.get(0).getCustomerName());
    }

    @Test
    void getAllBillsForCustomer_ShouldReturnOnlyCustomerBills(){
        when(billRepository.findAll()).thenReturn(List.of(bill));

        List<BillDto> result = billService.getAllBillsForCustomers();

        assertEquals(1, result.size());
        assertEquals(user.getName(), result.get(0).getCustomerName());
    }

    @Test
    void getMonthlySalesTotal_ShouldReturnTotalForCurrentMonth() {
        LocalDate testDate = LocalDate.now();
        bill.setIssueDate(java.sql.Date.valueOf(testDate));

        when(billRepository.findAll()).thenReturn(List.of(bill));

        Double result = billService.getMonthlySalesTotal();

        assertEquals(bill.getTotalPrice(), result);
    }

    @Test
    void getMonthlySalesTotal_ShouldIgnoreNOnCustomerOrOldBills(){
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        bill.setIssueDate(java.sql.Date.valueOf(lastMonthDate));

        Role employeeRole = new Role();
        employeeRole.setId(2L);
        employeeRole.setName("EMPLOYEE");
        user.setRoles(List.of(employeeRole));
        bill.setOrder(order);

        when(billRepository.findAll()).thenReturn(List.of(bill));

        Double result = billService.getMonthlySalesTotal();

        assertEquals(0.0, result);
    }

    @Test
    void getBestCustomerOfMonth_ShouldReturnTopSpendingCustomer(){
        when(billRepository.findAll()).thenReturn(List.of(bill));

        String bestCustomer = billService.getBestCustomerOfMonth();

        assertEquals("Felipe", bestCustomer);
        verify(billRepository).findAll();
    }

    @Test
    void getBestCustomerOfMonth_ShouldIgnoreUsersWithoutCustomerRole() {
        // Create user with role ADMIN
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setId(99L);
        adminUser.setName("Admin");
        adminUser.setRoles(List.of(adminRole));

        Order adminOrder = Order.builder()
                .id(10L)
                .user(adminUser)
                .totalPrice(999999.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
                .state("PROCESADO")
                .build();

        Bill adminBill = Bill.builder()
                .id(10L)
                .order(adminOrder)
                .issueDate(new java.sql.Date(System.currentTimeMillis())) // del mes actual
                .totalPrice(adminOrder.getTotalPrice())
                .paid(true)
                .build();

        when(billRepository.findAll()).thenReturn(List.of(adminBill));

        String result = billService.getBestCustomerOfMonth();

        assertEquals("Sin compras este mes", result);
    }

    @Test
    void getBestCustomerOfMonth_ShouldIgnoreBillsFromPreviousMonth() {
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);

        Bill oldBill = Bill.builder()
                .id(20L)
                .order(order)
                .issueDate(java.sql.Date.valueOf(lastMonthDate))
                .totalPrice(500000.0)
                .paid(true)
                .build();

        when(billRepository.findAll()).thenReturn(List.of(oldBill));

        String result = billService.getBestCustomerOfMonth();

        assertEquals("Sin compras este mes", result);
    }

    @Test
    void getBillById_existingBill_returnsBill() {
        Bill billAux = new Bill();
        billAux.setId(5L);

        when(billRepository.findById(5L)).thenReturn(Optional.of(billAux));

        Bill found = billService.get(5L);

        assertEquals(billAux, found);
    }

    @Test
    void getBillById_notFound_throwsException() {
        when(billRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> billService.get(99L));

        assertTrue(ex.getMessage().contains("Bill not found with ID:"));
    }

    @Test
    void saveBill_Succes(){
        when(billRepository.save(bill)).thenReturn(bill);

        Bill result = billService.save(bill);

        assertNotNull(result);
        assertEquals(bill.getId(), result.getId());
        verify(billRepository, times(1)).save(bill);
    }


    @Test
    void update_ShouldUpdateBillSuccessfully() {
        // Simular bill existente
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        // Validación pasa
        // Simular guardado
        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        Bill updated = Bill.builder()
                .id(1L)
                .order(order)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(99999.0)
                .paid(false)
                .build();

        Bill result = billService.update(1L, updated);

        assertEquals(updated.getTotalPrice(), result.getTotalPrice());
        assertFalse(result.isPaid());
        verify(billRepository).findById(1L);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void update_ShouldThrowException_WhenBillNotFound() {
        when(billRepository.findById(1L)).thenReturn(Optional.empty());

        Bill updated = new Bill();

        assertThrows(ResourceNotFoundException.class, () -> {
            billService.update(1L, updated);
        });

        verify(billRepository).findById(1L);
    }

    @Test
    void update_ShouldThrowException_WhenValidationTotalPriceFails() {
        // Retorna bill existente
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        // Bill con datos inválidos (ej: totalPrice negativo)
        Bill invalid = Bill.builder()
                .id(1L)
                .order(order)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(-1000.0)
                .paid(true)
                .build();

        assertThrows(InvalidBillDataException.class, () -> {
            billService.update(1L, invalid);
        });
    }

    @Test
    void update_ShouldThrowException_WhenValidationIssueDateFails() {
        // Retorna bill existente
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        // Bill con datos inválidos (ej: issueDate null)
        Bill invalid = Bill.builder()
                .id(1L)
                .order(order)
                .issueDate(null)
                .totalPrice(1000.0)
                .paid(true)
                .build();

        assertThrows(InvalidBillDataException.class, () -> {
            billService.update(1L, invalid);
        });
    }
    @Test
    void update_ShouldThrowException_WhenValidationOrderFails() {
        // Retorna bill existente
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        // Bill con datos inválidos (ej: Order null)
        Bill invalid = Bill.builder()
                .id(1L)
                .order(null)
                .issueDate(new java.sql.Date(System.currentTimeMillis()))
                .totalPrice(1000.0)
                .paid(true)
                .build();

        assertThrows(InvalidBillDataException.class, () -> {
            billService.update(1L, invalid);
        });
    }

    @Test
    void delete_ShouldDeleteBill_WhenExists() {
        when(billRepository.existsById(1L)).thenReturn(true);

        billService.delete(1L);

        verify(billRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenBillNotFound() {
        when(billRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            billService.delete(1L);
        });

        verify(billRepository, never()).deleteById(anyLong());
    }

    @Test
    void countCustomerBillsInCurrentMonth_ShouldReturnCount() {
        when(billRepository.countCustomerBillsInCurrentMonth(any(Date.class), any(Date.class)))
                .thenReturn(5L);

        Long result = billService.countCustomerBillsInCurrentMonth();

        assertEquals(5L, result);
    }
    @Test
    void countCustomerBillsInCurrentMonth_ShouldReturnZero_WhenNull() {
        when(billRepository.countCustomerBillsInCurrentMonth(any(Date.class), any(Date.class)))
                .thenReturn(null);

        Long result = billService.countCustomerBillsInCurrentMonth();

        assertEquals(0L, result);
    }
}
