package com.goldeneggs.Egg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldeneggs.Dto.Egg.EggSummaryDto;
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

import java.sql.Date;
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
    void testSaveEgg_ReturnsCreated() throws Exception {
        Long userId = 1L;
        Egg egg = new Egg();
        egg.setId(10L);

        when(eggService.save(any(Egg.class), eq(userId))).thenReturn(egg);

        mockMvc.perform(post("/api/v1/eggs/save/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testSaveEgg_InvalidData_ReturnsBadRequest() throws Exception {
        Long userId = 1L;
        Egg egg = new Egg();

        when(eggService.save(any(Egg.class), eq(userId)))
                .thenThrow(new InvalidEggDataException("Invalid egg"));

        mockMvc.perform(post("/api/v1/eggs/save/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid egg")); // <- cambiado
    }

    @Test
    void testGetAllEggDto_ReturnsOk() throws Exception {
        TypeEgg typeEgg = TypeEgg.builder()
                .id(1L)
                .type("AA")
                .build();
        Date expirationDate = new java.sql.Date(System.currentTimeMillis());

        EggSummaryDto dto = new EggSummaryDto(typeEgg, "Blanco", 10, expirationDate);
        when(eggService.findEggSummaries()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/eggs/getAllEggDto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type.type").value("AA"));
    }

    @Test
    void testUpdateEgg_ReturnsOk() throws Exception {
        Long eggId = 1L;
        Long userId = 2L;
        Egg egg = new Egg();
        egg.setId(eggId);

        when(eggService.update(eq(eggId), any(Egg.class), eq(userId))).thenReturn(egg);

        mockMvc.perform(put("/api/v1/eggs/update/{id}/{idUser}", eggId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eggId));
    }

    @Test
    void testUpdateEgg_NotFound_Returns404() throws Exception {
        Long eggId = 1L, userId = 2L;
        Egg egg = new Egg();

        when(eggService.update(eq(eggId), any(Egg.class), eq(userId)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/eggs/update/{id}/{idUser}", eggId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void testUpdateEgg_InvalidData_Returns400() throws Exception {
        Long eggId = 1L, userId = 2L;
        Egg egg = new Egg();

        when(eggService.update(eq(eggId), any(Egg.class), eq(userId)))
                .thenThrow(new InvalidEggDataException("Invalid data"));

        mockMvc.perform(put("/api/v1/eggs/update/{id}/{idUser}", eggId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(egg)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    void testDeleteEgg_Success_Returns200() throws Exception {
        Long eggId = 1L;

        mockMvc.perform(delete("/api/v1/eggs/delete/{id}", eggId))
                .andExpect(status().isOk());

        verify(eggService).delete(eggId);
    }

    @Test
    void testDeleteEgg_NotFound_Returns404() throws Exception {
        Long eggId = 1L;

        doThrow(new ResourceNotFoundException("Not found"))
                .when(eggService).delete(eggId);

        mockMvc.perform(delete("/api/v1/eggs/delete/{id}", eggId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void testDeleteEgg_InvalidData_Returns400() throws Exception {
        Long eggId = 1L;

        doThrow(new InvalidEggDataException("Invalid delete"))
                .when(eggService).delete(eggId);

        mockMvc.perform(delete("/api/v1/eggs/delete/{id}", eggId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid delete"));
    }

}
