package com.agendarpgadmin.api.controllers.publics;

import com.agendarpgadmin.api.dtos.ResendVerificationRequestDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.Public.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/email-validation")
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDTO<String>> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificationService.verifyByToken(token);
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Email verificado com sucesso",
                    "Sua conta foi ativada e você já pode fazer login"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Token inválido",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ResponseDTO<String>> resendVerification(@RequestBody ResendVerificationRequestDTO request) {
        try {
            emailVerificationService.resendVerificationLink(request.getEmail());
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    "Link de verificação reenviado",
                    "Verifique sua caixa de entrada"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Email não encontrado",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro interno do servidor",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
