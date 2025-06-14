package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Dto.InventoryMovement.InventoryMovementDTO;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Egg.EggRepository;
import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Order.Order;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryMovementServiceImplTest {

    @InjectMocks
    private InventoryMovementServiceImpl movementService;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private EggRepository eggRepository;

    @Mock
    private UserRepository userRepository;

    private InventoryMovement movement;
    private User user;
    private Egg egg;
    private Order order;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        //Create test role
        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        //Create test user
        RegisterDto registerDto = new RegisterDto();
        registerDto.setId(1L);
        registerDto.setName("Felipe");
        registerDto.setPhoneNumber("1234567");
        registerDto.setEmail("test@example.com");
        registerDto.setUsername("felipe123");
        registerDto.setPassword("Password1");
        registerDto.setAddress("Calle Falsa 123");
        registerDto.setRoleId(role.getId());

        user = new User();
        user.setId(registerDto.getId());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setName(registerDto.getName());
        user.setAddress(registerDto.getAddress());
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singletonList(role));
        user.setEnabled(true);

        //Crate test supplier
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("Avicola la floresta")
                .address("Km1 via Calarca-Armenia")
                .build();

        //Crete type egg test
        TypeEgg typeEgg = TypeEgg.builder()
                .id(1L)
                .type("AA")
                .build();

        //Create egg test
        egg = Egg.builder()
                .id(1L)
                .type(typeEgg)
                .color("Blanco")
                .buyPrice(12000.0)
                .salePrice(13000.0)
                .expirationDate(new java.sql.Date(System.currentTimeMillis()))  // Usando java.sql.Date
                .supplier(supplier)
                .avibleQuantity(1000)
                .movements(Collections.emptyList())
                .build();

        //Create egg of order
        OrderEgg orderEgg = OrderEgg.builder()
                .id(1L)
                .type(egg.getType().getType())
                .quantity(10)
                .unitPrice(12000.0)
                .subtotal(120000.0)
                .build();

        //Create egg of order
        OrderEgg orderEgg2 = OrderEgg.builder()
                .id(2L)
                .type(egg.getType().getType())
                .quantity(5)
                .unitPrice(12000.0)
                .subtotal(60000.0)
                .build();

        //Create order
        order = Order.builder()
                .id(1L)
                .user(user)
                .orderEggs(List.of(orderEgg, orderEgg2))
                .totalPrice(180000.0)
                .orderDate(new java.sql.Date(System.currentTimeMillis()))  // Usando java.sql.Date
                .state("ENVIADO")
                .build();

        //Put the order in orderEggs
        orderEgg.setOrder(order);
        orderEgg2.setOrder(order);

        movement = InventoryMovement.builder()
                .id(1L)
                .movementDate(new java.sql.Date(System.currentTimeMillis()))
                .combs(10)
                .egg(egg)
                .order(order)
                .user(user)
                .build();
    }

    @Test
    void getAllMovements_ShouldReturnListOfDTOs() {
        InventoryMovement movement1 = InventoryMovement.builder()
                .id(2L)
                .movementDate(new java.sql.Date(System.currentTimeMillis()))
                .combs(100)
                .egg(egg)
                .order(order)
                .user(user)
                .build();

        when(movementRepository.findAll()).thenReturn(Arrays.asList(movement1, movement));

        List<InventoryMovementDTO> result = movementService.getAll();

        assertEquals(2, result.size());

        InventoryMovementDTO aux = result.get(1);
        assertEquals(movement.getId(), aux.getId());
        assertEquals(movement.getCombs(), aux.getCombs());

        verify(movementRepository).findAll();
    }


    @Test
    void testGetMovementById_Success() {
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

        InventoryMovement result = movementService.get(1L);
        assertEquals(movement.getUser(), result.getUser());
    }

    @Test
    void testGetMovementById_NotFound() {
        when(movementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movementService.get(1L));
    }

    @Test
    void testUpdateMovement_Success() {
        Long movementId = 1L;
        InventoryMovement existingMovement = new InventoryMovement();
        existingMovement.setId(movementId);
        existingMovement.setCombs(50); // Valor original
        existingMovement.setUser(new User());
        existingMovement.setEgg(new Egg());
        existingMovement.setMovementDate(new java.sql.Date(System.currentTimeMillis()));
        existingMovement.setOrder(new Order());

        InventoryMovement updatedMovement = new InventoryMovement();
        updatedMovement.setCombs(80); // Nuevo valor
        updatedMovement.setUser(existingMovement.getUser());
        updatedMovement.setEgg(existingMovement.getEgg());
        updatedMovement.setMovementDate(existingMovement.getMovementDate());
        updatedMovement.setOrder(existingMovement.getOrder());

        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(updatedMovement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(updatedMovement.getMovementDate())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateCombs(updatedMovement.getCombs())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateUser(updatedMovement.getUser())).thenReturn(true);
            when(eggRepository.existsById(updatedMovement.getEgg().getId())).thenReturn(true);
            when(userRepository.existsById(updatedMovement.getUser().getId())).thenReturn(true);

            when(movementRepository.findById(movementId)).thenReturn(Optional.of(existingMovement));

            when(movementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
                InventoryMovement saved = invocation.getArgument(0);
                saved.setId(movementId);
                return saved;
            });
            InventoryMovement result = movementService.update(movementId, updatedMovement);

            assertNotNull(result);
            assertEquals(movementId, result.getId());
            assertEquals(80, result.getCombs()); // Verificar que el valor fue actualizado

            verify(movementRepository).findById(movementId);

            verify(movementRepository).save(argThat(mov ->
                    mov.getId().equals(movementId) && mov.getCombs() == 80
            ));
        }
    }

    @Test
    void testUpdateEgg_NotFound(){
        when(movementRepository.findById(1L)).thenReturn(Optional.empty());

        InventoryMovement updated = new InventoryMovement();

        assertThrows(ResourceNotFoundException.class, () -> movementService.update(1L, updated));

        verify(movementRepository).findById(1L);
    }

    @Test
    void testSaveEgg_Success(){
        // Mockeamos todas las validaciones como exitosas
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(movement.getMovementDate())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateCombs(movement.getCombs())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateUser(movement.getUser())).thenReturn(true);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(true);
            when(userRepository.existsById(movement.getUser().getId())).thenReturn(true);

            when(movementRepository.save(movement)).thenReturn(movement);

            InventoryMovement result = movementService.save(movement);

            assertNotNull(result);
            assertEquals(movement, result);
        }
    }

    @Test
    void testDeleteMovement_Success() {
        when(movementRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movementRepository).deleteById(1L);

        assertDoesNotThrow(() -> movementService.delete(1L));
    }

    @Test
    void testDeleteMovement_NotFound() {
        when(movementRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> movementService.delete(1L));
    }

    @Test
    void testSaveMovement_InvalidEgg_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(false);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("Invalid egg", exception.getMessage());
        }
    }

    @Test
    void testSaveMovement_EggDoesNotExist_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(false);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("Egg does not exist", exception.getMessage());
        }
    }

    @Test
    void testSaveMovement_InvalidDate_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(movement.getMovementDate())).thenReturn(false);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(true);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("Invalid date", exception.getMessage());
        }
    }

    @Test
    void testSaveMovement_InvalidCombs_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(movement.getMovementDate())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateCombs(movement.getCombs())).thenReturn(false);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(true);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("Invalid combs", exception.getMessage());
        }
    }

    @Test
    void testSaveMovement_InvalidUser_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(movement.getMovementDate())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateCombs(movement.getCombs())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateUser(movement.getUser())).thenReturn(false);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(true);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("Invalid user", exception.getMessage());
        }
    }

    @Test
    void testSaveMovement_UserDoesNotExist_ShouldThrow() {
        try (MockedStatic<InventoryMovementValidator> mockedValidator = mockStatic(InventoryMovementValidator.class)) {
            mockedValidator.when(() -> InventoryMovementValidator.validateEgg(movement.getEgg())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateMovementDate(movement.getMovementDate())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateCombs(movement.getCombs())).thenReturn(true);
            mockedValidator.when(() -> InventoryMovementValidator.validateUser(movement.getUser())).thenReturn(true);
            when(eggRepository.existsById(movement.getEgg().getId())).thenReturn(true);
            when(userRepository.existsById(movement.getUser().getId())).thenReturn(false);

            InvalidInventoryMovementDataException exception = assertThrows(
                    InvalidInventoryMovementDataException.class,
                    () -> movementService.save(movement)
            );
            assertEquals("User does not exist", exception.getMessage());
        }
    }

    @Test
    void createMovementForEgg_ShouldCreateMovement_WhenDataIsValid() {
        // Datos de prueba
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Egg egg = new Egg();
        egg.setAvibleQuantity(60); // 60 / 30 = 2 combs

        Order order = new Order();

        // Configuración de mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Ejecución
        movementService.createMovementForEgg(egg, order, userId);

        // Verificaciones
        verify(userRepository).findById(userId);

        // Captura el argumento guardado
        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(movementCaptor.capture());

        InventoryMovement savedMovement = movementCaptor.getValue();

        // Aserciones
        assertEquals(2, savedMovement.getCombs()); // 60 / 30 = 2
        assertEquals(egg, savedMovement.getEgg());
        assertEquals(order, savedMovement.getOrder());
        assertEquals(mockUser, savedMovement.getUser());
        assertNotNull(savedMovement.getMovementDate());
    }
}
