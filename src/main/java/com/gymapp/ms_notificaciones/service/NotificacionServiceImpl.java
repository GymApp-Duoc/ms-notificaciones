package com.gymapp.ms_notificaciones.service;

import com.gymapp.ms_notificaciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import com.gymapp.ms_notificaciones.exception.BusinessException;
import com.gymapp.ms_notificaciones.model.Notificacion;
import com.gymapp.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${ms.miembros.url}")
    private String miembrosUrl;

    @Override
    @Transactional
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto) {

        validarMiembroEnMsMiembros(dto.getMiembroId());

        Notificacion n = new Notificacion();
        n.setMiembroId(dto.getMiembroId());
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setLeida(false);

        return mapearADTO(repository.save(n));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerTodasPorMiembro(Long miembroId) {
        return repository.findByMiembroIdOrderByFechaCreacionDesc(miembroId).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> obtenerNoLeidasPorMiembro(Long miembroId) {
        return repository.findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(miembroId).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Notificación ID " + id + " no encontrada."));
        n.setLeida(true);
        return mapearADTO(repository.save(n));
    }

    @Override
    @Transactional
    public void marcarTodasComoLeidas(Long miembroId) {
        List<Notificacion> noLeidas = repository.findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(miembroId);
        if (!noLeidas.isEmpty()) {
            noLeidas.forEach(n -> n.setLeida(true));
            repository.saveAll(noLeidas);
            log.info("Marcadas {} notificaciones como leídas para el miembro {}", noLeidas.size(), miembroId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long miembroId) {
        return repository.countByMiembroIdAndLeidaFalse(miembroId);
    }



    private void validarMiembroEnMsMiembros(Long miembroId) {
        try {
            String url = miembrosUrl + "/api/miembros/" + miembroId;
            restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            log.error("Error validando miembro {}: {}", miembroId, e.getMessage());
            throw new BusinessException("No se puede enviar la notificación: El miembro no existe en el sistema.");
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
}

