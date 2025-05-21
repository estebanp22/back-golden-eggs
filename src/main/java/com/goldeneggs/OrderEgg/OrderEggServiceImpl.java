package com.goldeneggs.OrderEgg;

import com.goldeneggs.Exception.InvalidOrderEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link OrderEggService} interface.
 * <p>
 * Provides methods for retrieving, creating, updating, and deleting payments.
 * </p>
 */
@Service
public class OrderEggServiceImpl implements  OrderEggService {

    @Autowired
    private OrderEggRepository orderEggRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderEgg> getAll() {return  orderEggRepository.findAll();}

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderEgg get(Long id) {
        return orderEggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderEgg not found with ID:"+ id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderEgg save(OrderEgg orderEgg) {
        validateOrderEggOrThrow(orderEgg);
        return orderEggRepository.save(orderEgg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderEgg update(Long id, OrderEgg updatedOrderEgg) {
        OrderEgg existing = orderEggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderEgg not found with ID:"+ id));

        validateOrderEggOrThrow(updatedOrderEgg);
        existing.setOrder(updatedOrderEgg.getOrder());
        existing.setEgg(updatedOrderEgg.getEgg());
        existing.setQuantity(updatedOrderEgg.getQuantity());
        existing.setUnitPrice(updatedOrderEgg.getUnitPrice());
        existing.setSubtotal(updatedOrderEgg.getSubtotal());

        return orderEggRepository.save(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!orderEggRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. order egg not found with ID: " + id);
        }
        orderEggRepository.deleteById(id);
    }

    private void validateOrderEggOrThrow(OrderEgg orderEgg) {
        if (!OrderEggValidator.validateQuantity(orderEgg.getQuantity())) {
            throw new InvalidOrderEggDataException("Invalid quantity");
        }
        if (!OrderEggValidator.validateUnitPrice(orderEgg.getUnitPrice())) {
            throw new InvalidOrderEggDataException("Invalid unitPrice");
        }
        if (!OrderEggValidator.validateSubtotal(orderEgg.getSubtotal(), orderEgg.getQuantity(), orderEgg.getUnitPrice())) {
            throw new InvalidOrderEggDataException("Invalid total to pay");
        }
        if (!OrderEggValidator.validateOrder(orderEgg.getOrder())) {
            throw new InvalidOrderEggDataException("Invalid order");
        }
        if (!OrderEggValidator.validateEgg(orderEgg.getEgg())) {
            throw new InvalidOrderEggDataException("Invalid egg");
        }
    }
}
