package com.gymapp.ms_notificaciones.repository;

import com.gymapp.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByMiembroIdOrderByFechaCreacionDesc(Long miembroId);
    List<Notificacion> findByMiembroIdAndLeidaFalseOrderByFechaCreacionDesc(Long miembroId);
    long countByMiembroIdAndLeidaFalse(Long miembroId);




    @Query("SELECT n FROM Notificacion n WHERE n.miembroId = :miembroId AND n.leida = true ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findLeidasPorMiembro(@Param("miembroId") Long miembroId);


    @Query("SELECT n FROM Notificacion n WHERE LOWER(n.titulo) LIKE LOWER(CONCAT('%', :palabra, '%'))")
    List<Notificacion> buscarPorTitulo(String palabra);


    @Query("SELECT n FROM Notificacion n WHERE n.miembroId = :miembroId AND n.fechaCreacion >= :fecha ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findRecientesPorMiembro(@Param("miembroId") Long miembroId, @Param("fecha") LocalDateTime fecha);


    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.leida = false")
    long countGlobalNoLeidas();


    @Query("SELECT n FROM Notificacion n ORDER BY n.fechaCreacion DESC LIMIT 10")
    List<Notificacion> findTop10RecientesGlobal();
}