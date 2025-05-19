package com.goldeneggs.Supplier;

import com.goldeneggs.Exception.InvalidSupplierDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceImplTest {

    @InjectMocks
    private SupplierServiceImpl service;

    @Mock
    private SupplierRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_ShouldReturnAllSuppliers() {
        List<Supplier> list = List.of(new Supplier(1L, "A", "Addr", null));
        when(repository.findAll()).thenReturn(list);

        List<Supplier> result = service.getAll();
        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void get_WhenFound_ShouldReturnSupplier() {
        Supplier sup = new Supplier(1L, "A", "Addr", null);
        when(repository.findById(1L)).thenReturn(Optional.of(sup));

        Supplier result = service.get(1L);
        assertEquals("A", result.getName());
        verify(repository).findById(1L);
    }

    @Test
    void get_WhenNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.get(1L));
        verify(repository).findById(1L);
    }

    @Test
    void save_WhenValidAndNotDuplicate_ShouldSave() {
        Supplier sup = new Supplier(null, "A", "Addr", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(sup)).thenReturn(true);
            when(repository.existsByNameIgnoreCaseAndAddressIgnoreCase("A", "Addr")).thenReturn(false);
            when(repository.save(sup)).thenReturn(new Supplier(1L, "A", "Addr", null));

            Supplier result = service.save(sup);
            assertNotNull(result.getId());
            assertEquals("A", result.getName());
            verify(repository).save(sup);
        }
    }

    @Test
    void save_WhenInvalid_ShouldThrowException() {
        Supplier sup = new Supplier(null, "A", "Addr", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(sup)).thenReturn(false);

            assertThrows(InvalidSupplierDataException.class, () -> service.save(sup));
            verify(repository, never()).save(any());
        }
    }

    @Test
    void save_WhenDuplicate_ShouldThrowException() {
        Supplier sup = new Supplier(null, "A", "Addr", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(sup)).thenReturn(true);
            when(repository.existsByNameIgnoreCaseAndAddressIgnoreCase("A", "Addr")).thenReturn(true);

            assertThrows(InvalidSupplierDataException.class, () -> service.save(sup));
            verify(repository, never()).save(any());
        }
    }

    @Test
    void update_WhenFoundAndValidAndNotDuplicate_ShouldUpdate() {
        Supplier existing = new Supplier(1L, "A", "Addr", null);
        Supplier updated = new Supplier(null, "B", "Addr2", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(updated)).thenReturn(true);
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.existsByNameIgnoreCaseAndAddressIgnoreCase("B", "Addr2")).thenReturn(false);
            when(repository.save(existing)).thenReturn(existing);

            Supplier result = service.update(1L, updated);
            assertEquals("B", result.getName());
            assertEquals("Addr2", result.getAddress());
            verify(repository).save(existing);
        }
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        Supplier updated = new Supplier(null, "B", "Addr2", null);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, updated));
    }

    @Test
    void update_WhenInvalid_ShouldThrowException() {
        Supplier existing = new Supplier(1L, "A", "Addr", null);
        Supplier updated = new Supplier(null, "B", "Addr2", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(updated)).thenReturn(false);
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            assertThrows(InvalidSupplierDataException.class, () -> service.update(1L, updated));
            verify(repository, never()).save(any());
        }
    }

    @Test
    void update_WhenDuplicate_ShouldThrowException() {
        Supplier existing = new Supplier(1L, "A", "Addr", null);
        Supplier updated = new Supplier(null, "B", "Addr2", null);

        try (MockedStatic<SupplierValidator> validator = mockStatic(SupplierValidator.class)) {
            validator.when(() -> SupplierValidator.isValid(updated)).thenReturn(true);
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.existsByNameIgnoreCaseAndAddressIgnoreCase("B", "Addr2")).thenReturn(true);

            // But updated name and address differ from existing, so it's duplicate
            assertThrows(InvalidSupplierDataException.class, () -> service.update(1L, updated));
            verify(repository, never()).save(any());
        }
    }

    @Test
    void delete_WhenFound_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(any());
    }
}
