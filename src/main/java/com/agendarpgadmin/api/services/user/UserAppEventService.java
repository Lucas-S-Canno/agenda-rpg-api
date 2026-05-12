package com.agendarpgadmin.api.services.user;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.admin.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserAppEventService {

    @Autowired
    private EventService eventService;

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

    public List<EventDTO> getMyEvents(UUID userId) {
        return Collections.emptyList();
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        throw new UnsupportedOperationException("Criação de evento via user-app/event foi descontinuada. Use /events.");
    }

    public EventDTO updateEvent(UUID id, EventDTO eventDTO) {
        throw new UnsupportedOperationException("Atualização de evento via user-app/event foi descontinuada. Use /events/{id}.");
    }

    public EventDTO registerPlayerInEvent(UUID eventId, UUID playerId) {
        throw new UnsupportedOperationException("Inscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    public EventDTO unregisterPlayerFromEvent(UUID id, UUID userId) {
        throw new UnsupportedOperationException("Desinscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    public List<EventDTO> getRegisteredEvents(UUID userId) {
        return Collections.emptyList();
    }

    public List<EventDTO> getUpcomingMyEvents(UUID userId) {
        return Collections.emptyList();
    }

    public List<EventDTO> getUpcomingRegisteredEvents(UUID userId) {
        return Collections.emptyList();
    }
}