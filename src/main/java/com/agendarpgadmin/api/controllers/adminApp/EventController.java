package com.agendarpgadmin.api.controllers.adminApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.AdminApp.EventService;
import com.agendarpgadmin.api.services.Utils.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getAllEvents() {
        try {
            List<EventDTO> events = eventService.findAll();
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

    @PostMapping
    public ResponseEntity<ResponseDTO<EventDTO>> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureEventManagementAccess(authorizationHeader);
            Long creatorUserId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            EventDTO createdEvent = eventService.create(eventDTO, creatorUserId);
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    createdEvent
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/my-created")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getMyCreatedEvents(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            Long userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            List<EventDTO> events = eventService.findByCreatorUserId(userId);
            ResponseDTO<List<EventDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    events
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<List<EventDTO>> response = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ResponseDTO<List<EventDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureEventManagementAccess(authorizationHeader);
            EventDTO updatedEvent = eventService.update(id, eventDTO);
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    updatedEvent
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseDTO<EventDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureEventManagementAccess(authorizationHeader);
            EventDTO event = eventService.findById(id);
            eventService.delete(id);
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Evento deletado com sucesso: " + (event == null ? id : event.getNome()) + " (ID: " + id + ")"
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}
