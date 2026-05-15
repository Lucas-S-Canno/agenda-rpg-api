package com.agendarpgadmin.api.controllers.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.admin.EventService;
import com.agendarpgadmin.api.services.utils.AuthorizationService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@Observed(name = "admin.event.controller")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @Observed(name = "admin.event.controller.getallevents", contextualName = "http-get-all-events")
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
    @Observed(name = "admin.event.controller.geteventbyid", contextualName = "http-get-event-by-id")
    public ResponseEntity<ResponseDTO<EventDTO>> getEventById(@PathVariable UUID id) {
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
    @Observed(name = "admin.event.controller.createevent", contextualName = "http-create-event")
    public ResponseEntity<ResponseDTO<EventDTO>> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureEventManagementAccess(authorizationHeader);
            UUID creatorUserId = authorizationService.getAuthenticatedUserId(authorizationHeader);
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
    @Observed(name = "admin.event.controller.getmycreatedevents", contextualName = "http-get-my-created-events")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getMyCreatedEvents(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            UUID userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
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
    @Observed(name = "admin.event.controller.updateevent", contextualName = "http-update-event")
    public ResponseEntity<ResponseDTO<EventDTO>> updateEvent(
            @PathVariable UUID id,
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
    @Observed(name = "admin.event.controller.deleteevent", contextualName = "http-delete-event")
    public ResponseEntity<ResponseDTO<String>> deleteEvent(
            @PathVariable UUID id,
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