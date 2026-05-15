package com.agendarpgadmin.api.controllers.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.services.admin.UserService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Observed(name = "admin.user.controller")
public class UserController {

    @Autowired
    private UserService userService;
//    New Endpoints

   @PostMapping("/validate-admin")
   @Observed(name = "admin.user.controller.validateadmin", contextualName = "http-validate-admin")
   public ResponseEntity<ResponseDTO<Boolean>> validateAdminUser(@RequestHeader("Authorization") String token) {
       try {
           UserDTO validatedUser = userService.validateAdminUser(token);
           ResponseDTO<Boolean> response = new ResponseDTO<>(
                   HttpStatus.OK.value(),
                   HttpStatus.OK.getReasonPhrase(),
                   Boolean.TRUE
           );
           return ResponseEntity.ok(response);
       } catch (Exception e) {
           ResponseDTO<Boolean> response = new ResponseDTO<>(
                   HttpStatus.UNAUTHORIZED.value(),
                   "Usuário não autorizado ou não é administrador",
                   Boolean.FALSE
           );
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
       }
   }

    @GetMapping("/search")
    @Observed(name = "admin.user.controller.search", contextualName = "http-search-users")
    public ResponseEntity<ResponseDTO<Page<UserDTO>>> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tipos, // ex: "ADM,CRD"
            @RequestParam(required = false) String menor, // "S" ou "N"
            @RequestParam(defaultValue = "nomeCompleto") String sort, // nomeCompleto|email|apelido
            @RequestParam(defaultValue = "asc") String dir, // asc|desc
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Validar se o usuário é admin ou coordenador
            userService.validateAdminUser(token);

            List<String> tiposList = tipos == null || tipos.isBlank()
                    ? null
                    : Arrays.stream(tipos.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            Page<UserDTO> pageResult = userService.searchUsers(page, size, tiposList, menor, sort, dir);

            ResponseDTO<Page<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    pageResult
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<Page<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso",
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<Page<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Token inválido ou usuário não encontrado",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ResponseDTO<Page<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @Observed(name = "admin.user.controller.update", contextualName = "http-update-user")
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Validar se o usuário é admin ou coordenador
            userService.validateAdminUser(token);

            UserDTO updatedUser = userService.update(id, userDTO);
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    updatedUser
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso",
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Token inválido ou usuário não encontrado",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Observed(name = "admin.user.controller.delete", contextualName = "http-delete-user")
    public ResponseEntity<ResponseDTO<String>> deleteEvent(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Validar se o usuário é admin ou coordenador
            userService.validateAdminUser(token);

            UserDTO user = userService.findById(id);
            userService.delete(id);
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Usuário deletado com sucesso: " + user.getNomeCompleto() + " (ID: " + user.getId() + " | email: " + user.getEmail() + ")"
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso",
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Token inválido ou usuário não encontrado",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    @Observed(name = "admin.user.controller.create", contextualName = "http-create-user")
    public ResponseEntity<ResponseDTO<UserDTO>> createUser(
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Validar se o usuário é admin ou coordenador
            userService.validateAdminUser(token);

            UserDTO createdUser = userService.createUser(userDTO);
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    createdUser);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso",
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Token inválido ou usuário não encontrado",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    OLD

    @GetMapping
    @Observed(name = "admin.user.controller.getall", contextualName = "http-get-all-users")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers(
            @RequestHeader("Authorization") String token
    ) {
        try {
            userService.validateAdminUser(token);

            List<UserDTO> users = userService.getAllUsers();
            ResponseDTO<List<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<List<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}