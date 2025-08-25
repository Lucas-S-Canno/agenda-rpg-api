package com.agendarpgadmin.api.controllers.publics;

import com.agendarpgadmin.api.dtos.*;
import com.agendarpgadmin.api.services.Public.PublicPasswordResetService;
import com.agendarpgadmin.api.services.Public.PublicUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public/user")
public class PublicUserController {

    @Autowired
    private PublicUserService publicUserService;

    @Autowired
    private PublicPasswordResetService passwordResetService;

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO<String>> testEndpoint() {
        ResponseDTO<String> response = new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Public User Endpoint is working",
                "Test successful"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = publicUserService.createUser(userDTO);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<PasswordResetRequestDTO>> requestResetCode(@RequestBody PasswordResetRequestDTO requestDTO) {
        try {
            passwordResetService.requestResetCode(requestDTO.getEmail());

            ResponseDTO<PasswordResetRequestDTO> response = new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Código de redefinição enviado",
                requestDTO
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            ResponseDTO<PasswordResetRequestDTO> response = new ResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() != null ? ex.getMessage() : "Requisição inválida",
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception ex) {
            ResponseDTO<PasswordResetRequestDTO> response = new ResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao enviar código de redefinição",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/validate-reset-code")
    public ResponseEntity<ResponseDTO<String>> validateResetCode(@RequestBody PasswordResetCodeDTO dto) {
        try {
            String token = passwordResetService.validateResetCode(dto.getEmail(), dto.getCode());
            ResponseDTO<String> response = new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Código validado com sucesso",
                token
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                "Código inválido ou expirado",
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<Boolean>> resetPassword(@RequestBody PasswordResetDTO dto) {
        try {
            passwordResetService.resetPassword(dto.getEmail(), dto.getNewPassword(), dto.getResetToken());
            ResponseDTO<Boolean> response = new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Senha alterada com sucesso",
                true
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<Boolean> response = new ResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                "Não foi possível alterar a senha",
                false
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
