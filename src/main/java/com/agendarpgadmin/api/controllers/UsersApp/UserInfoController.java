package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.ChangePasswordDTO;
import com.agendarpgadmin.api.dtos.NarratorProfileDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UpdateProfileDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.services.UsersApp.UserInfoService;
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
            @PathVariable Long narratorId
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

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<ResponseDTO<UserDTO>> updateProfile(
            @PathVariable Long userId,
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
