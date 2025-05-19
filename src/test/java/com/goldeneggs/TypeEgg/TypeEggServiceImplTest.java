package com.goldeneggs.TypeEgg;

import com.goldeneggs.Exception.InvalidTypeEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TypeEggServiceImplTest {

    @InjectMocks
    private TypeEggServiceImpl service;

    @Mock
    private TypeEggRepository repository;

    private TypeEgg sampleEgg;

    @BeforeEach
    void setUp() {
        sampleEgg = TypeEgg.builder()
                .id(1L)
                .type("Organic")
                .build();
    }

    @Test
    void save_ValidTypeEgg_ShouldSaveSuccessfully() {
        when(repository.existsByTypeIgnoreCase("Organic")).thenReturn(false);
        when(repository.findById(sampleEgg.getId())).thenReturn(Optional.of(sampleEgg));
        when(repository.save(sampleEgg)).thenReturn(sampleEgg);

        TypeEgg result = service.save(sampleEgg);

        assertEquals(sampleEgg, result);
        verify(repository).save(sampleEgg);
    }


    @Test
    void save_InvalidType_ShouldThrowException() {
        TypeEgg invalid = new TypeEgg(null, "", null);

        assertThrows(InvalidTypeEggDataException.class, () -> service.save(invalid));
        verify(repository, never()).save(any());
    }

    @Test
    void save_ExistingType_ShouldThrowException() {
        when(repository.existsByTypeIgnoreCase("Organic")).thenReturn(true);

        assertThrows(InvalidTypeEggDataException.class, () -> service.save(sampleEgg));
        verify(repository, never()).save(any());
    }

    @Test
    void save_WithIdNotFound_ShouldThrowNotFound() {
        sampleEgg.setId(2L);
        when(repository.existsByTypeIgnoreCase("Organic")).thenReturn(false);
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.save(sampleEgg));
    }

    @Test
    void save_WithIdButDuplicateType_ShouldThrowInvalidException() {
        sampleEgg.setId(1L);
        TypeEgg stored = TypeEgg.builder().id(1L).type("Brown").build();

        when(repository.existsByTypeIgnoreCase("Organic")).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(stored));

        assertThrows(InvalidTypeEggDataException.class, () -> service.save(sampleEgg));
    }

    @Test
    void update_Valid_ShouldUpdate() {
        TypeEgg updated = TypeEgg.builder().id(1L).type("Free-Range").build();

        when(repository.findById(1L)).thenReturn(Optional.of(sampleEgg));
        when(repository.existsByTypeIgnoreCaseAndIdNot("Free-Range", 1L)).thenReturn(false);
        when(repository.save(any())).thenReturn(updated);

        TypeEgg result = service.update(1L, updated);

        assertEquals("Free-Range", result.getType());
        verify(repository).save(sampleEgg);
    }

    @Test
    void update_NotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, sampleEgg));
    }

    @Test
    void update_DuplicateType_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleEgg));
        when(repository.existsByTypeIgnoreCaseAndIdNot("Organic", 1L)).thenReturn(true);

        assertThrows(InvalidTypeEggDataException.class, () -> service.update(1L, sampleEgg));
    }

    @Test
    void getById_Found_ShouldReturnEntity() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleEgg));

        TypeEgg result = service.getById(1L);

        assertEquals(sampleEgg, result);
    }

    @Test
    void getById_NotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void delete_Existing_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_NotExisting_ShouldThrowException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void getAll_ShouldReturnList() {
        List<TypeEgg> eggs = List.of(sampleEgg);
        when(repository.findAll()).thenReturn(eggs);

        List<TypeEgg> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(sampleEgg, result.get(0));
    }
}

