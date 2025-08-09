package com.agendarpgadmin.api.services.UsersApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.entities.EventEntity;
import com.agendarpgadmin.api.repositories.EventRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAppEventService {

    @Autowired
    private EventRepository eventRepository;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

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

    public EventDTO registerPlayerInEvent(Long eventId, String playerId) {
        Optional<EventEntity> eventEntity = eventRepository.findById(eventId);

        if (eventEntity.isPresent()) {
            EventEntity entity = eventEntity.get();

            // Criar lista mutável corretamente
            List<String> jogadores = new java.util.ArrayList<>();
            if (entity.getJogadores() != null && !entity.getJogadores().isEmpty()) {
                jogadores.addAll(Arrays.asList(entity.getJogadores().split(",")));
            }

            if (jogadores.contains(playerId)) {
                return convertToDTO(entity);
            }

            // Adicionar o jogador à lista
            jogadores.add(playerId);
            entity.setJogadores(String.join(",", jogadores));

            EventEntity updatedEntity = eventRepository.save(entity);
            return convertToDTO(updatedEntity);
        }

        return null; // Evento não encontrado
    }

    public List<EventDTO> getRegisteredEvents(String userId) {
        List<EventEntity> events = eventRepository.findByJogadoresContaining(userId);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO unregisterPlayerFromEvent(Long id, String userId) {
        Optional<EventEntity> eventEntity = eventRepository.findById(id);

        if (eventEntity.isPresent()) {
            EventEntity entity = eventEntity.get();

            // Criar lista mutável corretamente
            List<String> jogadores = new java.util.ArrayList<>();
            if (entity.getJogadores() != null && !entity.getJogadores().isEmpty()) {
                jogadores.addAll(Arrays.asList(entity.getJogadores().split(",")));
            }

            if (!jogadores.contains(userId)) {
                return convertToDTO(entity); // Jogador não está registrado
            }

            // Remover o jogador da lista
            jogadores.remove(userId);
            entity.setJogadores(String.join(",", jogadores));

            EventEntity updatedEntity = eventRepository.save(entity);
            return convertToDTO(updatedEntity);
        }

        return null; // Evento não encontrado
    }

    public List<EventDTO> getUpcomingMyEvents(String userId) {
        String today = java.time.LocalDate.now().toString();
        return eventRepository.findByNarrador(userId).stream()
                .filter(e -> e.getData().compareTo(today) >= 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingRegisteredEvents(String userId) {
        String today = java.time.LocalDate.now().toString();
        return eventRepository.findByJogadoresContaining(userId).stream()
                .filter(e -> e.getData().compareTo(today) >= 0)
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