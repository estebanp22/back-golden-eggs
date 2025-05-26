package com.goldeneggs.Bill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Dto.BillDto;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidBillDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Order.Order;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BillControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillService billService;

    @InjectMocks
    private BillController billController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Bill bill;
    private BillDto billDto;

    @BeforeEach
    void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(billController).build();

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
                .expirationDate(new java.sql.Date(System.currentTimeMillis()))
                .supplier(supplier)
                .avibleQuantity(1000)
                .movements(Collections.emptyList())
                .build();

        //Create egg of order
        OrderEgg orderEgg = OrderEgg.builder()
                .id(1L)
                .type(egg.getType().getType())
                .quantity(10)
                .unitPrice(12000.0)
                .subtotal(120000.0)
                .build();

        //Create egg of order
        OrderEgg orderEgg2 = OrderEgg.builder()
                .id(2L)
                .type(egg.getType().getType())
                .quantity(5)
                .unitPrice(12000.0)
                .subtotal(60000.0)
                .build();
        //Create order
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .orderEggs(List.of(orderEgg, orderEgg2))
                .totalPrice(180000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))
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

        billDto = new BillDto(
                1L,
                "2025-05-19",
                true,
                150.0,
                "Juan Pérez",
                "2025-05-18",
                "DELIVERED"
        );
    }

    @Test
    void testSaveBill_Success() throws Exception {
        when(billService.save(any(Bill.class))).thenReturn(bill);

        mockMvc.perform(post("/api/v1/bills/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bill)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.totalPrice").value(bill.getTotalPrice()))
                .andExpect(jsonPath("$.paid").value(bill.isPaid()));
    }

    @Test
    void testSaveBill_InvalidData() throws Exception {
        when(billService.save(any(Bill.class)))
                .thenThrow(new InvalidBillDataException("Datos inválidos"));

        mockMvc.perform(post("/api/v1/bills/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bill)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Datos inválidos"));
    }

    @Test
    void testGetBestCustomerOfMonth() throws Exception {
        when(billService.getBestCustomerOfMonth()).thenReturn(user.getName());

        mockMvc.perform(get("/api/v1/bills/bestCustomer"))
                .andExpect(status().isOk())
                .andExpect(content().string(user.getName()));
    }

    @Test
    void testGetMonthlySalesTotal() throws Exception {
        when(billService.getMonthlySalesTotal()).thenReturn(bill.getTotalPrice());

        mockMvc.perform(get("/api/v1/bills/monthlySalesTotal"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(bill.getTotalPrice())));
    }

    @Test
    void testGetAllCustomerBills() throws Exception {
        when(billService.getAllBillsForCustomers()).thenReturn(List.of(billDto));

        mockMvc.perform(get("/api/v1/bills/getAllOfCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(billDto.getId()))
                .andExpect(jsonPath("$[0].issueDate").value(billDto.getIssueDate()))
                .andExpect(jsonPath("$[0].paid").value(billDto.isPaid()))
                .andExpect(jsonPath("$[0].totalPrice").value(billDto.getTotalPrice()))
                .andExpect(jsonPath("$[0].customerName").value(billDto.getCustomerName()))
                .andExpect(jsonPath("$[0].orderDate").value(billDto.getOrderDate()))
                .andExpect(jsonPath("$[0].orderState").value(billDto.getOrderState()));
    }

    @Test
    void testGetAllCompanyBills() throws Exception {
        when(billService.getAllBillsForCompany()).thenReturn(List.of(billDto));

        mockMvc.perform(get("/api/v1/bills/getAllOfCompany"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(billDto.getId()))
                .andExpect(jsonPath("$[0].issueDate").value(billDto.getIssueDate()))
                .andExpect(jsonPath("$[0].paid").value(billDto.isPaid()))
                .andExpect(jsonPath("$[0].totalPrice").value(billDto.getTotalPrice()))
                .andExpect(jsonPath("$[0].customerName").value(billDto.getCustomerName()))
                .andExpect(jsonPath("$[0].orderDate").value(billDto.getOrderDate()))
                .andExpect(jsonPath("$[0].orderState").value(billDto.getOrderState()));
    }

    @Test
    void testGetAll() throws Exception {
        when(billService.getAll()).thenReturn(List.of(billDto));

        mockMvc.perform(get("/api/v1/bills/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(billDto.getId()));
    }

    @Test
    void testCountBillsInCurrentMonth() throws Exception {
        long expectedCount = 5L; // el número que quieras que devuelva el mock

        when(billService.countCustomerBillsInCurrentMonth()).thenReturn(expectedCount);

        mockMvc.perform(get("/api/v1/bills/countThisMonth"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));
    }

    @Test
    void testUpdateBill_Success() throws Exception {
        Long billId = 1L;

        when(billService.update(eq(billId), any(Bill.class))).thenReturn(bill);

        mockMvc.perform(put("/api/v1/bills/update/{id}", billId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.totalPrice").value(bill.getTotalPrice()))
                .andExpect(jsonPath("$.paid").value(bill.isPaid()));
    }

    @Test
    void testUpdateBill_NotFound() throws Exception {
        Long billId = 1L;

        when(billService.update(eq(billId), any(Bill.class)))
                .thenThrow(new ResourceNotFoundException("Bill not found"));

        mockMvc.perform(put("/api/v1/bills/update/{id}", billId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bill)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBill_InvalidData() throws Exception {
        Long billId = 1L;

        when(billService.update(eq(billId), any(Bill.class)))
                .thenThrow(new InvalidBillDataException("Invalid bill data"));

        mockMvc.perform(put("/api/v1/bills/update/{id}", billId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bill)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid bill data"));
    }

    @Test
    void testDeleteBill_Success() throws Exception {
        Long billId = 1L;

        doNothing().when(billService).delete(billId);

        mockMvc.perform(delete("/api/v1/bills/delete/{id}", billId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBill_NotFound() throws Exception {
        Long billId = 1L;

        doThrow(new ResourceNotFoundException("Bill not found")).when(billService).delete(billId);

        mockMvc.perform(delete("/api/v1/bills/delete/{id}", billId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBillsByCustomer_Success() throws Exception {
        Long customerId = 1L;

        when(billService.getBillsByCustomer(customerId)).thenReturn(List.of(billDto));

        mockMvc.perform(get("/api/v1/bills/byCustomer/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(billDto.getId()))
                .andExpect(jsonPath("$[0].customerName").value(billDto.getCustomerName()));
    }

    @Test
    void testGetBillsByCustomer_NotFound() throws Exception {
        Long customerId = 1L;

        when(billService.getBillsByCustomer(customerId)).thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/bills/byCustomer/{id}", customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBillById_Success() throws Exception {
        Long billId = 1L;

        when(billService.get(billId)).thenReturn(bill);

        mockMvc.perform(get("/api/v1/bills/get/{id}", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.totalPrice").value(bill.getTotalPrice()))
                .andExpect(jsonPath("$.paid").value(bill.isPaid()));
    }

    @Test
    void testGetBillById_NotFound() throws Exception {
        Long billId = 1L;

        when(billService.get(billId)).thenThrow(new ResourceNotFoundException("Bill not found"));

        mockMvc.perform(get("/api/v1/bills/get/{id}", billId))
                .andExpect(status().isNotFound());
    }

}
