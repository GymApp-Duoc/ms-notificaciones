package com.gymapp.ms_notificaciones.service;

import com.gymapp.ms_notificaciones.client.MiembroClient;
import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.exception.BusinessException;
import com.gymapp.ms_notificaciones.model.Notificacion;
import com.gymapp.ms_notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private MiembroClient miembroClient;

    @InjectMocks
    private NotificacionServiceImpl service;

    private Notificacion notiMock;

    @BeforeEach
    void setUp() {
        notiMock = new Notificacion(1L, 100L, "Alerta", "Mensaje Test", false, LocalDateTime.now());
    }

    @Test
    void crearNotificacion_Exito() {
        NotificacionRequestDTO request = new NotificacionRequestDTO(100L, "Alerta", "Mensaje Test");
        when(miembroClient.validarMiembro(anyLong())).thenReturn(true);
        when(repository.save(any(Notificacion.class))).thenReturn(notiMock);

        NotificacionResponseDTO response = service.crearNotificacion(request);

        assertNotNull(response);
        assertEquals("Alerta", response.getTitulo());
    }

    @Test
    void crearNotificacion_FallaSiMiembroNoExiste() {
        NotificacionRequestDTO request = new NotificacionRequestDTO(100L, "Alerta", "Mensaje Test");
        when(miembroClient.validarMiembro(anyLong())).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.crearNotificacion(request));
        verify(repository, never()).save(any(Notificacion.class));
    }
}