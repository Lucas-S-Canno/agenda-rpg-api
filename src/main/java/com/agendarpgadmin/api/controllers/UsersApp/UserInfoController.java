package com.agendarpgadmin.api.controllers.UsersApp;

import com.agendarpgadmin.api.dtos.NarratorProfileDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.services.UsersApp.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            UserDTO user = userInfoService.findByEmail(email); // Implemente este método no service
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
                    narrator.getNomeCompleto(),
                    narrator.getEmail()
            );
            ResponseDTO<NarratorProfileDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Narrador encontrado: " + narrator.getNomeCompleto(),
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

}
