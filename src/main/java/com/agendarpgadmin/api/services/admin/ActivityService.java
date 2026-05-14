package com.agendarpgadmin.api.services.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ActivityDTO;
import com.agendarpgadmin.api.entities.ActivityEntity;
import com.agendarpgadmin.api.entities.ActivityParticipantEntity;
import com.agendarpgadmin.api.entities.EventEntity;
import com.agendarpgadmin.api.entities.enums.ActivityType;
import com.agendarpgadmin.api.repositories.ActivityParticipantRepository;
import com.agendarpgadmin.api.repositories.ActivityRepository;
import com.agendarpgadmin.api.repositories.EventRepository;
import com.agendarpgadmin.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityParticipantRepository activityParticipantRepository;

    public List<ActivityDTO> getByEventId(UUID eventId) {
        return activityRepository.findByEventoId(eventId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ActivityDTO findById(UUID id) {
        return activityRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    public ActivityDTO create(UUID eventId, ActivityDTO dto) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        validateCommonRules(event, dto);
        validateTypeSpecificRules(dto);

        ActivityEntity entity = convertToEntity(dto, event);
        entity = activityRepository.save(entity);
        return convertToDTO(entity);
    }

    public ActivityDTO update(UUID id, ActivityDTO dto) {
        ActivityEntity current = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        EventEntity event = current.getEvento();
        validateCommonRules(event, dto);
        validateTypeSpecificRules(dto);

        current.setTipo(dto.getTipo());
        current.setNome(dto.getNome());
        current.setDescricao(dto.getDescricao());
        current.setInicio(dto.getInicio());
        current.setFim(dto.getFim());
        current.setLocalComplemento(dto.getLocalComplemento());
        current.setSistema(dto.getSistema());
        current.setNumeroVagas(dto.getNumeroVagas());
        current.setTags(dto.getTags() == null ? null : String.join(",", dto.getTags()));
        current.setNarradorId(dto.getNarradorId());
        current.setTema(dto.getTema());
        current.setPalestranteId(dto.getPalestranteId());

        current = activityRepository.save(current);
        return convertToDTO(current);
    }

    public void delete(UUID id) {
        activityRepository.deleteById(id);
    }

    private void validateCommonRules(EventEntity event, ActivityDTO dto) {
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da atividade é obrigatório");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome da atividade é obrigatório");
        }
        if (dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição da atividade é obrigatória");
        }
        if (dto.getInicio() == null || dto.getFim() == null) {
            throw new IllegalArgumentException("Início e fim da atividade são obrigatórios");
        }
        if (!dto.getFim().isAfter(dto.getInicio())) {
            throw new IllegalArgumentException("Fim da atividade deve ser maior que início");
        }
        if (dto.getLocalComplemento() == null || dto.getLocalComplemento().isBlank()) {
            throw new IllegalArgumentException("Complemento do local é obrigatório");
        }

        LocalDateTime eventStart = event.getInicio();
        LocalDateTime eventEnd = event.getFim();
        if (dto.getInicio().isBefore(eventStart) || dto.getFim().isAfter(eventEnd)) {
            throw new IllegalArgumentException("Atividade deve estar contida no horário do evento");
        }
    }

    private void validateTypeSpecificRules(ActivityDTO dto) {
        if (dto.getTipo() == ActivityType.RPG_MESA) {
            if (dto.getSistema() == null || dto.getSistema().isBlank()) {
                throw new IllegalArgumentException("Sistema é obrigatório para mesa RPG");
            }
            if (dto.getNumeroVagas() == null || dto.getNumeroVagas() <= 0) {
                throw new IllegalArgumentException("Número de vagas deve ser maior que zero para mesa RPG");
            }
            if (dto.getNarradorId() == null || userRepository.findById(dto.getNarradorId()).isEmpty()) {
                throw new IllegalArgumentException("Narrador inválido");
            }
            if (dto.getTags() == null || dto.getTags().isEmpty()) {
                throw new IllegalArgumentException("Tags são obrigatórias para mesa RPG");
            }
        }

        if (dto.getTipo() == ActivityType.WORKSHOP) {
            if (dto.getTema() == null || dto.getTema().isBlank()) {
                throw new IllegalArgumentException("Tema é obrigatório para workshop");
            }
            if (dto.getPalestranteId() == null || userRepository.findById(dto.getPalestranteId()).isEmpty()) {
                throw new IllegalArgumentException("Palestrante inválido");
            }
        }
    }

    private ActivityEntity convertToEntity(ActivityDTO dto, EventEntity event) {
        ActivityEntity entity = new ActivityEntity();
        entity.setEvento(event);
        entity.setTipo(dto.getTipo());
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setInicio(dto.getInicio());
        entity.setFim(dto.getFim());
        entity.setLocalComplemento(dto.getLocalComplemento());
        entity.setSistema(dto.getSistema());
        entity.setNumeroVagas(dto.getNumeroVagas());
        entity.setTags(dto.getTags() == null ? null : String.join(",", dto.getTags()));
        entity.setNarradorId(dto.getNarradorId());
        entity.setTema(dto.getTema());
        entity.setPalestranteId(dto.getPalestranteId());
        return entity;
    }

    private ActivityDTO convertToDTO(ActivityEntity entity) {
        List<String> tags = new ArrayList<>();
        if (entity.getTags() != null && !entity.getTags().isBlank()) {
            tags = Arrays.stream(entity.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }

        List<UUID> participants = activityParticipantRepository.findByAtividadeId(entity.getId()).stream()
                .map(ActivityParticipantEntity::getUsuarioId)
                .toList();

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
                new ArrayList<>(participants)
        );
    }
}

