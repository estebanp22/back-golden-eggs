package com.goldeneggs.Egg;

import com.goldeneggs.Dto.Egg.EggSummaryDto;
import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.InventoryMovement.InventoryMovement;
import com.goldeneggs.InventoryMovement.InventoryMovementRepository;
import com.goldeneggs.Order.Order;
import com.goldeneggs.OrderEgg.OrderEggRepository;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.Supplier.SupplierRepository;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.User.UserRepository;
import com.goldeneggs.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.goldeneggs.TypeEgg.TypeEggRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.time.LocalDate;
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

    @Mock
    private OrderEggRepository orderEggRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

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

            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);
            when(supplierRepository.existsById(sampleEgg.getSupplier().getId())).thenReturn(true);
            when(eggRepository.save(sampleEgg)).thenReturn(sampleEgg);
            when(userRepository.getById(1L)).thenReturn(User.builder().id(1L).build());

            Egg result = eggService.save(sampleEgg, 1L);

            assertNotNull(result);
            assertEquals(sampleEgg.getType(), result.getType());
            verify(inventoryMovementRepository).save(any(InventoryMovement.class));
        }
    }

    @Test
    void testUpdateEgg_Success() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateColor(sampleEgg.getColor())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateBuyPrice(sampleEgg.getBuyPrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSalePrice(sampleEgg.getBuyPrice(), sampleEgg.getSalePrice())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateExpirationDate(sampleEgg.getExpirationDate())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateSupplier(sampleEgg.getSupplier())).thenReturn(true);
            mockedValidator.when(() -> EggValidator.validateAviableQuantity(sampleEgg.getAvibleQuantity())).thenReturn(true);

            when(eggRepository.findById(1L)).thenReturn(Optional.of(sampleEgg));
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(true);
            when(supplierRepository.existsById(sampleEgg.getSupplier().getId())).thenReturn(true);
            when(eggRepository.save(any(Egg.class))).thenReturn(sampleEgg);

            InventoryMovement mockMovement = InventoryMovement.builder()
                    .id(1L)
                    .egg(sampleEgg)
                    .combs(10)
                    .movementDate(new java.sql.Date(System.currentTimeMillis()))
                    .build();

            when(inventoryMovementRepository.findTopByEggOrderByMovementDateDesc(sampleEgg))
                    .thenReturn(Optional.of(mockMovement));

            Egg updated = eggService.update(1L, sampleEgg, 1L);

            assertNotNull(updated);
            verify(inventoryMovementRepository).save(mockMovement);
        }
    }

    @Test
    void testUpdateEgg_NotFound(){
        when(eggRepository.findById(1L)).thenReturn(Optional.empty());

        Egg updated = new Egg();

        assertThrows(ResourceNotFoundException.class, () -> eggService.update(1L, updated, 1L));

        verify(eggRepository).findById(1L);
    }

    @Test
    void testSaveEgg_InvalidType_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(false);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg, 1L));
            assertEquals("Type egg not valid", exception.getMessage());
        }
    }

    @Test
    void testSaveEgg_TypeDoesNotExist_ShouldThrow() {
        try (MockedStatic<EggValidator> mockedValidator = mockStatic(EggValidator.class)) {
            mockedValidator.when(() -> EggValidator.validateTypeEgg(sampleEgg.getType())).thenReturn(true);
            when(typeEggRepository.existsById(sampleEgg.getType().getId())).thenReturn(false);

            InvalidEggDataException exception = assertThrows(InvalidEggDataException.class,
                    () -> eggService.save(sampleEgg, 1L));
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
                    () -> eggService.save(sampleEgg, 1L));
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
                    () -> eggService.save(sampleEgg, 1L));
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
                    () -> eggService.save(sampleEgg, 1L));
            assertEquals("sale price invalid", exception.getMessage());
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
                    () -> eggService.save(sampleEgg, 1L));
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
                    () -> eggService.save(sampleEgg, 1L));
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
                    () -> eggService.save(sampleEgg, 1L));
            assertEquals("Aviable quantity invalid", exception.getMessage());
        }
    }

    @Test
    void testFindEggSummaries_ReturnsList() {
        TypeEgg typeEgg1 = TypeEgg.builder().type("AAA").build();
        TypeEgg typeEgg2 = TypeEgg.builder().type("AA").build();
        Date expirationDate = new java.sql.Date(System.currentTimeMillis());
        List<EggSummaryDto> summaries = List.of(
                new EggSummaryDto(typeEgg1, "Blanco", 100, expirationDate),
                new EggSummaryDto(typeEgg2, "Rojo", 80, expirationDate)
        );

        when(eggRepository.findEggSummaries()).thenReturn(summaries);

        List<EggSummaryDto> result = eggService.findEggSummaries();

        assertEquals(summaries.size(), result.size());
        assertEquals("Blanco", result.get(0).getColor());
        verify(eggRepository, times(1)).findEggSummaries();
    }

    @Test
    void testDeleteEgg_EggDoesNotExist_ThrowsResourceNotFound() {
        Long eggId = 1L;
        when(eggRepository.existsById(eggId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eggService.delete(eggId));

        verify(eggRepository, times(1)).existsById(eggId);
        verify(eggRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteEgg_AssociatedWithActiveOrder_ThrowsInvalidEggData() {
        Long eggId = 1L;
        Egg egg = new Egg();
        egg.setId(eggId);
        egg.setColor("Blanco");

        TypeEgg typeEgg = new TypeEgg();
        typeEgg.setType("Grande");
        egg.setType(typeEgg);

        when(eggRepository.existsById(eggId)).thenReturn(true);
        when(eggRepository.findById(eggId)).thenReturn(Optional.of(egg));
        when(orderEggRepository.existsByTypeAndColorInActiveOrders("Grande", "Blanco", Order.STATE_PENDING))
                .thenReturn(true);

        assertThrows(InvalidEggDataException.class, () -> eggService.delete(eggId));

        verify(eggRepository).existsById(eggId);
        verify(eggRepository).findById(eggId);
        verify(orderEggRepository).existsByTypeAndColorInActiveOrders("Grande", "Blanco", Order.STATE_PENDING);
        verify(eggRepository, never()).deleteById(any());
    }

    @Test
    void testValidateEggOrThrow_InvalidExpirationDate_ThrowsInvalidEggData() {
        TypeEgg typeEgg = new TypeEgg();
        typeEgg.setId(123L);
        typeEgg.setType("M");

        lenient().when(typeEggRepository.existsById(123L)).thenReturn(true);

        Supplier supplier = new Supplier();
        supplier.setId(456L);
        supplier.setName("Gallinas Unidas");

        lenient().when(supplierRepository.existsById(456L)).thenReturn(true);

        Egg egg = new Egg();
        egg.setType(typeEgg);
        egg.setColor("Blanco");
        egg.setBuyPrice(100.0);
        egg.setSalePrice(150.0);
        egg.setExpirationDate(Date.valueOf(LocalDate.now().minusDays(1))); // fecha inválida
        egg.setSupplier(supplier);
        egg.setAvibleQuantity(10);

        InvalidEggDataException ex = assertThrows(InvalidEggDataException.class, () ->
                invokePrivateValidateEggOrThrow(egg)
        );

        assertEquals("The expiration date must be in the future.", ex.getMessage());
    }

    private void invokePrivateValidateEggOrThrow(Egg egg) {
        ReflectionTestUtils.invokeMethod(eggService, "validateEggOrThrow", egg);
    }

    @Test
    void testDelete_ValidEgg_DeletesSuccessfully() {
        Long eggId = 1L;

        TypeEgg typeEgg = new TypeEgg();
        typeEgg.setType("M");

        Egg egg = new Egg();
        egg.setId(eggId);
        egg.setColor("Blanco");
        egg.setType(typeEgg);

        when(eggRepository.existsById(eggId)).thenReturn(true);
        when(eggRepository.findById(eggId)).thenReturn(Optional.of(egg));
        when(orderEggRepository.existsByTypeAndColorInActiveOrders("M", "Blanco", Order.STATE_PENDING)).thenReturn(false);

        eggService.delete(eggId);

        verify(eggRepository, times(1)).deleteById(eggId);
    }

}
