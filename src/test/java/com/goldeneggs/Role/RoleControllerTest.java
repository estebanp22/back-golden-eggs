package com.goldeneggs.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Role role;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();

        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_Success() throws Exception {
        when(roleService.insert(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/api/v1/roles/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_InvalidRole() throws Exception {
        when(roleService.insert(any(Role.class))).thenThrow(new InvalidRoleDataException("Invalid name"));

        mockMvc.perform(post("/api/v1/roles/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid name"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_DuplicateRole() throws Exception {
        when(roleService.insert(any(Role.class))).thenThrow(new DuplicateRoleNameException("Role already exists"));

        mockMvc.perform(post("/api/v1/roles/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Role already exists"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAll() throws Exception {
        when(roleService.getAll()).thenReturn(List.of(role));

        mockMvc.perform(get("/api/v1/roles/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_Success() throws Exception {
        when(roleService.get(1L)).thenReturn(role);

        mockMvc.perform(get("/api/v1/roles/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_NotFound() throws Exception {
        when(roleService.get(1L)).thenThrow(new ResourceNotFoundException("Role not found"));

        mockMvc.perform(get("/api/v1/roles/get/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_Success() throws Exception {
        mockMvc.perform(put("/api/v1/roles/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found")).when(roleService).update(any(Role.class));

        mockMvc.perform(put("/api/v1/roles/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_InvalidData() throws Exception {
        doThrow(new InvalidRoleDataException("Invalid role data")).when(roleService).update(any(Role.class));

        mockMvc.perform(put("/api/v1/roles/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid role data"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_Duplicate() throws Exception {
        doThrow(new DuplicateRoleNameException("Duplicate role")).when(roleService).update(any(Role.class));

        mockMvc.perform(put("/api/v1/roles/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Duplicate role"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/roles/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found")).when(roleService).delete(1L);

        mockMvc.perform(delete("/api/v1/roles/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role not found"));
    }
}
