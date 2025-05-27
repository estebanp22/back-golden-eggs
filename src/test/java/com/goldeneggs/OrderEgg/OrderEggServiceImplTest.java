package com.goldeneggs.OrderEgg;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidOrderEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.TypeEgg.TypeEgg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEggServiceImplTest {

    @Mock
    private OrderEggRepository orderEggRepository;

    @InjectMocks
    private OrderEggServiceImpl orderEggService;

    private OrderEgg sampleOrderEgg;
    private OrderEgg invalidOrderEgg;

    @BeforeEach
    void setUp() {
        // Configurar un OrderEgg válido para pruebas
        sampleOrderEgg = new OrderEgg();
        sampleOrderEgg.setId(1L);
        sampleOrderEgg.setQuantity(5);
        sampleOrderEgg.setUnitPrice(10.0);
        sampleOrderEgg.setSubtotal(50.0);
        sampleOrderEgg.setOrder(new Order());
        sampleOrderEgg.setType("AA");
        sampleOrderEgg.setColor("Blanco");

        // Configurar un OrderEgg inválido para pruebas
        invalidOrderEgg = new OrderEgg();
        invalidOrderEgg.setQuantity(-1);
        invalidOrderEgg.setUnitPrice(-5.0);
        invalidOrderEgg.setSubtotal(100.0);
    }

    @Test
    void testGetAll() {
        // Configurar
        when(orderEggRepository.findAll()).thenReturn(List.of(sampleOrderEgg));

        // Ejecutar
        List<OrderEgg> result = orderEggService.getAll();

        // Verificar
        assertEquals(1, result.size());
        verify(orderEggRepository).findAll();
    }

    @Test
    void testGetById_Existing() {
        // Configurar
        when(orderEggRepository.findById(1L)).thenReturn(Optional.of(sampleOrderEgg));

        // Ejecutar
        OrderEgg result = orderEggService.get(1L);

        // Verificar
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        // Configurar
        when(orderEggRepository.findById(2L)).thenReturn(Optional.empty());

        // Ejecutar y Verificar
        assertThrows(ResourceNotFoundException.class, () -> orderEggService.get(2L));
    }

    @Test
    void testSave_ValidData() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar validaciones
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(sampleOrderEgg.getQuantity())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateUnitPrice(sampleOrderEgg.getUnitPrice())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateSubtotal(
                    sampleOrderEgg.getSubtotal(),
                    sampleOrderEgg.getQuantity(),
                    sampleOrderEgg.getUnitPrice()
            )).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateOrder(sampleOrderEgg.getOrder())).thenReturn(true);

            // Configurar repositorio
            when(orderEggRepository.save(sampleOrderEgg)).thenReturn(sampleOrderEgg);

            // Ejecutar
            OrderEgg result = orderEggService.save(sampleOrderEgg);

            // Verificar
            assertNotNull(result);
            verify(orderEggRepository).save(sampleOrderEgg);
        }
    }

    @Test
    void testSave_InvalidQuantity() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar validación fallida
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(invalidOrderEgg.getQuantity())).thenReturn(false);

            // Ejecutar y Verificar
            InvalidOrderEggDataException exception = assertThrows(
                    InvalidOrderEggDataException.class,
                    () -> orderEggService.save(invalidOrderEgg)
            );
            assertEquals("Invalid quantity", exception.getMessage());
        }
    }

    @Test
    void testSave_InvalidUnitPrice() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar validaciones
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(invalidOrderEgg.getQuantity())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateUnitPrice(invalidOrderEgg.getUnitPrice())).thenReturn(false);

            // Ejecutar y Verificar
            InvalidOrderEggDataException exception = assertThrows(
                    InvalidOrderEggDataException.class,
                    () -> orderEggService.save(invalidOrderEgg)
            );
            assertEquals("Invalid unitPrice", exception.getMessage());
        }
    }

    @Test
    void testSave_InvalidSubtotal() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar validaciones
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(sampleOrderEgg.getQuantity())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateUnitPrice(sampleOrderEgg.getUnitPrice())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateSubtotal(
                    sampleOrderEgg.getSubtotal(),
                    sampleOrderEgg.getQuantity(),
                    sampleOrderEgg.getUnitPrice()
            )).thenReturn(false);

            // Ejecutar y Verificar
            InvalidOrderEggDataException exception = assertThrows(
                    InvalidOrderEggDataException.class,
                    () -> orderEggService.save(sampleOrderEgg)
            );
            assertEquals("Invalid total to pay", exception.getMessage());
        }
    }

    @Test
    void testSave_InvalidOrder() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar validaciones
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(sampleOrderEgg.getQuantity())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateUnitPrice(sampleOrderEgg.getUnitPrice())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateSubtotal(
                    sampleOrderEgg.getSubtotal(),
                    sampleOrderEgg.getQuantity(),
                    sampleOrderEgg.getUnitPrice()
            )).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateOrder(sampleOrderEgg.getOrder())).thenReturn(false);

            // Ejecutar y Verificar
            InvalidOrderEggDataException exception = assertThrows(
                    InvalidOrderEggDataException.class,
                    () -> orderEggService.save(sampleOrderEgg)
            );
            assertEquals("Invalid order", exception.getMessage());
        }
    }

    @Test
    void testUpdate_Existing() {
        try (MockedStatic<OrderEggValidator> mockedValidator = mockStatic(OrderEggValidator.class)) {
            // Configurar datos
            OrderEgg updatedOrderEgg = new OrderEgg();
            updatedOrderEgg.setQuantity(10);
            updatedOrderEgg.setUnitPrice(15.0);
            updatedOrderEgg.setSubtotal(150.0);
            updatedOrderEgg.setOrder(new Order());
            updatedOrderEgg.setType("AAA");
            updatedOrderEgg.setColor("Blanco");

            // Configurar validaciones
            mockedValidator.when(() -> OrderEggValidator.validateQuantity(updatedOrderEgg.getQuantity())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateUnitPrice(updatedOrderEgg.getUnitPrice())).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateSubtotal(
                    updatedOrderEgg.getSubtotal(),
                    updatedOrderEgg.getQuantity(),
                    updatedOrderEgg.getUnitPrice()
            )).thenReturn(true);
            mockedValidator.when(() -> OrderEggValidator.validateOrder(updatedOrderEgg.getOrder())).thenReturn(true);

            // Configurar repositorio
            when(orderEggRepository.findById(1L)).thenReturn(Optional.of(sampleOrderEgg));
            when(orderEggRepository.save(any(OrderEgg.class))).thenReturn(updatedOrderEgg);

            // Ejecutar
            OrderEgg result = orderEggService.update(1L, updatedOrderEgg);

            // Verificar
            assertEquals(10, result.getQuantity());
            assertEquals(15.0, result.getUnitPrice());
            verify(orderEggRepository).save(any(OrderEgg.class));
        }
    }

    @Test
    void testUpdate_NotFound() {
        // Configurar
        when(orderEggRepository.findById(2L)).thenReturn(Optional.empty());

        // Ejecutar y Verificar
        assertThrows(ResourceNotFoundException.class, () -> orderEggService.update(2L, sampleOrderEgg));
    }

    @Test
    void testDelete_Existing() {
        // Configurar
        when(orderEggRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderEggRepository).deleteById(1L);

        // Ejecutar
        orderEggService.delete(1L);

        // Verificar
        verify(orderEggRepository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Configurar
        when(orderEggRepository.existsById(2L)).thenReturn(false);

        // Ejecutar y Verificar
        assertThrows(ResourceNotFoundException.class, () -> orderEggService.delete(2L));
    }

    @Test
    void createOrderEggForEgg_ShouldCreateOrderEgg_WhenDataIsValid() {
        // Configurar datos de prueba
        Egg egg = new Egg();
        egg.setType(new TypeEgg()); // Asume que EggType tiene un campo "type"
        egg.setColor("Blanco");
        egg.setAvibleQuantity(100);
        egg.setBuyPrice(2.5);

        OrderEgg savedOrderEgg = new OrderEgg();
        savedOrderEgg.setType(egg.getType().getType());
        savedOrderEgg.setColor(egg.getColor());
        savedOrderEgg.setQuantity(egg.getAvibleQuantity());
        savedOrderEgg.setUnitPrice(egg.getBuyPrice());
        savedOrderEgg.setSubtotal(egg.getBuyPrice() * egg.getAvibleQuantity());

        // Configurar mock
        when(orderEggRepository.save(any(OrderEgg.class))).thenReturn(savedOrderEgg);

        // Ejecutar método
        OrderEgg result = orderEggService.createOrderEggForEgg(egg);

        // Verificar
        assertNotNull(result);
        assertEquals("Blanco", result.getColor());
        assertEquals(100, result.getQuantity());
        assertEquals(2.5, result.getUnitPrice());
        assertEquals(250.0, result.getSubtotal()); // 2.5 * 100 = 250
        verify(orderEggRepository).save(any(OrderEgg.class));
    }
}