package com.agendarpgadmin.api.controllers.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ActivityDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.admin.ActivityService;
import com.agendarpgadmin.api.services.utils.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/events/{eventId}/activities")
    public ResponseEntity<ResponseDTO<List<ActivityDTO>>> getActivitiesByEvent(@PathVariable UUID eventId) {
        try {
            List<ActivityDTO> activities = activityService.getByEventId(eventId);
            ResponseDTO<List<ActivityDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    activities
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<List<ActivityDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/events/{eventId}/activities")
    public ResponseEntity<ResponseDTO<ActivityDTO>> createActivity(
            @PathVariable UUID eventId,
            @RequestBody ActivityDTO dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureActivityManagementAccess(authorizationHeader);
            ActivityDTO created = activityService.create(eventId, dto);
            ResponseDTO<ActivityDTO> response = new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    created
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<ResponseDTO<ActivityDTO>> getActivityById(@PathVariable UUID id) {
        try {
            ActivityDTO activity = activityService.findById(id);
            if (activity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), "Atividade não encontrada", null));
            }

            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    activity
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<ResponseDTO<ActivityDTO>> updateActivity(
            @PathVariable UUID id,
            @RequestBody ActivityDTO dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureActivityManagementAccess(authorizationHeader);
            ActivityDTO updated = activityService.update(id, dto);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    updated
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteActivity(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureActivityManagementAccess(authorizationHeader);
            activityService.delete(id);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Atividade removida com sucesso"
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }
}

