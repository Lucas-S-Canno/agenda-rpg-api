package com.agendarpgadmin.api.services.user;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.admin.EventService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Observed(name = "user.event.service")
public class UserAppEventService {

    @Autowired
    private EventService eventService;

    @Observed(name = "user.event.getbyid", contextualName = "user-get-event-by-id")
    public ResponseDTO<EventDTO> getEventById(UUID id) {
        EventDTO eventDTO = eventService.findById(id);
        if (eventDTO != null) {
            return new ResponseDTO<>(HttpStatus.OK.value(), "Evento encontrado com sucesso", eventDTO);
        }

        return new ResponseDTO<>(
                HttpStatus.NOT_FOUND.value(),
                "Evento não encontrado",
                null
        );
    }

    @Observed(name = "user.event.getmyevents", contextualName = "user-get-my-events")
    public List<EventDTO> getMyEvents(UUID userId) {
        return Collections.emptyList();
    }

    @Observed(name = "user.event.create", contextualName = "user-create-event-legacy")
    public EventDTO createEvent(EventDTO eventDTO) {
        throw new UnsupportedOperationException("Criação de evento via user-app/event foi descontinuada. Use /events.");
    }

    @Observed(name = "user.event.update", contextualName = "user-update-event-legacy")
    public EventDTO updateEvent(UUID id, EventDTO eventDTO) {
        throw new UnsupportedOperationException("Atualização de evento via user-app/event foi descontinuada. Use /events/{id}.");
    }

    @Observed(name = "user.event.register", contextualName = "user-register-event-legacy")
    public EventDTO registerPlayerInEvent(UUID eventId, UUID playerId) {
        throw new UnsupportedOperationException("Inscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    @Observed(name = "user.event.unregister", contextualName = "user-unregister-event-legacy")
    public EventDTO unregisterPlayerFromEvent(UUID id, UUID userId) {
        throw new UnsupportedOperationException("Desinscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    @Observed(name = "user.event.getregistered", contextualName = "user-get-registered-events")
    public List<EventDTO> getRegisteredEvents(UUID userId) {
        return Collections.emptyList();
    }

    @Observed(name = "user.event.getupcomingmy", contextualName = "user-get-upcoming-my-events")
    public List<EventDTO> getUpcomingMyEvents(UUID userId) {
        return Collections.emptyList();
    }

    @Observed(name = "user.event.getupcomingregistered", contextualName = "user-get-upcoming-registered-events")
    public List<EventDTO> getUpcomingRegisteredEvents(UUID userId) {
        return Collections.emptyList();
    }
}