package com.goldeneggs.Bill;

import com.goldeneggs.Dto.BillDto;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link Bill} resources.
 */
@RestController
@RequestMapping("/api/v1/bills")
@CrossOrigin("*")
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * Creates and saves a new bill.
     *
     * @param bill The bill to be saved.
     * @return The saved {@link Bill} entity.
     */
    @PostMapping("/save")
    public ResponseEntity<Bill> save(@RequestBody Bill bill) {
        return ResponseEntity.ok(billService.save(bill));
    }

    /**
     * Retrieves a bill by its ID.
     *
     * @param id The ID of the bill.
     * @return The {@link Bill} with the given ID.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Bill> get(@PathVariable Long id) {
        return ResponseEntity.ok(billService.get(id));
    }

    /**
     * Retrieves all bills in the system.
     *
     * @return A list of all {@link Bill} entities.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<BillDto>> getAll() {
        return ResponseEntity.ok(billService.getAll());
    }

    /**
     * Retrieves all bills associated with a specific customer.
     *
     * @param id The ID of the customer whose bills are to be retrieved.
     * @return A {@link ResponseEntity} containing a list of {@link BillDto} objects
     *         representing the customer's bills.
     */
    @GetMapping("/byCustomer/{id}")
    public ResponseEntity<List<BillDto>> getBillsByCustomer(@PathVariable Long id) {
        List<BillDto> customerBills = billService.getBillsByCustomer(id);
        return ResponseEntity.ok(customerBills);
    }


    /**
     * Retrieves all bills associated with company users
     * (users with roles "EMPLOYEE" or "ADMIN").
     *
     * @return A {@link ResponseEntity} containing a list of {@link BillDto} objects
     *         representing company bills.
     */
    @GetMapping("/getAllOfCompany")
    public ResponseEntity<List<BillDto>> getAllCompanyBills() {
        return ResponseEntity.ok(billService.getAllBillsForCompany());
    }

    /**
     * Retrieves all bills associated with customers
     * (users with the "CUSTOMER" role).
     *
     * @return A {@link ResponseEntity} containing a list of {@link BillDto} objects
     *         representing customer bills.
     */
    @GetMapping("/getAllOfCustomers")
    public ResponseEntity<List<BillDto>> getAllCustomerBills() {
        return ResponseEntity.ok(billService.getAllBillsForCustomers());
    }

    /**
     * Retrieves the total amount of sales for the current month.
     * Only bills from customers are considered in the calculation.
     *
     * @return a {@link ResponseEntity} containing the total monthly sales asgetBestCustomer() {
    console.log(this.http.get<string>(`${this.apiUrl}/bills/bestCustomer`));
    getBestCustomer() {
      return this.http.get<any>(`${this.apiUrl}/bills/bestCustomer`);
    }
  } a {@link Double}.
     */
    @GetMapping("/monthlySalesTotal")
    public ResponseEntity<Double> getMonthlySalesTotal() {
        return ResponseEntity.ok(billService.getMonthlySalesTotal());
    }

    /**
     * Retrieves the name of the best customer of the current month,
     * based on the highest total purchase amount.
     *
     * @return a {@link ResponseEntity} containing the name of the top-spending customer.
     */
    @GetMapping("/bestCustomer")
    public ResponseEntity<String> getBestCustomerOfMonth() {
        return ResponseEntity.ok(billService.getBestCustomerOfMonth());
    }

    /**
     * Updates an existing bill with the given ID.
     *
     * @param id   The ID of the bill to update.
     * @param bill The updated bill data.
     * @return The updated {@link Bill} entity.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Bill> update(@PathVariable Long id, @RequestBody Bill bill) {
        return ResponseEntity.ok(billService.update(id, bill));
    }

    /**
     * Deletes a bill by its ID.
     *
     * @param id The ID of the bill to delete.
     * @return HTTP 200 OK if successfully deleted.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the total number of bills issued to customers in the current month.
     *
     * @return ResponseEntity containing the count of customer bills for the current month.
     */
    @GetMapping("/countThisMonth")
    public ResponseEntity<Long> countBillsInCurrentMonth() {
        Long count = billService.countCustomerBillsInCurrentMonth();
        return ResponseEntity.ok(count);
    }
}
