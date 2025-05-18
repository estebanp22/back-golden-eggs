package com.goldeneggs.Statistics;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillRepository;
import com.goldeneggs.Dto.Statistics.*;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Order.OrderRepository;
import com.goldeneggs.Role.Role;
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

    /**
     * Retrieves general statistical data related to bills and orders,
     * including key performance indicators (KPIs) and chart data.
     *
     * The method calculates statistics such as total sales, total orders,
     * paid and unpaid orders, average ticket size, best customer, most sold
     * product, last order date, day with least sales, and average time from
     * order to bill. It also generates data for several charts, including
     * orders over time, orders by state, paid vs unpaid orders, top customers,
     * and top products.
     *
     * @return a {@code StatisticsResponseDto} object containing the calculated
     *         general statistics and chart data.
     */
    public StatisticsResponseDto getGeneralStatistics() {
        StatisticsResponseDto response = new StatisticsResponseDto();
        StatisticsKpiDto kpis = new StatisticsKpiDto();
        StatisticsChartDto charts = new StatisticsChartDto();

        List<Bill> bills = billRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        if (bills == null || bills.isEmpty() || orders == null || orders.isEmpty()) {
            response.kpis = kpis;
            response.charts = charts;
            return response;
        }

        List<Bill> incomeBills = bills.stream()
                .filter(b -> b != null && b.getOrder() != null && b.getOrder().getUser() != null &&
                        b.getOrder().getUser().getRoles() != null &&
                        b.getOrder().getUser().getRoles().stream()
                                .map(Role::getName)
                                .anyMatch(role -> role.equalsIgnoreCase("CUSTOMER")))
                .toList();

        List<Order> incomeOrders = orders.stream()
                .filter(o -> o != null && o.getUser() != null &&
                        o.getUser().getRoles() != null &&
                        o.getUser().getRoles().stream()
                                .map(Role::getName)
                                .anyMatch(role -> role.equalsIgnoreCase("CUSTOMER")))
                .toList();

        kpis.totalSales = incomeBills.stream()
                .mapToDouble(b -> b != null ? b.getTotalPrice() : 0)
                .sum();

        kpis.totalOrders = incomeOrders.size();

        kpis.paidOrders = (int) incomeBills.stream()
                .filter(b -> b != null && b.isPaid())
                .count();

        kpis.unpaidOrders = incomeBills.size() - kpis.paidOrders;

        kpis.averageTicket = incomeOrders.isEmpty() ? 0 :
                incomeOrders.stream()
                        .filter(o -> o != null)
                        .mapToDouble(Order::getTotalPrice)
                        .average()
                        .orElse(0);

        Map<String, Double> customerSpending = new HashMap<>();
        for (Bill bill : incomeBills) {
            if (bill != null && bill.getOrder() != null && bill.getOrder().getUser() != null) {
                String name = bill.getOrder().getUser().getName();
                customerSpending.merge(name, bill.getTotalPrice(), Double::sum);
            }
        }

        kpis.bestCustomer = customerSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Map<String, Long> productSales = incomeOrders.stream()
                .filter(o -> o != null && o.getOrderEggs() != null)
                .flatMap(order -> order.getOrderEggs().stream())
                .filter(egg -> egg != null && egg.getEgg().getType() != null)
                .map(egg -> egg.getEgg().getType().getType() + " - " + egg.getEgg().getColor())
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        kpis.mostSoldProduct = productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        kpis.lastOrderDate = incomeOrders.stream()
                .filter(o -> o != null && o.getOrderDate() != null)
                .max(Comparator.comparing(Order::getOrderDate))
                .map(o -> o.getOrderDate().toString())
                .orElse("N/A");

        Map<LocalDate, Double> salesPerDay = incomeBills.stream()
                .filter(b -> b != null && b.getIssueDate() != null)
                .collect(Collectors.groupingBy(
                        b -> convertToLocalDate(b.getIssueDate()),
                        Collectors.summingDouble(Bill::getTotalPrice)
                ));

        kpis.dayWithLeastSales = salesPerDay.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");

        kpis.cancelledOrders = (int) incomeOrders.stream()
                .filter(o -> o != null && "cancelled".equalsIgnoreCase(o.getState()))
                .count();

        kpis.averageTimeOrderToBill = incomeBills.stream()
                .filter(b -> b != null && b.getOrder() != null &&
                        b.getIssueDate() != null && b.getOrder().getOrderDate() != null)
                .mapToLong(b -> {
                    long diff = b.getIssueDate().getTime() - b.getOrder().getOrderDate().getTime();
                    return TimeUnit.MILLISECONDS.toHours(diff);
                })
                .average().orElse(0);

        charts.ordersOverTime = incomeOrders.stream()
                .filter(o -> o != null && o.getOrderDate() != null)
                .collect(Collectors.groupingBy(
                        o -> convertToLocalDate(o.getOrderDate()),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> {
                    TimeSeriesPointDto dto = new TimeSeriesPointDto();
                    dto.name = e.getKey().toString();
                    dto.orders = e.getValue().intValue();
                    return dto;
                }).toList();

        charts.ordersByState = Arrays.asList("pending", "delivered", "cancelled").stream()
                .map(state -> {
                    DistributionDto dto = new DistributionDto();
                    dto.name = state;
                    dto.value = incomeOrders.stream()
                            .filter(o -> o != null && state.equalsIgnoreCase(o.getState()))
                            .count();
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

    /**
     * Converts a java.util.Date, java.sql.Date, or java.sql.Timestamp to LocalDate.
     */
    private LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        } else if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        } else if (date != null) {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

}
