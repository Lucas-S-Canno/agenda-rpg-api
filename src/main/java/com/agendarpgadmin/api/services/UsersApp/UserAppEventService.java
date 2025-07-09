package com.agendarpgadmin.api.services.UsersApp;

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
public class UserAppEventService {

    @Autowired
    private EventRepository eventRepository;

    public EventDTO createEvent(EventDTO eventDTO) {
        EventEntity entity = convertToEntity(eventDTO);
        EventEntity savedEntity = eventRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Optional<EventEntity> existingEvent = eventRepository.findById(id);

        if (existingEvent.isPresent()) {
            EventEntity entity = existingEvent.get();

            // Atualizar campos
            entity.setTitulo(eventDTO.getTitulo());
            entity.setSistema(eventDTO.getSistema());
            entity.setHorario(eventDTO.getHorario());
            entity.setNumeroDeVagas(eventDTO.getNumeroDeVagas());
            entity.setNarrador(eventDTO.getNarrador());
            entity.setData(eventDTO.getData());
            entity.setLocal(eventDTO.getLocal());
            entity.setTags(String.join(",", eventDTO.getTags()));
            entity.setDescricao(eventDTO.getDescricao());
            entity.setJogadores(String.join(",", eventDTO.getJogadores()));

            EventEntity updatedEntity = eventRepository.save(entity);
            return convertToDTO(updatedEntity);
        }

        return null;
    }

    public ResponseDTO<EventDTO> getEventById(Long id) {
        Optional<EventEntity> eventEntity = eventRepository.findById(id);

        if (eventEntity.isPresent()) {
            EventDTO eventDTO = convertToDTO(eventEntity.get());
            return new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Evento encontrado com sucesso",
                    eventDTO
            );
        }

        return new ResponseDTO<>(
                HttpStatus.NOT_FOUND.value(),
                "Evento não encontrado",
                null
        );
    }
    public List<EventDTO> getMyEvents(String userId) {
        List<EventEntity> events = eventRepository.findByNarrador(userId);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EventEntity convertToEntity(EventDTO dto) {
        EventEntity entity = new EventEntity();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setSistema(dto.getSistema());
        entity.setHorario(dto.getHorario());
        entity.setNumeroDeVagas(dto.getNumeroDeVagas());
        entity.setNarrador(dto.getNarrador());
        entity.setData(dto.getData());
        entity.setLocal(dto.getLocal());
        entity.setTags(String.join(",", dto.getTags()));
        entity.setDescricao(dto.getDescricao());
        entity.setJogadores(String.join(",", dto.getJogadores()));
        return entity;
    }

    private EventDTO convertToDTO(EventEntity entity) {
        EventDTO dto = new EventDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setSistema(entity.getSistema());
        dto.setHorario(entity.getHorario());
        dto.setNumeroDeVagas(entity.getNumeroDeVagas());
        dto.setNarrador(entity.getNarrador());
        dto.setData(entity.getData());
        dto.setLocal(entity.getLocal());

        // Converter tags e jogadores de String para List
        if (entity.getTags() != null && !entity.getTags().isEmpty()) {
            dto.setTags(Arrays.asList(entity.getTags().split(",")));
        }

        dto.setDescricao(entity.getDescricao());

        if (entity.getJogadores() != null && !entity.getJogadores().isEmpty()) {
            dto.setJogadores(Arrays.asList(entity.getJogadores().split(",")));
        }

        return dto;
    }

}