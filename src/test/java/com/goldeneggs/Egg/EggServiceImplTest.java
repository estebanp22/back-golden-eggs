package com.goldeneggs.Egg;

import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.Supplier.SupplierRepository;
import com.goldeneggs.TypeEgg.TypeEgg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.goldeneggs.TypeEgg.TypeEggRepository;

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
    private TypeEggRepository typeEggRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EggRepository eggRepository;

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
    void testSaveEgg_Success() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateAviableQuantity(sampleEgg.getAvibleQuantity())).thenReturn(true);

            when(typeEggRepository.existsById(1L)).thenReturn(true);
            when(supplierRepository.existsById(1L)).thenReturn(true);
            when(eggRepository.save(sampleEgg)).thenReturn(sampleEgg);

            Egg result = eggService.save(sampleEgg);

            assertNotNull(result);
            assertEquals(sampleEgg, result);
        }
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

        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(updatedEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(updatedEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(updatedEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(updatedEgg.getBuyPrice(), updatedEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(updatedEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(updatedEgg.getSupplier())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateAviableQuantity(updatedEgg.getAvibleQuantity())).thenReturn(true);

            when(eggRepository.findById(1L)).thenReturn(Optional.of(sampleEgg));
            when(typeEggRepository.existsById(1L)).thenReturn(true);
            when(supplierRepository.existsById(1L)).thenReturn(true);
            when(eggRepository.save(any(Egg.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Egg result = eggService.update(1L, updatedEgg);

            assertNotNull(result);
            assertEquals(typeEgg, result.getType());
            assertEquals("Rojo", result.getColor());
            assertEquals(310.0, result.getBuyPrice());
            assertEquals(360.0, result.getSalePrice());
        }
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
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(false);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Type egg not valid", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_TypeDoesNotExist_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(false);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Type egg does not exist", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_InvalidColor_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(false);

            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Color not valid", exception.getMessage());
        }
    }


    @Test
    void testSaveEgg_InvalidBuyPrice_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(false);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("buy price invalid", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_InvalidSalePrice_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(false);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("sale price invalid", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_InvalidExpirationDate_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(false);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("The expiration date must be today or in the future.", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_InvalidSupplier_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(false);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Supplier invalid", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_SupplierDoesNotExist_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
            when(supplierRepository.existsById(sampleEgg.getSupplier().getId())).thenReturn(false);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Supplier does not exist", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_InvalidQuantity_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateAviableQuantity(sampleEgg.getAvibleQuantity())).thenReturn(false);
            when(supplierRepository.existsById(sampleEgg.getSupplier().getId())).thenReturn(true);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg));
            assertEquals("Aviable quantity invalid", exception.getMessage());
        }
    }
}
