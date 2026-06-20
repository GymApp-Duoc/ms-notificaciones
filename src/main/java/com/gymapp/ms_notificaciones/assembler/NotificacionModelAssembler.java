package com.gymapp.ms_notificaciones.assembler;

import com.gymapp.ms_notificaciones.controller.NotificacionController;
import com.gymapp.ms_notificaciones.dto.NotificacionResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificacionModelAssembler implements RepresentationModelAssembler<NotificacionResponseDTO, EntityModel<NotificacionResponseDTO>> {

    @Override
    public EntityModel<NotificacionResponseDTO> toModel(NotificacionResponseDTO dto) {
        return EntityModel.of(dto,

                linkTo(methodOn(NotificacionController.class).obtenerTodas(dto.getMiembroId())).withRel("bandeja")
        );
    }
}