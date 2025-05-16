package com.goldeneggs.Bill;
import com.goldeneggs.Dto.BillDto;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Bill} entities.
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    /**
     * Retrieves all bills from the repository.
     *
     * @return a list of all {@link Bill} entities.
     */
    @Override
    public List<BillDto> getAll() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream().map(bill -> {
            Order order = bill.getOrder();
            return new BillDto(
                    bill.getId(),
                    bill.getIssueDate().toString(),            // issueDate
                    bill.isPaid(),                             // paid
                    bill.getTotalPrice(),                      // totalPrice
                    order.getUser().getName(),                 // customerName
                    order.getOrderDate().toString(),           // orderDate
                    order.getState()                           // orderState
            );
        }).collect(Collectors.toList());
    }
    /**
     * Retrieves a list of bills for a specific customer.
     *
     * @param customerId the unique identifier of the customer whose bills need to be retrieved
     * @return a list of BillDto objects representing the bills of the specified customer
     */
    @Override
    public List<BillDto> getBillsByCustomer(Long customerId) {
        return billRepository.findAll().stream()
                .filter(bill -> {
                    Order order = bill.getOrder();
                    return order.getUser().getId().equals(customerId);
                })
                .map(bill -> {
                    Order order = bill.getOrder();
                    return new BillDto(
                            bill.getId(),
                            bill.getIssueDate().toString(),
                            bill.isPaid(),
                            bill.getTotalPrice(),
                            order.getUser().getName(),
                            order.getOrderDate().toString(),
                            order.getState()
                    );
                })
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all bills that are associated with company users,
     * specifically those with roles "EMPLOYEE" or "ADMIN".
     *
     * @return a list of {@link BillDto} objects representing the bills for company users.
     */
    @Override
    public List<BillDto> getAllBillsForCompany() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .filter(bill -> {
                    Order order = bill.getOrder();
                    List<String> roles = order.getUser().getRoles().stream()
                            .map(Role::getName)
                            .toList();

                    return roles.contains("EMPLOYEE") || roles.contains("ADMIN");
                })
                .map(bill -> {
                    Order order = bill.getOrder();
                    return new BillDto(
                            bill.getId(),
                            bill.getIssueDate().toString(),
                            bill.isPaid(),
                            bill.getTotalPrice(),
                            order.getUser().getName(),
                            order.getOrderDate().toString(),
                            order.getState()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bills that are associated with customers,
     * specifically users with the "CUSTOMER" role.
     *
     * @return a list of {@link BillDto} objects representing the bills for customer users.
     */
    @Override
    public List<BillDto> getAllBillsForCustomers() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .filter(bill -> {
                    Order order = bill.getOrder();
                    List<String> roles = order.getUser().getRoles().stream()
                            .map(Role::getName)
                            .toList();

                    return roles.contains("CUSTOMER");
                })
                .map(bill -> {
                    Order order = bill.getOrder();
                    return new BillDto(
                            bill.getId(),
                            bill.getIssueDate().toString(),
                            bill.isPaid(),
                            bill.getTotalPrice(),
                            order.getUser().getName(),
                            order.getOrderDate().toString(),
                            order.getState()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total sales amount for the current month,
     * considering only bills associated with users who have the "CUSTOMER" role.
     *
     * @return the total amount of sales for the current month as a {@link Double}.
     */
    @Override
    public Double getMonthlySalesTotal() {
        YearMonth currentMonth = YearMonth.now();

        return billRepository.findAll().stream()
                .filter(bill -> {
                    Order order = bill.getOrder();
                    List<String> roles = order.getUser().getRoles().stream()
                            .map(Role::getName)
                            .toList();

                    LocalDate issueLocalDate = bill.getIssueDate()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    return roles.contains("CUSTOMER") &&
                            YearMonth.from(issueLocalDate).equals(currentMonth);
                })
                .map(Bill::getTotalPrice)
                .reduce(0.0, Double::sum);
    }

    /**
     * Identifies the customer who has spent the most during the current month.
     * It calculates the total amount spent by each customer and returns the name
     * of the one with the highest total.
     *
     * @return the name of the top-spending customer of the current month.
     *         Returns "Sin compras este mes" if no purchases were made.
     */
    @Override
    public String getBestCustomerOfMonth() {
        YearMonth currentMonth = YearMonth.now();

        Map<String, Double> spendingByCustomer = billRepository.findAll().stream()
                .filter(bill -> {
                    Order order = bill.getOrder();
                    List<String> roles = order.getUser().getRoles().stream()
                            .map(Role::getName)
                            .toList();

                    LocalDate issueLocalDate = bill.getIssueDate()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    return roles.contains("CUSTOMER") &&
                            YearMonth.from(issueLocalDate).equals(currentMonth);
                })
                .collect(Collectors.groupingBy(
                        bill -> bill.getOrder().getUser().getName(),
                        Collectors.summingDouble(Bill::getTotalPrice)
                ));

        return spendingByCustomer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin compras este mes");
    }


    /**
     * Retrieves a bill by its ID.
     *
     * @param id the ID of the bill to retrieve.
     * @return the {@link Bill} with the given ID.
     * @throws ResourceNotFoundException if no bill is found with the given ID.
     */
    @Override
    public Bill get(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + id + " not found"));
    }

    /**
     * Saves a new bill in the repository.
     *
     * @param bill the bill to save.
     * @return the saved {@link Bill}.
     */
    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Updates an existing bill.
     *
     * @param id the ID of the bill to update.
     * @param updatedBill the updated bill data.
     * @return the updated {@link Bill}.
     * @throws ResourceNotFoundException if the bill does not exist.
     */
    @Override
    public Bill update(Long id, Bill updatedBill) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + id + " not found"));

        existing.setOrder(updatedBill.getOrder());
        existing.setIssueDate(updatedBill.getIssueDate());
        existing.setTotalPrice(updatedBill.getTotalPrice());
        existing.setPaid(updatedBill.isPaid());

        return billRepository.save(existing);
    }

    /**
     * Deletes a bill by its ID.
     *
     * @param id the ID of the bill to delete.
     * @throws ResourceNotFoundException if the bill does not exist.
     */
    @Override
    public void delete(Long id) {
        if (!billRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bill with ID " + id + " not found");
        }
        billRepository.deleteById(id);
    }

    /**
     * Retrieves the number of bills issued in the current month.
     *
     * This method calculates the start and end of the current month using LocalDate,
     * converts them to java.util.Date for JPA compatibility, and queries the repository
     * for the total count of bills within this date range.
     *
     * @return The total number of bills issued in the current month. Returns 0 if no bills are found.
     */
    @Override
    public Long countCustomerBillsInCurrentMonth() {
        LocalDate now = LocalDate.now();

        Date start = Date.from(
                now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Date end = Date.from(
                now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        Long count = billRepository.countCustomerBillsInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

}