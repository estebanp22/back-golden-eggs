package com.goldeneggs.TypeEgg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Exception.InvalidTypeEggDataException;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TypeEggControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TypeEggService typeEggService;

    @InjectMocks
    private TypeEggController typeEggController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TypeEgg typeEgg;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(typeEggController).build();

        typeEgg = TypeEgg.builder()
                .id(1L)
                .type("Organic")
                .suppliers(List.of())
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_Success() throws Exception {
        when(typeEggService.save(any(TypeEgg.class))).thenReturn(typeEgg);

        mockMvc.perform(post("/api/v1/egg-types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Organic"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_Invalid() throws Exception {
        when(typeEggService.save(any(TypeEgg.class)))
                .thenThrow(new InvalidTypeEggDataException("Invalid data"));

        mockMvc.perform(post("/api/v1/egg-types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSave_Unexpected() throws Exception {
        when(typeEggService.save(any(TypeEgg.class)))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(post("/api/v1/egg-types/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAll() throws Exception {
        when(typeEggService.getAll()).thenReturn(List.of(typeEgg));

        mockMvc.perform(get("/api/v1/egg-types/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("Organic"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_Success() throws Exception {
        when(typeEggService.getById(1L)).thenReturn(typeEgg);

        mockMvc.perform(get("/api/v1/egg-types/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Organic"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetById_NotFound() throws Exception {
        when(typeEggService.getById(1L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/egg-types/get/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/egg-types/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDelete_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found"))
                .when(typeEggService).delete(1L);

        mockMvc.perform(delete("/api/v1/egg-types/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_Success() throws Exception {
        when(typeEggService.update(eq(1L), any(TypeEgg.class)))
                .thenReturn(typeEgg);

        mockMvc.perform(put("/api/v1/egg-types/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Organic"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_NotFound() throws Exception {
        when(typeEggService.update(eq(1L), any(TypeEgg.class)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/egg-types/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdate_Invalid() throws Exception {
        when(typeEggService.update(eq(1L), any(TypeEgg.class)))
                .thenThrow(new InvalidTypeEggDataException("Invalid"));

        mockMvc.perform(put("/api/v1/egg-types/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeEgg)))
                .andExpect(status().isBadRequest());
    }
}
