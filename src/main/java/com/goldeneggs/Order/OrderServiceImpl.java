package com.goldeneggs.Order;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillService;
import com.goldeneggs.Dto.Order.OrderDTO;
import com.goldeneggs.Dto.Order.OrderItemDTO;
import com.goldeneggs.Dto.Order.OrderRequestDTO;
import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Pay.PayService;
import com.goldeneggs.User.User;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface.
 * <p>
 * This service provides the business logic for managing orders. It interacts with
 * the {@link OrderRepository} to perform CRUD operations on orders.
 * </p>
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final BillService billService;

    private final PayService payService;

    /**
     * Constructs an instance of the OrderServiceImpl class.
     *
     * @param orderRepository The repository used to perform CRUD operations on orders.
     * @param billService The service responsible for generating bills for orders.
     * @param payService The service responsible for processing payments for orders.
     * @param userRepository The repository used to manage and retrieve user data.
     */
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BillService billService, PayService payService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.billService = billService;
        this.payService = payService;
    }


    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     */
    @Override
    public List<Order> getAllOrders() {return orderRepository.findAll();}

    /**
     * Finds an order by its ID.
     *
     * @param id The ID of the order to find.
     * @return The order if found, otherwise throws a {@link ResourceNotFoundException}.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order not found with ID: " + id)
        );
    }

    /**
     * Counts the total number of orders placed by a specific customer.
     *
     * @param customerId The ID of the customer whose orders are to be counted.
     * @return The total number of orders placed by the specified customer.
     */
    @Override
    public Long countOrdersByCustomerId(Long customerId) {
        return orderRepository.countOrdersByCustomerId(customerId);
    }


    /**
     * Saves a new or existing order.
     *
     * @param dto The order to save.
     * @return The saved order or throws error
     */
    @Override
    public Order saveOrder(OrderRequestDTO dto) {
        User user = userRepository.findById(dto.getIdCustomer())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<OrderEgg> orderEggs = dto.getCartItem().stream().map(item -> {
            OrderEgg oe = new OrderEgg();
            oe.setType(item.getName());
            oe.setColor(item.getColor());
            oe.setQuantity(item.getQuantity());
            oe.setUnitPrice(item.getPrice());
            oe.setSubtotal(item.getQuantity() * item.getPrice());
            return oe;
        }).collect(Collectors.toList());

        Order order = new Order();
        order.setUser(user);
        order.setOrderEggs(orderEggs);
        order.setTotalPrice(dto.getTotalPrice());
        order.setOrderDate(dto.getOrderDate());
        order.setState(dto.getState());

        orderEggs.forEach(oe -> oe.setOrder(order)); // establecer relación inversa

        return orderRepository.save(order);
    }


    @Override
    public Order updateOrder(Long id, Order order) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found."));

        validateOrderOrThrow(existing);

        existing.setUser(order.getUser());
        existing.setOrderEggs(order.getOrderEggs());
        existing.setTotalPrice(order.getTotalPrice());
        existing.setOrderDate(order.getOrderDate());
        existing.setState(order.getState());
        return orderRepository.save(existing);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order with ID " + id + " not found.")
        );
        orderRepository.delete(order);
    }

    /**
     * Retrieves a list of orders placed within the current month.
     *
     * @return A list of {@link Order} instances representing the orders placed
     *         between the start and end dates of the current month.
     */
    @Override
    public List<Order> getOrdersInCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return orderRepository.findOrdersInCurrentMonth(start, end);
    }

    /**
     * Counts the number of orders placed within the current month.
     * This method calculates the start and end dates of the current month
     * and queries the order repository to determine the total count of orders
     * placed during this period.
     *
     * @return The total count of orders placed in the current month. Returns 0 if no orders are found.
     */
    @Override
    public Long countOrdersInCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Long count = orderRepository.countOrdersInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

    public void validateOrderOrThrow(Order order) {
        if(!OrderValidator.validateUser(order.getUser())){
            throw new InvalidOrderDataException("User is not valid.");
        }
        if(!OrderValidator.validateOrderEggs(order.getOrderEggs())){
            throw new InvalidOrderDataException("Order Eggs are not valid.");
        }
        if(!OrderValidator.validateTotalPrice(order.getTotalPrice())){
            throw new InvalidOrderDataException("Total price is not valid.");
        }
        if(!OrderValidator.validateOrderDate(order.getOrderDate())){
            throw new InvalidOrderDataException("Order date is not valid.");
        }
        if(!OrderValidator.validateState(order.getState())){
            throw new InvalidOrderDataException("Order state is not valid.");
        }
    }

    /**
     * Retrieves all orders and maps them to OrderDTOs.
     *
     * @return list of OrderDTOs
     */
    @Override
    public List<OrderDTO> getAllAsDTO() {
        return orderRepository.findAll().stream().map(order ->
                new OrderDTO(
                order.getId(),
                order.getUser().getName(),
                order.getState(),
                order.getTotalPrice(),
                order.getOrderDate().toString(),
                order.getOrderEggs().stream().map(orderEgg ->
                        new OrderItemDTO(
                                orderEgg.getType(),
                                orderEgg.getQuantity(),
                                orderEgg.getUnitPrice()
                        )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    /**
     * Cancels an order by its ID.
     *
     * @param id The ID of the order to cancel.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public void cancelOrder(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order with ID " + id + " not found.")
        );
        order.setState(Order.STATE_CANCELED);
        orderRepository.save(order);
    }

    /**
     * Processes an order by creating a bill, initiating payment, and updating the order state.
     *
     * @param id The ID of the order to process.
     * @param paymentMethod The payment method to use for processing the order.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public void processOrder(Long id, String paymentMethod){
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order with ID " + id + " not found.")
        );

        Bill bill = billService.createBillForOrder(order);
        payService.createPayForBill(bill, paymentMethod);

        order.setState(Order.STATE_COMPLETED);
        orderRepository.save(order);
    }
}
