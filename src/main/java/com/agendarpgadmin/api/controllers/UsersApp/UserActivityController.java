package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.ActivityDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.UsersApp.UserAppActivityService;
import com.agendarpgadmin.api.services.Utils.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserActivityController {

    @Autowired
    private UserAppActivityService userAppActivityService;

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/activities/{id}/register")
    public ResponseEntity<ResponseDTO<ActivityDTO>> register(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            log.info("Register endpoint called. activityId={}", id);
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            Long userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            log.info("Authenticated user for register. activityId={}, userId={}", id, userId);
            ActivityDTO activity = userAppActivityService.register(id, userId);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Registro realizado com sucesso",
                    activity
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Register validation error. activityId={}, message={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (IllegalStateException e) {
            log.warn("Register state conflict. activityId={}, message={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(HttpStatus.CONFLICT.value(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error while registering user in activity. activityId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/activities/{id}/register")
    public ResponseEntity<ResponseDTO<ActivityDTO>> unregister(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            log.info("Unregister endpoint called. activityId={}", id);
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            Long userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            log.info("Authenticated user for unregister. activityId={}, userId={}", id, userId);
            ActivityDTO activity = userAppActivityService.unregister(id, userId);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Desregistro realizado com sucesso",
                    activity
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Unregister validation error. activityId={}, message={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error while unregistering user from activity. activityId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @GetMapping("/user-app/activities/my-registrations")
    public ResponseEntity<ResponseDTO<List<ActivityDTO>>> myRegistrations(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            Long userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            List<ActivityDTO> data = userAppActivityService.getMyRegistrations(userId);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    data
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }

    @GetMapping("/user-app/activities/my-creations")
    public ResponseEntity<ResponseDTO<List<ActivityDTO>>> myCreations(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            authorizationService.ensureAnyAuthenticated(authorizationHeader);
            Long userId = authorizationService.getAuthenticatedUserId(authorizationHeader);
            List<ActivityDTO> data = userAppActivityService.getMyCreations(userId);
            return ResponseEntity.ok(new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    data
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", null));
        }
    }
}


