package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.EventDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.UsersApp.UserAppEventService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-app/event")
public class UserEventController {

    private final String SECRET_KEY = "secretaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Autowired
    private UserAppEventService userAppEventService;

    @PostMapping
    public ResponseEntity<ResponseDTO<EventDTO>> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar e extrair o token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Token não fornecido", null)
                );
            }

            String token = authorizationHeader.substring(7);

            // Decodificar o token e verificar o tipo de usuário
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            String userType = claims.get("tipo", String.class);

            // Verificar se o usuário é do tipo JGD (não autorizado)
            if ("JGD".equals(userType)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Usuário não pode criar evento", null)
                );
            }

            // Criar o evento
            EventDTO createdEvent = userAppEventService.createEvent(eventDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseDTO<>(HttpStatus.CREATED.value(), "Evento criado com sucesso", createdEvent)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Erro ao criar evento: " + e.getMessage(), null)
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
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Token não fornecido", null)
                );
            }

            String token = authorizationHeader.substring(7);

            // Decodificar o token e verificar o tipo de usuário
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            String userType = claims.get("tipo", String.class);

            // Verificar se o usuário é do tipo JGD (não autorizado)
            if ("JGD".equals(userType)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Usuário não pode atualizar evento", null)
                );
            }

            // Atualizar o evento
            EventDTO updatedEvent = userAppEventService.updateEvent(id, eventDTO);

            if (updatedEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), "Evento não encontrado", null)
                );
            }

            return ResponseEntity.ok(
                    new ResponseDTO<>(HttpStatus.OK.value(), "Evento atualizado com sucesso", updatedEvent)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Erro ao atualizar evento: " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<EventDTO>> getEventById(@PathVariable Long id) {
        ResponseDTO<EventDTO> response = userAppEventService.getEventById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}