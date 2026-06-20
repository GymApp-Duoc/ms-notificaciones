package com.gymapp.ms_notificaciones.service;

import com.gymapp.ms_notificaciones.client.MiembroClient;
import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.exception.BusinessException;
import com.gymapp.ms_notificaciones.model.Notificacion;
import com.gymapp.ms_notificaciones.repository.NotificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final MiembroClient miembroClient;

    @Override
    @Transactional
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto) {
        log.info("Iniciando creación de notificación para el miembro ID: {}", dto.getMiembroId());

        validarMiembroEnMsMiembros(dto.getMiembroId());

        Notificacion n = new Notificacion();
        n.setMiembroId(dto.getMiembroId());
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setLeida(false);

        Notificacion guardada = repository.save(n);
        log.info("Notificación registrada exitosamente con ID: {}", guardada.getId());

        return mapearADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerTodasPorMiembro(Long miembroId) {
        log.info("Consultando historial completo de notificaciones para el miembro ID: {}", miembroId);
        return repository.findByMiembroIdOrderByFechaCreacionDesc(miembroId).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerNoLeidasPorMiembro(Long miembroId) {
        log.info("Consultando notificaciones no leídas para el miembro ID: {}", miembroId);
        return repository.findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(miembroId).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new BusinessException("No se pudo actualizar estado: Notificación ID " + id + " no encontrada."));

        n.setLeida(true);
        log.info("Notificación ID {} marcada como leída.", id);
        return mapearADTO(repository.save(n));
    }

    @Override
    @Transactional
    public void marcarTodasComoLeidas(Long miembroId) {
        List<Notificacion> noLeidas = repository.findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(miembroId);
        if (!noLeidas.isEmpty()) {
            noLeidas.forEach(n -> n.setLeida(true));
            repository.saveAll(noLeidas);
            log.info("Operación masiva: Marcadas {} notificaciones como leídas para el miembro {}", noLeidas.size(), miembroId);
        } else {
            log.info("El miembro {} no tiene notificaciones pendientes por leer.", miembroId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long miembroId) {
        return repository.countByMiembroIdAndLeidaFalse(miembroId);
    }



    private void validarMiembroEnMsMiembros(Long miembroId) {
        try {
            Boolean existe = miembroClient.validarMiembro(miembroId);

            if (existe == null || !existe) {
                log.warn("Rechazo de creación: El miembro ID {} no existe en los registros.", miembroId);
                throw new BusinessException("No se puede enviar la notificación: El miembro destinatario no existe.");
            }
        } catch (FeignException.NotFound e) {
            log.warn("El MS-MIEMBROS reportó que el miembro ID {} no fue encontrado.", miembroId);
            throw new BusinessException("No se puede enviar la notificación: El miembro destinatario no existe.");
        } catch (FeignException e) {
            log.error("Fallo grave de comunicación con MS-MIEMBROS: {}", e.getMessage());
            throw new BusinessException("Servicio de validación de usuarios temporalmente no disponible.");
        }
    }

    private NotificacionResponseDTO mapearADTO(Notificacion n) {
        return NotificacionResponseDTO.builder()
                .id(n.getId())
                .miembroId(n.getMiembroId())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .leida(n.isLeida())
                .fechaCreacion(n.getFechaCreacion())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> reporteLeidasPorMiembro(Long miembroId) {
        return repository.findLeidasPorMiembro(miembroId).stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> reporteBuscarPorTitulo(String palabra) {
        return repository.buscarPorTitulo(palabra).stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> reporteRecientes(Long miembroId) {
        return repository.findRecientesPorMiembro(miembroId, java.time.LocalDateTime.now().minusDays(1)).stream()
                .map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long reporteTotalNoLeidasSistema() {
        return repository.countGlobalNoLeidas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> reporteTop10Global() {
        return repository.findTop10RecientesGlobal().stream().map(this::mapearADTO).collect(Collectors.toList());
    }
}

