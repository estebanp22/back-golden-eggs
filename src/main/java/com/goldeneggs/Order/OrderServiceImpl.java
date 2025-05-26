package com.goldeneggs.Order;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillService;
import com.goldeneggs.Dto.Order.CartItemDTO;
import com.goldeneggs.Dto.Order.OrderDTO;
import com.goldeneggs.Dto.Order.OrderItemDTO;
import com.goldeneggs.Dto.Order.OrderRequestDTO;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Egg.EggService;
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

    @Autowired
    private EggService eggService;

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

        // 1. Crear la orden vacía (sin huevos)
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(dto.getTotalPrice());
        order.setOrderDate(dto.getOrderDate());
        order.setState(dto.getState());

        validateOrderOrThrow(order);
        Order savedOrder = orderRepository.save(order); // ← ya tiene ID

        // 2. Ahora sí procesar los huevos
        List<OrderEgg> orderEggs = dto.getCartItem().stream().map(item -> {
            OrderEgg oe = new OrderEgg();
            oe.setType(item.getName());
            oe.setColor(item.getColor());
            oe.setQuantity(item.getQuantity());
            oe.setUnitPrice(item.getPrice());
            oe.setSubtotal(item.getQuantity() * item.getPrice());
            oe.setOrder(savedOrder); // ← ya puedes asociar la orden

            // Descontar los huevos y registrar el movimiento
            boolean response = eggService.updateEggQuantity(oe.getQuantity() * 30, oe.getColor(), oe.getType(), user, savedOrder);

            if (!response) {
                throw new RuntimeException("No hay suficiente inventario para " + oe.getQuantity() * 30 + " huevos " + oe.getColor() + " tipo " + oe.getType());
            }
            return oe;
        }).collect(Collectors.toList());

        savedOrder.setOrderEggs(orderEggs);
        return orderRepository.save(savedOrder); // actualizar con los huevos
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

        for (OrderEgg oe : order.getOrderEggs()) {
            int totalHuevos = oe.getQuantity() * 30;

            // Suponiendo que eggService.updateEggQuantity permite valores negativos para restar y positivos para sumar
            boolean result = eggService.restockEggs(totalHuevos, oe.getColor(), oe.getType(), order.getUser(), order);

            if (!result) {
                throw new RuntimeException("Error al devolver huevos al inventario para tipo: " + oe.getType() + ", color: " + oe.getColor());
            }
        }
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

    /**
     * {@inheritDoc}
     * @param id id for the useer
     * @param orderEggs list of egg in the order
     * @param egg to save the price
     * @return
     */
    @Override
    public Order createOrderForEgg(Long id, List<OrderEgg> orderEggs, Egg egg){
        Order order = new Order();
        order.setUser(userRepository.getById(id));
        order.setOrderEggs(orderEggs);
        order.setTotalPrice(egg.getBuyPrice() * egg.getAvibleQuantity());
        order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
        order.setState(Order.STATE_INVENTORY);

        return orderRepository.save(order);
    }

    /**
     * {inheritDoc}
     * @param id id to the customer
     * @return a list of all orders by customer
     */
    @Override
    public List<OrderRequestDTO> getOrdersByCustomerId(Long id){
        List<Order> orders = orderRepository.getOrdersByUserId(id);

        return orders.stream().map(order -> {
            OrderRequestDTO dto = new OrderRequestDTO();
            dto.setIdCustomer(order.getUser().getId());
            dto.setState(order.getState());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setOrderDate(order.getOrderDate());

            List<CartItemDTO> cartItems = order.getOrderEggs().stream().map(item -> {
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setName(item.getType());
                cartItemDTO.setPrice(item.getUnitPrice());
                cartItemDTO.setQuantity(item.getQuantity());
                return cartItemDTO;
            }).toList();

            dto.setCartItem(cartItems);

            return dto;
        }).toList();
    }

}
