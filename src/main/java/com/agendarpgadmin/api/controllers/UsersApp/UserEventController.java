package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.UsersApp.UserAppEventService;
import com.agendarpgadmin.api.services.Utils.ConstantUtilsService;
import com.agendarpgadmin.api.services.Utils.JwtUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user-app/event")
public class UserEventController {

    @Autowired
    private UserAppEventService userAppEventService;
    
    @Autowired
    private JwtUtilsService jwtService;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> getEventById(@PathVariable Long id) {
        ResponseDTO<EventDTO> response = userAppEventService.getEventById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/my-events")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getMyEvents(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Token não fornecido",
                                null
                        )
                );
            }

            if(
                    jwtService.checkIfUserIsDeterminedType(
                            authorizationHeader.substring(7),
                            ConstantUtilsService.USER_TYPE_PLAYER
                    )
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Usuário não pode acessar esses eventos",
                                null
                        )
                );
            }

            List<EventDTO> myEvents = userAppEventService.getMyEvents(
                    jwtService.getUserIdFromToken(authorizationHeader.substring(7))
            );

            return ResponseEntity.ok(
                    new ResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Eventos obtidos com sucesso",
                            myEvents
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Erro ao obter eventos: " + e.getMessage(),
                            null
                    )
            );
        }
    }

    @GetMapping("/registered-events")
    public ResponseEntity<ResponseDTO<List<EventDTO>>> getRegisteredEvents(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Token não fornecido",
                                null
                        )
                );
            }

            String userId = jwtService.getUserIdFromToken(authorizationHeader.substring(7));
            List<EventDTO> registeredEvents = userAppEventService.getRegisteredEvents(userId);

            return ResponseEntity.ok(
                    new ResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Eventos registrados obtidos com sucesso",
                            registeredEvents
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Erro ao obter eventos registrados: " + e.getMessage(),
                            null
                    )
            );
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<EventDTO>> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (
                    authorizationHeader == null ||
                    !authorizationHeader.startsWith("Bearer ")
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Token não fornecido",
                        null
                    )
                );
            }

            if(
                jwtService.checkIfUserIsDeterminedType(
                    authorizationHeader.substring(7),
                    ConstantUtilsService.USER_TYPE_PLAYER
                )
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Usuário não pode criar eventos",
                        null
                    )
                );
            }

            // Criar o evento
            EventDTO createdEvent = userAppEventService.createEvent(eventDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    "Evento criado com sucesso",
                    createdEvent
                )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao criar evento: " + e.getMessage(),
                    null
                )
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (
                    authorizationHeader == null ||
                    !authorizationHeader.startsWith("Bearer ")
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Token não fornecido",
                        null
                    )
                );
            }

            if(
                jwtService.checkIfUserIsDeterminedType(
                    authorizationHeader.substring(7),
                    ConstantUtilsService.USER_TYPE_PLAYER
                )
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Usuário não pode atualizar eventos",
                        null
                    )
                );
            }

            // Atualizar o evento
            EventDTO updatedEvent = userAppEventService.updateEvent(id, eventDTO);

            if (updatedEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseDTO<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Evento não encontrado",
                        null
                    )
                );
            }

            return ResponseEntity.ok(
                new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Evento atualizado com sucesso",
                    updatedEvent
                )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao atualizar evento: " + e.getMessage(),
                    null
                )
            );
        }
    }

    @PatchMapping("/{id}/register")
    public ResponseEntity<ResponseDTO<EventDTO>> registerForEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (
                    authorizationHeader == null ||
                    !authorizationHeader.startsWith("Bearer ")
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Token não fornecido",
                        null
                    )
                );
            }

            String userId = jwtService.getUserIdFromToken(authorizationHeader.substring(7));

            EventDTO registeredEvent = userAppEventService.registerPlayerInEvent(id, userId);

            if (registeredEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseDTO<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Evento não encontrado",
                        null
                    )
                );
            }

            return ResponseEntity.ok(
                new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Registro realizado com sucesso",
                    registeredEvent
                )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao registrar no evento: " + e.getMessage(),
                    null
                )
            );
        }
    }

    @PatchMapping("/{id}/unregister")
    public ResponseEntity<ResponseDTO<EventDTO>> unregisterFromEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (
                    authorizationHeader == null ||
                    !authorizationHeader.startsWith("Bearer ")
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Token não fornecido",
                        null
                    )
                );
            }

            String userId = jwtService.getUserIdFromToken(authorizationHeader.substring(7));

            EventDTO unregisteredEvent = userAppEventService.unregisterPlayerFromEvent(id, userId);

            if (unregisteredEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseDTO<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Evento não encontrado",
                        null
                    )
                );
            }

            return ResponseEntity.ok(
                new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Desregistro realizado com sucesso",
                    unregisteredEvent
                )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao desregistrar do evento: " + e.getMessage(),
                    null
                )
            );
        }
    }

}