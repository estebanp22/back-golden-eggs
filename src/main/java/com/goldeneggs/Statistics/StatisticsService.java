package com.goldeneggs.Statistics;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Dto.Statistics.*;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Order.OrderRepository;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Egg.Egg;

import com.goldeneggs.User.UserRepository;
import com.goldeneggs.Egg.EggRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service class for calculating business statistics.
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final EggRepository eggRepository;
    private final UserRepository userRepository;

    public StatisticsResponseDto getGeneralStatistics() {
        StatisticsResponseDto response = new StatisticsResponseDto();
        StatisticsKpiDto kpis = new StatisticsKpiDto();
        StatisticsChartDto charts = new StatisticsChartDto();

        List<Bill> bills = billRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        // Filtrar solo ingresos
        List<Bill> incomeBills = bills.stream()
                .filter(b -> b.getOrder().getUser().getRoles().stream()
                        .map(Role::getName)
                        .anyMatch(roleName -> roleName.equals("CUSTOMER")))
                .toList();

        List<Order> incomeOrders = orders.stream()
                .filter(o -> o.getUser().getRoles().stream()
                        .map(Role::getName)
                        .anyMatch(roleName -> roleName.equals("CUSTOMER")))
                .toList();

        // KPI: Ventas totales
        kpis.totalSales = incomeBills.stream().mapToDouble(Bill::getTotalPrice).sum();

        // KPI: Órdenes totales
        kpis.totalOrders = incomeOrders.size();

        // KPI: Órdenes pagadas vs no pagadas
        kpis.paidOrders = (int) incomeBills.stream().filter(Bill::isPaid).count();
        kpis.unpaidOrders = incomeBills.size() - kpis.paidOrders;

        // KPI: Ticket promedio
        kpis.averageTicket = incomeOrders.isEmpty() ? 0 :
                incomeOrders.stream().mapToDouble(Order::getTotalPrice).average().orElse(0);

        // KPI: Mejor cliente
        Map<String, Double> customerSpending = new HashMap<>();
        for (Bill bill : incomeBills) {
            String name = bill.getOrder().getUser().getName();
            customerSpending.merge(name, bill.getTotalPrice(), Double::sum);
        }
        kpis.bestCustomer = customerSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // KPI: Producto más vendido
        Map<String, Long> productSales = incomeOrders.stream()
                .flatMap(order -> order.getEggs().stream())
                .map(egg -> egg.getType().getType() + " - " + egg.getColor()) // clave: tipo + color
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        kpis.mostSoldProduct = productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");


        // KPI: Orden más reciente
        kpis.lastOrderDate = incomeOrders.stream()
                .max(Comparator.comparing(Order::getOrderDate))
                .map(o -> o.getOrderDate().toString())
                .orElse("N/A");

        // KPI: Día con menos ventas
        Map<LocalDate, Double> salesPerDay = incomeBills.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.summingDouble(Bill::getTotalPrice)
                ));

        kpis.dayWithLeastSales = salesPerDay.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");

        // KPI: Órdenes canceladas
        kpis.cancelledOrders = (int) incomeOrders.stream()
                .filter(o -> o.getState().equalsIgnoreCase("cancelled"))
                .count();

        // KPI: Tiempo promedio entre orden y facturación
        kpis.averageTimeOrderToBill = incomeBills.stream()
                .mapToLong(b -> {
                    long diff = b.getIssueDate().getTime() - b.getOrder().getOrderDate().getTime();
                    return TimeUnit.MILLISECONDS.toHours(diff);
                })
                .average().orElse(0);

        // Gráficas (ejemplo: ventas por día, top clientes, etc.)
        charts.salesOverTime = salesPerDay.entrySet().stream()
                .map(e -> {
                    TimeSeriesPointDto point = new TimeSeriesPointDto();
                    point.label = e.getKey().toString();
                    point.sales = e.getValue();
                    return point;
                }).toList();

        charts.ordersByState = Arrays.asList("pending", "delivered", "cancelled").stream()
                .map(state -> {
                    DistributionDto dto = new DistributionDto();
                    dto.label = state;
                    dto.value = incomeOrders.stream().filter(o -> o.getState().equalsIgnoreCase(state)).count();
                    return dto;
                }).toList();

        charts.paidVsUnpaid = List.of(
                new DistributionDto("Pagadas", kpis.paidOrders),
                new DistributionDto("No pagadas", kpis.unpaidOrders)
        );

        charts.topCustomers = customerSpending.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DistributionDto(e.getKey(), e.getValue()))
                .toList();

        charts.topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DistributionDto(e.getKey(), e.getValue()))
                .toList();

        response.kpis = kpis;
        response.charts = charts;

        return response;
    }
}
