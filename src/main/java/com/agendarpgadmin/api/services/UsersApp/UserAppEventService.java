package com.agendarpgadmin.api.services.UsersApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.AdminApp.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserAppEventService {

    @Autowired
    private EventService eventService;

    public ResponseDTO<EventDTO> getEventById(Long id) {
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

    public List<EventDTO> getMyEvents(String userId) {
        return Collections.emptyList();
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        throw new UnsupportedOperationException("Criação de evento via user-app/event foi descontinuada. Use /events.");
    }

    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        throw new UnsupportedOperationException("Atualização de evento via user-app/event foi descontinuada. Use /events/{id}.");
    }

    public EventDTO registerPlayerInEvent(Long eventId, String playerId) {
        throw new UnsupportedOperationException("Inscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    public EventDTO unregisterPlayerFromEvent(Long id, String userId) {
        throw new UnsupportedOperationException("Desinscrição em evento foi descontinuada. Use /activities/{id}/register.");
    }

    public List<EventDTO> getRegisteredEvents(String userId) {
        return Collections.emptyList();
    }

    public List<EventDTO> getUpcomingMyEvents(String userId) {
        return Collections.emptyList();
    }

    public List<EventDTO> getUpcomingRegisteredEvents(String userId) {
        return Collections.emptyList();
    }
}