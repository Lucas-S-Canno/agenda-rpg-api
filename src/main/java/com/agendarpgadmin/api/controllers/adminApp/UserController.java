package com.agendarpgadmin.api.controllers.adminApp;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.services.AdminApp.UserService;
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
public class UserController {

    @Autowired
    private UserService userService;
//    New Endpoints

   @PostMapping("/validate-admin")
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
    public ResponseEntity<ResponseDTO<Page<UserDTO>>> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String tipos, // ex: "ADM,CRD"
            @RequestParam(required = false) String menor, // "S" ou "N"
            @RequestParam(defaultValue = "nomeCompleto") String sort, // nomeCompleto|email|apelido
            @RequestParam(defaultValue = "asc") String dir // asc|desc
    ) {
        try {
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
        } catch (Exception e) {
            ResponseDTO<Page<UserDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    OLD

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        try {
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

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.findById(id);
            ResponseDTO<UserDTO> response = userService.getUserById(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    createdUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.update(id, userDTO);
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    updatedUser
            );
            return ResponseEntity.ok(response);
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
    public ResponseEntity<ResponseDTO<String>> deleteEvent(@PathVariable Long id) {
        try {
            UserDTO user = userService.findById(id);
            userService.delete(id);
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    "Usuário deletado com sucesso: " + user.getNomeCompleto() + " (ID: " + user.getId() + " | email: " + user.getEmail() + ")"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/narradores")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getNarradores() {
        try {
            List<String> tipos = Arrays.asList("NRD", "ADM", "CRD");
            List<UserDTO> users = userService.getUsersByTipos(tipos);
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