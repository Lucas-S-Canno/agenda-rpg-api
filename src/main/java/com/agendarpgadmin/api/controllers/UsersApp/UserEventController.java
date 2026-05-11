package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.AdminApp.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-app/event")
public class UserEventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.findById(id);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), "Evento não encontrado", null));
        }
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Evento encontrado com sucesso", event));
    }

    @GetMapping("/my-events")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getMyEvents() {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Use /user-app/activities/my-creations.",
                        null
                )
        );
    }

    @GetMapping("/registered-events")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getRegisteredEvents() {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Use /user-app/activities/my-registrations.",
                        null
                )
        );
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<EventDTO>> createEvent() {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Criação de evento deve usar /events.",
                        null
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> updateEvent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Atualização de evento deve usar /events/{id}.",
                        null
                )
        );
    }

    @PatchMapping("/{id}/register")
    public ResponseEntity<ResponseDTO<EventDTO>> registerForEvent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Use /user-app/activities/{id}/register.",
                        null
                )
        );
    }

    @PatchMapping("/{id}/unregister")
    public ResponseEntity<ResponseDTO<EventDTO>> unregisterFromEvent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ResponseDTO<>(
                        HttpStatus.GONE.value(),
                        "Endpoint descontinuado. Use DELETE /user-app/activities/{id}/register.",
                        null
                )
        );
    }

}