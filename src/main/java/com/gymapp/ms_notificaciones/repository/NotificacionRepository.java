package com.gymapp.ms_notificaciones.repository;

import com.gymapp.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {


    List<Notificacion> findByMiembroIdOrderByFechaCreacionDesc(Long miembroId);


    List<Notificacion> findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(Long miembroId);

    // Contar cuántas tiene pendientes por leer
    long countByMiembroIdAndLeidaFalse(Long miembroId);
}