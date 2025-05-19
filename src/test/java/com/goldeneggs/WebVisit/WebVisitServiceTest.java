package com.goldeneggs.WebVisit;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebVisitServiceTest {

    @Mock
    private WebVisitRepository repository;

    @InjectMocks
    private WebVisitService service;

    @Mock
    private HttpServletRequest request;

    @Test
    void testSaveVisit() {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        service.saveVisit(request);

        ArgumentCaptor<WebVisit> captor = ArgumentCaptor.forClass(WebVisit.class);
        verify(repository).save(captor.capture());

        WebVisit saved = captor.getValue();
        assertEquals("192.168.1.1", saved.getIp());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void testGetVisitCount() {
        when(repository.count()).thenReturn(42L);

        long count = service.getVisitCount();

        assertEquals(42L, count);
        verify(repository).count();
    }
}

