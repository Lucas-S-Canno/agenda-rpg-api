package com.agendarpgadmin.api.services;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.entities.EventEntity;
import com.agendarpgadmin.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventoRepository;

    public List<EventDTO> findAll() {
        return eventoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO findById(Long id) {
        Optional<EventEntity> evento = eventoRepository.findById(id);
        return evento.map(this::convertToDTO).orElse(null);
    }

    public EventDTO create(EventDTO eventDTO) {
        EventEntity eventoEntity = convertToEntity(eventDTO);
        eventoEntity = eventoRepository.save(eventoEntity);
        return convertToDTO(eventoEntity);
    }

    public void delete(Long id) {
        eventoRepository.deleteById(id);
    }

    private EventDTO convertToDTO(EventEntity eventoEntity) {
        return new EventDTO(
                eventoEntity.getId(),
                eventoEntity.getTitulo(),
                eventoEntity.getSistema(),
                eventoEntity.getHorario(),
                eventoEntity.getNumeroDeVagas(),
                eventoEntity.getNarrador(),
                eventoEntity.getData(),
                eventoEntity.getLocal(),
                Arrays.asList(eventoEntity.getTags().split(",")),
                eventoEntity.getDescricao(),
                Arrays.asList(eventoEntity.getJogadores().split(","))
                );
    }

    private EventEntity convertToEntity(EventDTO eventDTO) {
        return new EventEntity(
                eventDTO.getId(),
                eventDTO.getTitulo(),
                eventDTO.getSistema(),
                eventDTO.getHorario(),
                eventDTO.getNumeroDeVagas(),
                eventDTO.getNarrador(),
                eventDTO.getData(),
                eventDTO.getLocal(),
                String.join(",", eventDTO.getTags()),
                eventDTO.getDescricao(),
                String.join(",", eventDTO.getJogadores())
                );
    }

    public ResponseDTO<List<EventDTO>> getAllEventsListResponseDTO(List<EventDTO> events) {
        ResponseDTO<List<EventDTO>> response;
        if (events == null || events.isEmpty()) {
            response = new ResponseDTO<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Events Not Found",
                    events
            );
        } else {
            response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    events
            );
        }
        return response;
    }

    public ResponseDTO<EventDTO> getEventByIdResponseDTO(EventDTO event) {
        ResponseDTO<EventDTO> response;
        if (event == null) {
            response = new ResponseDTO<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Event Not Found",
                    null
            );
        } else {
            response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    event
            );
        }
        return response;
    }

    public EventDTO update(Long id, EventDTO eventDTO) {
        EventEntity eventoEntity = convertToEntity(eventDTO);
        eventoEntity.setId(id);
        eventoEntity = eventoRepository.save(eventoEntity);
        return convertToDTO(eventoEntity);
    }
}