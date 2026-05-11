package com.agendarpgadmin.api.services.AdminApp;

import com.agendarpgadmin.api.dtos.ActivityDTO;
import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.entities.ActivityEntity;
import com.agendarpgadmin.api.entities.EventEntity;
import com.agendarpgadmin.api.repositories.ActivityRepository;
import com.agendarpgadmin.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventoRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public List<EventDTO> findAll() {
        return eventoRepository.findAllByOrderByInicioDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO findById(Long id) {
        Optional<EventEntity> evento = eventoRepository.findById(id);
        return evento.map(this::convertToDTO).orElse(null);
    }

    public EventDTO create(EventDTO eventDTO, Long creatorUserId) {
        validateEvent(eventDTO);
        EventEntity eventoEntity = convertToEntity(eventDTO);
        eventoEntity.setCreatorUserId(creatorUserId);
        eventoEntity = eventoRepository.save(eventoEntity);
        return convertToDTO(eventoEntity);
    }

    public void delete(Long id) {
        eventoRepository.deleteById(id);
    }

    private EventDTO convertToDTO(EventEntity eventoEntity) {
        List<ActivityDTO> atividades = activityRepository.findByEventoId(eventoEntity.getId()).stream()
                .map(this::convertActivityToDTO)
                .collect(Collectors.toList());

        return new EventDTO(
                eventoEntity.getId(),
                eventoEntity.getNome(),
                eventoEntity.getLocal(),
                eventoEntity.getInicio(),
                eventoEntity.getFim(),
                eventoEntity.getCreatorUserId(),
                atividades
        );
    }

    private EventEntity convertToEntity(EventDTO eventDTO) {
        return new EventEntity(
                eventDTO.getId(),
                eventDTO.getNome(),
                eventDTO.getLocal(),
                eventDTO.getInicio(),
                eventDTO.getFim(),
                eventDTO.getCreatorUserId(),
                new ArrayList<>()
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
        validateEvent(eventDTO);
        EventEntity eventoEntity = convertToEntity(eventDTO);
        eventoEntity.setId(id);
        EventEntity existingEvent = eventoRepository.findById(id).orElse(null);
        if (existingEvent != null) {
            eventoEntity.setCreatorUserId(existingEvent.getCreatorUserId());
        }
        eventoEntity = eventoRepository.save(eventoEntity);
        return convertToDTO(eventoEntity);
    }

    public List<EventDTO> findByCreatorUserId(Long creatorUserId) {
        return eventoRepository.findByCreatorUserIdOrderByInicioDesc(creatorUserId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventoRepository.findByInicioGreaterThanEqualOrderByInicioAsc(now)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void validateEvent(EventDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do evento é obrigatório");
        }
        if (dto.getLocal() == null || dto.getLocal().isBlank()) {
            throw new IllegalArgumentException("Local do evento é obrigatório");
        }
        if (dto.getInicio() == null || dto.getFim() == null) {
            throw new IllegalArgumentException("Início e fim do evento são obrigatórios");
        }
        if (!dto.getFim().isAfter(dto.getInicio())) {
            throw new IllegalArgumentException("Fim do evento deve ser maior que início");
        }
    }

    private ActivityDTO convertActivityToDTO(ActivityEntity entity) {
        List<String> tags = new ArrayList<>();
        if (entity.getTags() != null && !entity.getTags().isBlank()) {
            tags = List.of(entity.getTags().split(","));
        }

        return new ActivityDTO(
                entity.getId(),
                entity.getEvento().getId(),
                entity.getTipo(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getInicio(),
                entity.getFim(),
                entity.getLocalComplemento(),
                entity.getSistema(),
                entity.getNumeroVagas(),
                new ArrayList<>(tags),
                entity.getNarradorId(),
                entity.getTema(),
                entity.getPalestranteId(),
                new ArrayList<>()
        );
    }
}