package com.agendarpgadmin.api.controllers.publics;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.AdminApp.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/event")
public class PublicEventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getAllEvents() {
        try {
            List<EventDTO> events = eventService.getUpcomingEvents();
            ResponseDTO<List<EventDTO>> response = eventService.getAllEventsListResponseDTO(events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<List<EventDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> getEventById(@PathVariable Long id) {
        try {
            EventDTO event = eventService.findById(id);
            ResponseDTO<EventDTO> response = eventService.getEventByIdResponseDTO(event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

}
