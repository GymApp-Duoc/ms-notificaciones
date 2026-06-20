package com.gymapp.ms_notificaciones.controller;

import com.gymapp.ms_notificaciones.assembler.NotificacionModelAssembler;
import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para alertas y bandeja de entrada de usuarios")
public class NotificacionController {

    private final NotificacionService service;
    private final NotificacionModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Crear", description = "Envía una notificación a un miembro validando que exista")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(service.crearNotificacion(dto)));
    }

    @GetMapping("/miembro/{miembroId}")
    @Operation(summary = "Bandeja completa", description = "Retorna el historial con HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> obtenerTodas(@PathVariable Long miembroId) {
        List<EntityModel<NotificacionResponseDTO>> lista = service.obtenerTodasPorMiembro(miembroId).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(NotificacionController.class).obtenerTodas(miembroId)).withSelfRel()));
    }

    @GetMapping("/miembro/{miembroId}/no-leidas")
    @Operation(summary = "Bandeja no leídas")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> obtenerNoLeidas(@PathVariable Long miembroId) {
        List<EntityModel<NotificacionResponseDTO>> lista = service.obtenerNoLeidasPorMiembro(miembroId).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/miembro/{miembroId}/contador")
    @Operation(summary = "Contador de alertas")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Long miembroId) {
        return ResponseEntity.ok(Map.of("noLeidas", service.contarNoLeidas(miembroId)));
    }

    @PatchMapping("/{id}/leer")
    @Operation(summary = "Marcar un mensaje como leído")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.marcarComoLeida(id)));
    }

    @PatchMapping("/miembro/{miembroId}/leer-todas")
    @Operation(summary = "Marcar todas como leídas (Limpiar bandeja)")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long miembroId) {
        service.marcarTodasComoLeidas(miembroId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/reportes/miembro/{miembroId}/leidas")
    @Operation(summary = "Reporte 1: Historial de Leídas")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> reporteLeidas(@PathVariable Long miembroId) {
        List<EntityModel<NotificacionResponseDTO>> lista = service.reporteLeidasPorMiembro(miembroId).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/buscar")
    @Operation(summary = "Reporte 2: Buscar por título")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> reporteBuscar(@RequestParam String palabra) {
        List<EntityModel<NotificacionResponseDTO>> lista = service.reporteBuscarPorTitulo(palabra).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/miembro/{miembroId}/recientes")
    @Operation(summary = "Reporte 3: Alertas de las últimas 24 hrs")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> reporteRecientes(@PathVariable Long miembroId) {
        List<EntityModel<NotificacionResponseDTO>> lista = service.reporteRecientes(miembroId).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/admin/global-no-leidas")
    @Operation(summary = "Reporte 4: Alertas ignoradas globalmente")
    public ResponseEntity<Long> reporteGlobalNoLeidas() {
        return ResponseEntity.ok(service.reporteTotalNoLeidasSistema());
    }

    @GetMapping("/reportes/admin/top10")
    @Operation(summary = "Reporte 5: Últimas 10 emitidas en todo el sistema")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> reporteTop10() {
        List<EntityModel<NotificacionResponseDTO>> lista = service.reporteTop10Global().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }
}