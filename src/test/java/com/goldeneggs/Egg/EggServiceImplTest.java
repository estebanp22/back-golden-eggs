package com.goldeneggs.Egg;

import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EggServiceImplTest {


    @InjectMocks
    private EggServiceImpl eggService;

    @Mock
    private EggRepository eggRepository;

    @Mock
    private EggValidator eggValidator;

    private Egg sampleEgg;
    private Supplier supplier;
    private TypeEgg typeEgg;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Crate test supplier
        supplier = Supplier.builder()
                .id(1L)
                .name("Avicola la floresta")
                .address("Km1 via Calarca-Armenia")
                .build();

        //Crete type egg test
        typeEgg = TypeEgg.builder()
                .id(1L)
                .type("AA")
                .build();

        sampleEgg = new Egg();
        sampleEgg.setId(1L);
        sampleEgg.setType(typeEgg);
        sampleEgg.setColor("blanco");
        sampleEgg.setBuyPrice(500.0);
        sampleEgg.setSalePrice(600.0);
        sampleEgg.setAvibleQuantity(100);
        sampleEgg.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        sampleEgg.setSupplier(supplier);
    }

    @Test
    void testGetEggById_Success() {
        when(eggRepository.findById(1L)).thenReturn(Optional.of(sampleEgg));

        Egg result = eggService.get(1L);
        assertEquals(sampleEgg.getType(), result.getType());
    }

    @Test
    void testGetEggById_NotFound() {
        when(eggRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eggService.get(1L));
    }

    @Test
    void testDeleteEgg_Success() {
        when(eggRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eggRepository).deleteById(1L);

        assertDoesNotThrow(() -> eggService.delete(1L));
    }

    @Test
    void testDeleteEgg_NotFound() {
        when(eggRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eggService.delete(1L));
    }

    @Test
    void testGetTotalEggQuantity_WithValue() {
        when(eggRepository.getTotalEggQuantity()).thenReturn(1000L);

        Long total = eggService.getTotalEggQuantity();
        assertEquals(1000L, total);
    }

    @Test
    void testGetTotalEggQuantity_NullValue() {
        when(eggRepository.getTotalEggQuantity()).thenReturn(null);

        Long total = eggService.getTotalEggQuantity();
        assertEquals(0L, total);
    }

    @Test
    void getAllEggs_ShouldReturnListoOfEggs(){
        Egg egg2 = Egg.builder()
                .id(2L)
                .type(typeEgg)
                .color("Blanco")
                .salePrice(12000.0)
                .buyPrice(11000.0)
                .avibleQuantity(1000)
                .expirationDate(new java.sql.Date(System.currentTimeMillis()))
                .supplier(supplier)
                .build();

        when(eggRepository.findAll()).thenReturn(Arrays.asList(egg2, sampleEgg));

        List<Egg> result = eggService.getAll();

        assertEquals(2, result.size());

        Egg egg1 = result.get(1);
        assertEquals(sampleEgg.getId(), egg1.getId());
        assertEquals(sampleEgg.getType(), egg1.getType());

        verify(eggRepository).findAll();
    }

    @Test
    void testSaveEgg_Success(){
        // Mockeamos todas las validaciones como exitosas
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
        when(eggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
        when(eggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
        when(eggValidator.validateAviableQuantity(sampleEgg.getAvibleQuantity())).thenReturn(true);

        when(eggRepository.save(sampleEgg)).thenReturn(sampleEgg);

        Egg result = eggService.save(sampleEgg);

        assertNotNull(result);
        assertEquals(sampleEgg, result);
    }

    @Test
    void testUpdateEgg_Success(){
        Egg updatedEgg = new Egg();
        updatedEgg.setType(typeEgg);
        updatedEgg.setColor("Rojo");
        updatedEgg.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        updatedEgg.setBuyPrice(310.0);
        updatedEgg.setSalePrice(360.0);
        updatedEgg.setSupplier(supplier);
        updatedEgg.setAvibleQuantity(120);

        // Mock repo y validaciones
        when(eggRepository.findById(1L)).thenReturn(Optional.of(sampleEgg));
        when(eggValidator.validateTypeEgg(updatedEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(updatedEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(updatedEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(updatedEgg.getBuyPrice(), updatedEgg.getSalePrice())).thenReturn(true);
        when(eggValidator.validateExpirationDate(updatedEgg.getExpirationDate())).thenReturn(true);
        when(eggValidator.validateSupplier(updatedEgg.getSupplier())).thenReturn(true);
        when(eggValidator.validateAviableQuantity(updatedEgg.getAvibleQuantity())).thenReturn(true);

        // Simular guardado
        when(eggRepository.save(any(Egg.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Egg result = eggService.update(1L, updatedEgg);

        assertNotNull(result);
        assertEquals(typeEgg, result.getType());
        assertEquals("Rojo", result.getColor());
        assertEquals(310.0, result.getBuyPrice());
        assertEquals(360.0, result.getSalePrice());
    }

    @Test
    void testUpdateEgg_NotFound(){
        when(eggRepository.findById(1L)).thenReturn(Optional.empty());

        Egg updated = new Egg();

        assertThrows(ResourceNotFoundException.class, () -> eggService.update(1L, updated));

        verify(eggRepository).findById(1L);
    }

    @Test
    void testSaveEgg_InvalidType_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Tipo de huevo no válido o no existente", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidColor_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Color no válido", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidBuyPrice_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Precio de compra inválido", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidSalePrice_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Precio de venta debe ser mayor o igual al de compra", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidExpirationDate_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
        when(eggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("La fecha de expiración debe ser hoy o futura", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidSupplier_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
        when(eggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
        when(eggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Proveedor no válido o inexistente", exception.getMessage());
    }

    @Test
    void testSaveEgg_InvalidQuantity_ShouldThrow() {
        when(eggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
        when(eggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
        when(eggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
        when(eggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
        when(eggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
        when(eggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
        when(eggValidator.validateAviableQuantity(sampleEgg.getAvibleQuantity())).thenReturn(false);

        InvalidEggDataException exception = assertThrows(InvalidEggDataException.class, () -> eggService.save(sampleEgg));
        assertEquals("Cantidad no válida", exception.getMessage());
    }

}
