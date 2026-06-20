package com.gymapp.ms_notificaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto para el envío de una nueva alerta o mensaje a un miembro")
public class NotificacionRequestDTO {

    @NotNull(message = "El ID del miembro es obligatorio")
    @Positive(message = "El ID del miembro debe ser un número positivo")
    @Schema(description = "ID del usuario destinatario (ms-miembros)", example = "105")
    private Long miembroId;

    @NotBlank(message = "El título de la notificación no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Schema(description = "Asunto o título corto", example = "Reserva Confirmada")
    private String titulo;

    @NotBlank(message = "El cuerpo del mensaje es obligatorio")
    @Size(min = 5, max = 500, message = "El mensaje debe tener entre 5 y 500 caracteres")
    @Schema(description = "Cuerpo detallado del mensaje", example = "Tu reserva para la clase de Yoga está confirmada.")
    private String mensaje;
}