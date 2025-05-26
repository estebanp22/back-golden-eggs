package com.goldeneggs.Pay;

import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Role.Role;
import com.goldeneggs.User.User;
import com.goldeneggs.Bill.Bill;
import com.goldeneggs.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayServiceImplTest {

    @InjectMocks
    private PayServiceImpl payService;

    @Mock
    private PayRepository payRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillRepository billRepository;

    private Pay samplePay;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        samplePay = new Pay();
        samplePay.setId(1L);
        samplePay.setUser(user);
        samplePay.setBill(new Bill());
        samplePay.setAmountPaid(100.0);
        samplePay.setPaymentMethod("EFECTIVO");

    }

    @Test
    void testGetAll() {
        List<Pay> payList = List.of(samplePay);
        when(payRepository.findAll()).thenReturn(payList);

        List<Pay> result = payService.getAll();

        assertEquals(1, result.size());
        verify(payRepository, times(1)).findAll();
    }

    @Test
    void testGetById_Existing() {
        when(payRepository.findById(1L)).thenReturn(Optional.of(samplePay));

        Pay result = payService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(payRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payService.get(2L));
    }

    @Test
    void testSave_ValidData() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(samplePay.getBill())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateAmountPaid(samplePay.getAmountPaid())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validatePaymentMethod(samplePay.getPaymentMethod())).thenReturn(true);

            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(true);
            when(billRepository.existsById(samplePay.getBill().getId())).thenReturn(true);
            when(payRepository.save(any(Pay.class))).thenReturn(samplePay);

            Pay result = payService.save(samplePay);

            assertNotNull(result);
            verify(payRepository).save(samplePay);
        }
    }

    @Test
    void testUpdate_Existing() {
        Pay updated = new Pay();
        updated.setUser(user);
        updated.setBill(new Bill());
        updated.setAmountPaid(200.0);
        updated.setPaymentMethod("TARJETA");

        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            // Configurar validaciones
            mockedValidator.when(() -> PayValidator.validateUser(updated.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(updated.getBill())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateAmountPaid(updated.getAmountPaid())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validatePaymentMethod(updated.getPaymentMethod())).thenReturn(true);

            // Configurar repositorios
            when(payRepository.findById(1L)).thenReturn(Optional.of(samplePay));
            when(userRepository.existsById(updated.getUser().getId())).thenReturn(true);
            when(billRepository.existsById(updated.getBill().getId())).thenReturn(true);
            when(payRepository.save(any(Pay.class))).thenReturn(updated);

            Pay result = payService.update(1L, updated);

            assertEquals(200.0, result.getAmountPaid());
            verify(payRepository).save(any(Pay.class));
        }
    }

    @Test
    void testUpdate_NotFound() {
        when(payRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payService.update(1L, samplePay));
    }

    @Test
    void testDelete_Existing() {
        when(payRepository.existsById(1L)).thenReturn(true);
        doNothing().when(payRepository).deleteById(1L);

        payService.delete(1L);

        verify(payRepository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(payRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> payService.delete(2L));
    }

    @Test
    void testTotalIncome() {
        when(payRepository.sumAllByAmountPaid()).thenReturn(500.0);

        Double result = payService.totalIncome();

        assertEquals(500.0, result);
    }

    @Test
    void testTotalIncome_Null() {
        when(payRepository.sumAllByAmountPaid()).thenReturn(null);

        Double result = payService.totalIncome();

        assertEquals(0.0, result);
    }

    @Test
    void testTotalIncomeCurrentMonth() {
        when(payRepository.sumAmountPaidInCurrentMonth(any(), any())).thenReturn(300.0);

        Double result = payService.totalIncomeCurrentMonth();

        assertEquals(300.0, result);
    }

    @Test
    void testTotalIncomeCurrentMonth_Null() {
        when(payRepository.sumAmountPaidInCurrentMonth(any(), any())).thenReturn(null);

        Double result = payService.totalIncomeCurrentMonth();

        assertEquals(0.0, result);
    }

    @Test
    void testSavePay_InvalidUser_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(false);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("Invalid user", exception.getMessage());
        }
    }

    @Test
    void testSavePay_UserDoesNotExist_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(false);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("User does not exist", exception.getMessage());
        }
    }

    @Test
    void testSavePay_InvalidBill_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(samplePay.getBill())).thenReturn(false);
            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(true);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("Invalid bill", exception.getMessage());
        }
    }

    @Test
    void testSavePay_BillDoesNotExist_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(samplePay.getBill())).thenReturn(true);
            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(true);
            when(billRepository.existsById(samplePay.getBill().getId())).thenReturn(false);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("User does not exist", exception.getMessage()); // <- ¿querés cambiar este mensaje?
        }
    }

    @Test
    void testSavePay_InvalidAmountPaid_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(samplePay.getBill())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateAmountPaid(samplePay.getAmountPaid())).thenReturn(false);
            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(true);
            when(billRepository.existsById(samplePay.getBill().getId())).thenReturn(true);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("Invalid amount", exception.getMessage());
        }
    }

    @Test
    void testSavePay_InvalidPaymentMethod_ShouldThrow() {
        try (MockedStatic<PayValidator> mockedValidator = mockStatic(PayValidator.class)) {
            mockedValidator.when(() -> PayValidator.validateUser(samplePay.getUser())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateBill(samplePay.getBill())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validateAmountPaid(samplePay.getAmountPaid())).thenReturn(true);
            mockedValidator.when(() -> PayValidator.validatePaymentMethod(samplePay.getPaymentMethod())).thenReturn(false);
            when(userRepository.existsById(samplePay.getUser().getId())).thenReturn(true);
            when(billRepository.existsById(samplePay.getBill().getId())).thenReturn(true);

            InvalidPayDataException exception = assertThrows(
                    InvalidPayDataException.class,
                    () -> payService.save(samplePay)
            );
            assertEquals("Invalid payment method", exception.getMessage());
        }
    }

    @Test
    void createPayForBill_createsAndSavesPayCorrectly() {
        User user = new User();
        user.setId(5L);

        Order order = new Order();
        order.setUser(user);

        Bill bill = new Bill();
        bill.setId(10L);
        bill.setOrder(order);
        bill.setTotalPrice(150.0);

        String paymentMethod = "EFECTIVO";

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(billRepository.existsById(bill.getId())).thenReturn(true);
        doAnswer(invocation -> invocation.getArgument(0))
                .when(payRepository).save(any(Pay.class));

        payService.createPayForBill(bill, paymentMethod);

        ArgumentCaptor<Pay> payCaptor = ArgumentCaptor.forClass(Pay.class);
        verify(payRepository).save(payCaptor.capture());

        Pay savedPay = payCaptor.getValue();
        assertEquals(bill, savedPay.getBill());
        assertEquals(user, savedPay.getUser());
        assertEquals(150.0, savedPay.getAmountPaid());
        assertEquals(paymentMethod, savedPay.getPaymentMethod());
    }



}
