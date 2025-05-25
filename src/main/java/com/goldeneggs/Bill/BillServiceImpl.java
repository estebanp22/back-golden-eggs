package com.goldeneggs.Bill;
import com.goldeneggs.Dto.BillDto;
import com.goldeneggs.Exception.InvalidBillDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.sql.Date;
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

                    // Conversión segura de java.sql.Date a LocalDate
                    LocalDate issueLocalDate = bill.getIssueDate().toLocalDate();

                    return roles.contains("CUSTOMER") &&
                            YearMonth.from(issueLocalDate).equals(currentMonth);
                })
                .mapToDouble(Bill::getTotalPrice)
                .sum();
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

                    // Conversión segura de java.sql.Date a LocalDate
                    LocalDate issueLocalDate = bill.getIssueDate().toLocalDate();

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
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
    }

    /**
     * Saves a new bill in the repository.
     *
     * @param bill the bill to save.
     * @return InvalidBillDataException if the bill data is invalid
     */
    @Override
    public Bill save(Bill bill) {
        validateBillOrThrow(bill);
        return billRepository.save(bill);
    }

    /**
     * Updates an existing bill.
     *
     * @param id the ID of the bill to update.
     * @param updatedBill the updated bill data.
     * @return the updated {@link Bill}.
     * @throws InvalidBillDataException if the updated bill data is invalid
     */
    @Override
    public Bill update(Long id, Bill updatedBill) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + id + " not found"));

        validateBillOrThrow(updatedBill);

        existing.setOrder(updatedBill.getOrder());
        existing.setIssueDate(updatedBill.getIssueDate());
        existing.setTotalPrice(updatedBill.getTotalPrice());
        existing.setPaid(updatedBill.isPaid());

        return  billRepository.save(existing);
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

        // Primer día del mes (a medianoche)
        java.sql.Date start = java.sql.Date.valueOf(now.withDayOfMonth(1));

        // Primer día del siguiente mes (a medianoche)
        java.sql.Date end = java.sql.Date.valueOf(now.plusMonths(1).withDayOfMonth(1));

        Long count = billRepository.countCustomerBillsInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

    /**
     * Validates the provided bill object and throws an exception if any validation fails.
     *
     * @param bill the Bill object to be validated. It must have a valid order, issue date,
     *             and total price, otherwise an InvalidBillDataException is thrown.
     * @throws InvalidBillDataException if the order, issue date, or total price of the bill is invalid.
     */
    private void validateBillOrThrow(Bill bill) {
        if (!BillValidator.validateOrder(bill.getOrder())) {
            throw new InvalidBillDataException("Order is not valid");
        }

        if (!BillValidator.validateIssueDate(bill.getIssueDate())) {
            throw new  InvalidBillDataException("Issue date is not valid");
        }

        if (!BillValidator.validateTotalPrice(bill.getTotalPrice())) {
            throw new  InvalidBillDataException("Total price is not valid");
        }
    }

    /**
     * Creates a bill for the provided order, sets its attributes, and saves it to the repository.
     *
     * @param order the order for which the bill will be created
     */
    @Override
    public Bill createBillForOrder(Order order) {
        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setIssueDate(Date.valueOf(LocalDate.now()));
        bill.setTotalPrice(order.getTotalPrice());
        bill.setPaid(true);
        return billRepository.save(bill);
    }


}