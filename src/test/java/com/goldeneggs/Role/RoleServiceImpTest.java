package com.goldeneggs.Role;

import com.goldeneggs.Exception.DuplicateRoleNameException;
import com.goldeneggs.Exception.InvalidRoleDataException;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImpTest {

    @InjectMocks
    private RoleServiceImp roleService;

    @Mock
    private RoleRepository roleRepository;

    private Role validRole;

    @BeforeEach
    void setUp() {
        validRole = new Role();
        validRole.setId(1L);
        validRole.setName("ADMIN");
    }

    @Test
    void testGetById_ExistingRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(validRole));
        Role result = roleService.get(1L);
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void testGetById_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.get(1L));
    }

    @Test
    void testGetAll() {
        when(roleRepository.findAll()).thenReturn(List.of(validRole));
        List<Role> roles = roleService.getAll();
        assertEquals(1, roles.size());
    }

    @Test
    void testInsert_Success() {
        when(roleRepository.findByNameIgnoreCase("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(validRole)).thenReturn(validRole);
        Role result = roleService.insert(validRole);
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void testInsert_InvalidName() {
        validRole.setName("   ");
        assertThrows(InvalidRoleDataException.class, () -> roleService.insert(validRole));
    }

    @Test
    void testInsert_DuplicateName() {
        when(roleRepository.findByNameIgnoreCase("ADMIN")).thenReturn(Optional.of(validRole));
        assertThrows(DuplicateRoleNameException.class, () -> roleService.insert(validRole));
    }

    @Test
    void testUpdate_Success() {
        Role updated = new Role();
        updated.setId(1L);
        updated.setName("NEW");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(validRole));
        when(roleRepository.findByNameIgnoreCase("NEW")).thenReturn(Optional.empty());

        roleService.update(updated);

        verify(roleRepository).save(argThat(role -> role.getName().equals("NEW")));
    }

    @Test
    void testUpdate_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.update(validRole));
    }

    @Test
    void testUpdate_InvalidName() {
        validRole.setName("   ");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(validRole));
        assertThrows(InvalidRoleDataException.class, () -> roleService.update(validRole));
    }

    @Test
    void testUpdate_DuplicateName() {
        Role otherRole = new Role();
        otherRole.setId(2L);
        otherRole.setName("ADMIN");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(validRole));
        when(roleRepository.findByNameIgnoreCase("ADMIN")).thenReturn(Optional.of(otherRole));

        assertThrows(DuplicateRoleNameException.class, () -> roleService.update(validRole));
    }

    @Test
    void testDelete_Success() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        roleService.delete(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(roleRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(1L));
    }
}
