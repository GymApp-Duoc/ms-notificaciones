package com.gymapp.ms_notificaciones.controller;

import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("Petición REST: Crear nueva notificación para el miembro ID {}", dto.getMiembroId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearNotificacion(dto));
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerTodas(@PathVariable Long miembroId) {
        log.info("Petición REST: Obtener historial completo de notificaciones del miembro ID {}", miembroId);
        return ResponseEntity.ok(service.obtenerTodasPorMiembro(miembroId));
    }

    @GetMapping("/miembro/{miembroId}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNoLeidas(@PathVariable Long miembroId) {
        log.info("Petición REST: Consultar bandeja de no leídas para el miembro ID {}", miembroId);
        return ResponseEntity.ok(service.obtenerNoLeidasPorMiembro(miembroId));
    }

    @GetMapping("/miembro/{miembroId}/contador")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Long miembroId) {
        log.info("Petición REST: Contar notificaciones pendientes del miembro ID {}", miembroId);
        long cantidad = service.contarNoLeidas(miembroId);
        return ResponseEntity.ok(Map.of("noLeidas", cantidad));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(@PathVariable Long id) {
        log.info("Petición REST: Actualizar estado a 'leída' para la notificación ID {}", id);
        return ResponseEntity.ok(service.marcarComoLeida(id));
    }

    @PatchMapping("/miembro/{miembroId}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long miembroId) {
        log.info("Petición REST: Marcar TODAS las notificaciones como leídas para el miembro ID {}", miembroId);
        service.marcarTodasComoLeidas(miembroId);
        return ResponseEntity.noContent().build();
    }
}