package com.goldeneggs.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Exception.InvalidSupplierDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplierController).build();

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("SupplierOne");
        supplier.setAddress("123 Supplier Street");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_Success() throws Exception {
        when(supplierService.save(any(Supplier.class))).thenReturn(supplier);

        mockMvc.perform(post("/api/v1/suppliers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SupplierOne"))
                .andExpect(jsonPath("$.address").value("123 Supplier Street"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_InvalidSupplier() throws Exception {
        when(supplierService.save(any(Supplier.class)))
                .thenThrow(new InvalidSupplierDataException("Invalid supplier data"));

        mockMvc.perform(post("/api/v1/suppliers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid supplier data"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAll() throws Exception {
        when(supplierService.getAll()).thenReturn(List.of(supplier));

        mockMvc.perform(get("/api/v1/suppliers/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("SupplierOne"))
                .andExpect(jsonPath("$[0].address").value("123 Supplier Street"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_Success() throws Exception {
        when(supplierService.get(1L)).thenReturn(supplier);

        mockMvc.perform(get("/api/v1/suppliers/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SupplierOne"))
                .andExpect(jsonPath("$.address").value("123 Supplier Street"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_NotFound() throws Exception {
        when(supplierService.get(1L)).thenThrow(new ResourceNotFoundException("Supplier not found"));

        mockMvc.perform(get("/api/v1/suppliers/get/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_Success() throws Exception {
        when(supplierService.update(eq(1L), any(Supplier.class))).thenReturn(supplier);

        mockMvc.perform(put("/api/v1/suppliers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SupplierOne"))
                .andExpect(jsonPath("$.address").value("123 Supplier Street"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_NotFound() throws Exception {
        when(supplierService.update(eq(1L), any(Supplier.class)))
                .thenThrow(new ResourceNotFoundException("Supplier not found"));

        mockMvc.perform(put("/api/v1/suppliers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Supplier not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_InvalidData() throws Exception {
        when(supplierService.update(eq(1L), any(Supplier.class)))
                .thenThrow(new InvalidSupplierDataException("Invalid supplier data"));

        mockMvc.perform(put("/api/v1/suppliers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid supplier data"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/suppliers/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Supplier not found")).when(supplierService).delete(1L);

        mockMvc.perform(delete("/api/v1/suppliers/delete/1"))
                .andExpect(status().isNotFound());
    }
}
