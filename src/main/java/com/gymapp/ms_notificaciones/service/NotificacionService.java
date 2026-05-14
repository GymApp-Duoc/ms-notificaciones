package com.gymapp.ms_notificaciones.service;

import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;

import java.util.List;

public interface NotificacionService {
    NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto);
    List<NotificacionResponseDTO> obtenerTodasPorMiembro(Long miembroId);
    List<NotificacionResponseDTO> obtenerNoLeidasPorMiembro(Long miembroId);
    NotificacionResponseDTO marcarComoLeida(Long id);
    void marcarTodasComoLeidas(Long miembroId);
    long contarNoLeidas(Long miembroId);
}
