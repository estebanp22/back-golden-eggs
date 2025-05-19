package com.goldeneggs.Statistics;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Dto.Statistics.StatisticsResponseDto;
import com.goldeneggs.Egg.EggRepository;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Order.OrderRepository;
import com.goldeneggs.Role.Role;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    BillRepository billRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    EggRepository eggRepository;
    @Mock
    UserRepository userRepository;

    StatisticsService service;

    @BeforeEach
    void setUp() {
        service = new StatisticsService(billRepository, orderRepository, eggRepository, userRepository);
    }

    @Test
    void testGetGeneralStatistics_emptyData() {
        when(billRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        StatisticsResponseDto result = service.getGeneralStatistics();

        assertNotNull(result);
        assertNotNull(result.kpis);
        assertNotNull(result.charts);
        assertEquals(0, result.kpis.totalSales);
        assertEquals(0, result.kpis.totalOrders);
        // Otros KPIs básicos en 0 o "N/A"
    }

    @Test
    void testGetGeneralStatistics_withData() {
        // Crear usuarios, roles, órdenes, facturas y productos simulados

        Role customerRole = new Role();
        customerRole.setName("CUSTOMER");

        User user = new User();
        user.setName("Juan");
        user.setRoles(List.of(customerRole));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(100);
        order.setOrderDate(new Date());
        order.setState("delivered");

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setPaid(true);
        bill.setTotalPrice(100);
        bill.setIssueDate(new Date());

        when(billRepository.findAll()).thenReturn(List.of(bill));
        when(orderRepository.findAll()).thenReturn(List.of(order));

        StatisticsResponseDto result = service.getGeneralStatistics();

        assertNotNull(result);
        assertTrue(result.kpis.totalSales > 0);
        assertEquals(1, result.kpis.totalOrders);
        assertEquals(1, result.kpis.paidOrders);
        assertEquals(0, result.kpis.unpaidOrders);
        assertEquals("Juan", result.kpis.bestCustomer);
        assertEquals("delivered", result.charts.ordersByState.stream()
                .filter(d -> d.name.equals("delivered"))
                .findFirst()
                .map(d -> "delivered")
                .orElse(""));

        // Puedes agregar más asserts si quieres
    }
}

