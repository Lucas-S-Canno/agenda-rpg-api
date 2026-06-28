package com.agendarpgadmin.api.controllers.user;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ChangePasswordDTO;
import com.agendarpgadmin.api.dtos.ChangePasswordWithCodeDTO;
import com.agendarpgadmin.api.dtos.EmailChangeCodeDTO;
import com.agendarpgadmin.api.dtos.EmailChangeConfirmDTO;
import com.agendarpgadmin.api.dtos.PasswordChangeCodeDTO;
import com.agendarpgadmin.api.dtos.NarratorProfileDTO;
import com.agendarpgadmin.api.dtos.NarratorSimpleDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.exceptions.EmailAlreadyInUseException;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.services.user.EmailChangeVerificationService;
import com.agendarpgadmin.api.services.user.PasswordChangeVerificationService;
import com.agendarpgadmin.api.services.user.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-app/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PasswordChangeVerificationService passwordChangeVerificationService;

    @Autowired
    private EmailChangeVerificationService emailChangeVerificationService;

    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<UserDTO>> getAuthenticatedUser(org.springframework.security.core.Authentication authentication) {
        try {
            String email = authentication.getName();
            UserDTO user = userInfoService.findByEmail(email);
            ResponseDTO<UserDTO> response = userInfoService.getUserById(user);
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

    @GetMapping("/narrator-name/{narratorId}")
    public ResponseEntity<ResponseDTO<NarratorProfileDTO>> getNarratorProfile(
            @PathVariable UUID narratorId
    ) {
        try {
            UserDTO narrator = userInfoService.findById(narratorId);
            NarratorProfileDTO profile = new NarratorProfileDTO(
                    narrator.getId(),
                    narrator.getApelido(),
                    narrator.getEmail()
            );
            ResponseDTO<NarratorProfileDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Narrador encontrado: " + narrator.getApelido(),
                    profile
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<NarratorProfileDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/narrators")
    public ResponseEntity<ResponseDTO<List<NarratorSimpleDTO>>> getNarratorsSimple() {
        try {
            List<NarratorSimpleDTO> narrators = userInfoService.getNarratorsSimple();
            ResponseDTO<List<NarratorSimpleDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Lista de usuarios NRD, CRD e ADM retornada com sucesso",
                    narrators
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<List<NarratorSimpleDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<ResponseDTO<UserDTO>> updateProfile(
            @PathVariable UUID userId,
            @RequestBody UserDTO userDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            UserDTO updatedUser = userInfoService.updateProfileWithValidation(authenticatedEmail, userId, userDTO);

            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Perfil atualizado com sucesso",
                    updatedUser
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<UserDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDTO<String>> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            userInfoService.changePassword(authenticatedEmail, changePasswordDTO);

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Senha alterada com sucesso",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e instanceof EmailAlreadyInUseException) {
                ResponseDTO<String> response = new ResponseDTO<>(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        null
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/change-password/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmChangePassword(
            @RequestBody ChangePasswordWithCodeDTO changePasswordDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            userInfoService.changePasswordWithCode(authenticatedEmail, changePasswordDTO);

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Senha alterada com sucesso",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Fluxo de troca de e-mail autenticada (3 etapas)
    // ──────────────────────────────────────────────────────────────────────────

    @PostMapping("/change-email/request-code")
    public ResponseEntity<ResponseDTO<String>> requestEmailChangeCode(
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            emailChangeVerificationService.requestCode(authenticatedEmail);

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Código enviado para o e-mail cadastrado",
                    "Código enviado com sucesso"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/change-email/validate-code")
    public ResponseEntity<ResponseDTO<String>> validateEmailChangeCode(
            @RequestBody EmailChangeCodeDTO codeDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            String token = emailChangeVerificationService.validateCode(authenticatedEmail, codeDTO.getCode());

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Código validado com sucesso",
                    token
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/change-email/confirm")
    public ResponseEntity<ResponseDTO<String>> confirmEmailChange(
            @RequestBody EmailChangeConfirmDTO confirmDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            emailChangeVerificationService.confirmEmailChange(
                    authenticatedEmail,
                    confirmDTO.getNovoEmail(),
                    confirmDTO.getTokenVerificacao()
            );

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "E-mail alterado com sucesso",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Fluxo de troca de senha autenticada (3 etapas)
    // ──────────────────────────────────────────────────────────────────────────

    @PostMapping("/change-password/request-code")    public ResponseEntity<ResponseDTO<String>> requestChangePasswordCode(
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            passwordChangeVerificationService.requestCode(authenticatedEmail);

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Código enviado para o e-mail cadastrado",
                    "Código enviado com sucesso"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/change-password/validate-code")
    public ResponseEntity<ResponseDTO<String>> validateChangePasswordCode(
            @RequestBody PasswordChangeCodeDTO codeDTO,
            org.springframework.security.core.Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            String token = passwordChangeVerificationService.validateCode(authenticatedEmail, codeDTO.getCode());

            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Código validado com sucesso",
                    token
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

}
