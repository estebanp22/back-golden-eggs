package com.goldeneggs.Egg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EggControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EggService eggService;

    @InjectMocks
    private EggController eggController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Egg egg;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eggController).build();

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

        egg = new Egg();
        egg.setId(1L);
        egg.setType(typeEgg);
        egg.setColor("blanco");
        egg.setBuyPrice(500.0);
        egg.setSalePrice(600.0);
        egg.setAvibleQuantity(100);
        egg.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        egg.setSupplier(supplier);
    }

    @Test
    void testGetAll() throws Exception {
        when(eggService.getAll()).thenReturn(List.of(egg));

        mockMvc.perform(get("/api/v1/eggs/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(egg.getId()));
    }

    @Test
    void testGetTotalEggQuantity() throws Exception {
        when(eggService.getTotalEggQuantity()).thenReturn((long) egg.getAvibleQuantity());

        mockMvc.perform(get("/api/v1/eggs/totalQuantity"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(egg.getAvibleQuantity())));
    }

    @Test
    void testDeleteEgg_Success() throws Exception {
        Long eggId = 1L;

        doNothing().when(eggService).delete(eggId);

        mockMvc.perform(delete("/api/v1/eggs/delete/{id}", eggId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEgg_NotFound() throws Exception {
        Long eggId = 1L;

        doThrow(new ResourceNotFoundException("Egg not found")).when(eggService).delete(eggId);

        mockMvc.perform(delete("/api/v1/eggs/delete/{id}", eggId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetEggById_Success() throws Exception {
        Long eggId = 1L;

        when(eggService.get(eggId)).thenReturn(egg);

        mockMvc.perform(get("/api/v1/eggs/get/{id}", eggId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(egg.getId()))
                .andExpect(jsonPath("$.color").value(egg.getColor()))
                .andExpect(jsonPath("$.salePrice").value(egg.getSalePrice()));
    }

    @Test
    void testGetEggById_NotFound() throws Exception {
        Long eggId = 1L;

        when(eggService.get(eggId)).thenThrow(new ResourceNotFoundException("Egg not found"));

        mockMvc.perform(get("/api/v1/eggs/get/{id}", eggId))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSaveEgg_Success() throws Exception {
        when(eggService.save(any(Egg.class))).thenReturn(egg);

        mockMvc.perform(post("/api/v1/eggs/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(egg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(egg.getId()))
                .andExpect(jsonPath("$.color").value(egg.getColor()))
                .andExpect(jsonPath("$.salePrice").value(egg.getSalePrice()));
    }

    @Test
    void testSaveEgg_InvalidData() throws Exception {
        when(eggService.save(any(Egg.class)))
                .thenThrow(new InvalidEggDataException("Datos inválidos"));

        mockMvc.perform(post("/api/v1/eggs/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(egg)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Datos inválidos"));
    }

    @Test
    void testUpdateEgg_Success() throws Exception {
        Long eggId = 1L;

        when(eggService.update(eq(eggId), any(Egg.class))).thenReturn(egg);

        mockMvc.perform(put("/api/v1/eggs/update/{id}", eggId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(egg.getId()))
                .andExpect(jsonPath("$.color").value(egg.getColor()))
                .andExpect(jsonPath("$.salePrice").value(egg.getSalePrice()));
    }

    @Test
    void testUpdateEgg_NotFound() throws Exception {
        Long eggId = 1L;

        when(eggService.update(eq(eggId), any(Egg.class)))
                .thenThrow(new ResourceNotFoundException("Egg not found"));

        mockMvc.perform(put("/api/v1/eggs/update/{id}", eggId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEgg_InvalidData() throws Exception {
        Long eggId = 1L;

        when(eggService.update(eq(eggId), any(Egg.class)))
                .thenThrow(new InvalidEggDataException("Invalid egg data"));

        mockMvc.perform(put("/api/v1/eggs/update/{id}", eggId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid egg data"));
    }
}
