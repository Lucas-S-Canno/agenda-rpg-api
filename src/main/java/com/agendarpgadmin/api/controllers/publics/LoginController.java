package com.agendarpgadmin.api.controllers.publics;

import com.agendarpgadmin.api.dtos.LoginDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.services.AdminApp.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            String token = loginService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword());
            if (token != null) {
                ResponseDTO<String> res = new ResponseDTO<>(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        token
                );
                return ResponseEntity.ok(res);
            } else {
                ResponseDTO<String> res = new ResponseDTO<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Credenciais inválidas"
                );
                return ResponseEntity.status(401).body(res);
            }
        } catch (IllegalStateException e) {
            ResponseDTO<String> res = new ResponseDTO<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Email não verificado",
                    "Verifique sua caixa de entrada ou solicite um novo link de verificação"
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }
    }

}
