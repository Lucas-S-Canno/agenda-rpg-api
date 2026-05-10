package com.agendarpgadmin.api.services.UsersApp;

import com.agendarpgadmin.api.dtos.ActivityDTO;
import com.agendarpgadmin.api.entities.ActivityEntity;
import com.agendarpgadmin.api.entities.ActivityParticipantEntity;
import com.agendarpgadmin.api.entities.enums.ActivityType;
import com.agendarpgadmin.api.repositories.ActivityParticipantRepository;
import com.agendarpgadmin.api.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAppActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityParticipantRepository activityParticipantRepository;

    public ActivityDTO register(Long activityId, Long userId) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        if (activityParticipantRepository.existsByAtividadeIdAndUsuarioId(activityId, userId)) {
            throw new IllegalStateException("Usuário já inscrito na atividade");
        }

        if (activity.getTipo() == ActivityType.RPG_MESA) {
            long currentCount = activityParticipantRepository.countByAtividadeId(activityId);
            int vagas = activity.getNumeroVagas() == null ? 0 : activity.getNumeroVagas();
            if (currentCount >= vagas) {
                throw new IllegalStateException("Atividade lotada");
            }
        }

        ActivityParticipantEntity participant = new ActivityParticipantEntity();
        participant.setAtividade(activity);
        participant.setUsuarioId(userId);
        participant.setCreatedAt(LocalDateTime.now());
        activityParticipantRepository.save(participant);

        return convertToDTO(activity);
    }

    public ActivityDTO unregister(Long activityId, Long userId) {
        if (!activityParticipantRepository.existsByAtividadeIdAndUsuarioId(activityId, userId)) {
            throw new IllegalArgumentException("Usuário não está inscrito na atividade");
        }

        activityParticipantRepository.deleteByAtividadeIdAndUsuarioId(activityId, userId);

        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));
        return convertToDTO(activity);
    }

    public List<ActivityDTO> getMyRegistrations(Long userId) {
        List<ActivityParticipantEntity> registrations = activityParticipantRepository.findByUsuarioId(userId);

        Map<Long, ActivityDTO> byId = new LinkedHashMap<>();
        for (ActivityParticipantEntity registration : registrations) {
            ActivityEntity activity = registration.getAtividade();
            byId.putIfAbsent(activity.getId(), convertToDTO(activity));
        }

        return new ArrayList<>(byId.values());
    }

    public List<ActivityDTO> getMyCreations(Long userId) {
        Map<Long, ActivityDTO> byId = new LinkedHashMap<>();

        activityRepository.findByNarradorId(userId).forEach(a -> byId.put(a.getId(), convertToDTO(a)));
        activityRepository.findByPalestranteId(userId).forEach(a -> byId.put(a.getId(), convertToDTO(a)));

        return new ArrayList<>(byId.values());
    }

    private ActivityDTO convertToDTO(ActivityEntity entity) {
        List<String> tags = new ArrayList<>();
        if (entity.getTags() != null && !entity.getTags().isBlank()) {
            tags = Arrays.stream(entity.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }

        List<Long> participants = activityParticipantRepository.findByAtividadeId(entity.getId()).stream()
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

