package com.agendarpgadmin.api.controllers.publics;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.LoginDTO;
import com.agendarpgadmin.api.dtos.LoginResponseDTO;
import com.agendarpgadmin.api.dtos.RefreshTokenDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.admin.LoginService;
import com.agendarpgadmin.api.services.utils.JwtService;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Observed(name = "public.login.controller")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    @Observed(name = "public.login.controller.login", contextualName = "http-login-user")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO tokens = loginService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword());
            if (tokens != null) {
                ResponseDTO<LoginResponseDTO> res = new ResponseDTO<>(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        tokens
                );
                return ResponseEntity.ok(res);
            } else {
                ResponseDTO<LoginResponseDTO> res = new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        null
                );
                return ResponseEntity.status(401).body(res);
            }
        } catch (IllegalStateException e) {
            ResponseDTO<LoginResponseDTO> res = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Email não verificado",
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }
    }

    @PostMapping("/refresh")
    @Observed(name = "public.login.controller.refresh", contextualName = "http-refresh-token")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginResponseDTO newTokens = jwtService.refreshTokens(refreshTokenDTO.getRefreshToken());
        if (newTokens != null) {
            ResponseDTO<LoginResponseDTO> res = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    newTokens
            );
            return ResponseEntity.ok(res);
        } else {
            ResponseDTO<LoginResponseDTO> res = new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Refresh token inválido ou expirado",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }

    @PostMapping("/logout")
    @Observed(name = "public.login.controller.logout", contextualName = "http-logout-user")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.getEmailFromToken(token);
            if (email != null) {
                jwtService.revokeAllUserTokens(email);
            }
            jwtService.revokeAccessToken(token);
        }
        
        ResponseDTO<Void> res = new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Logout realizado com sucesso",
                null
        );
        return ResponseEntity.ok(res);
    }

}