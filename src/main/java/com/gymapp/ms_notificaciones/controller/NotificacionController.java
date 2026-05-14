package com.gymapp.ms_notificaciones.controller;

import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearNotificacion(dto));
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerTodas(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.obtenerTodasPorMiembro(miembroId));
    }

    @GetMapping("/miembro/{miembroId}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNoLeidas(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.obtenerNoLeidasPorMiembro(miembroId));
    }

    @GetMapping("/miembro/{miembroId}/contador")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Long miembroId) {
        long cantidad = service.contarNoLeidas(miembroId);
        return ResponseEntity.ok(Map.of("noLeidas", cantidad));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarComoLeida(id));
    }

    @PatchMapping("/miembro/{miembroId}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long miembroId) {
        service.marcarTodasComoLeidas(miembroId);
        return ResponseEntity.noContent().build();
    }
}