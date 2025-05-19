package com.goldeneggs.WebVisit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.servlet.http.HttpServletRequest;


@ExtendWith(MockitoExtension.class)
class WebVisitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WebVisitService service;

    @InjectMocks
    private WebVisitController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegisterVisit() throws Exception {
        mockMvc.perform(post("/api/v1/visits")
                        .with(request -> {
                            request.setRemoteAddr("192.168.1.1");
                            return request;
                        }))
                .andExpect(status().isOk());

        verify(service).saveVisit(any(HttpServletRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetVisitCount() throws Exception {
        when(service.getVisitCount()).thenReturn(10L);

        mockMvc.perform(get("/api/v1/visits/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
}

