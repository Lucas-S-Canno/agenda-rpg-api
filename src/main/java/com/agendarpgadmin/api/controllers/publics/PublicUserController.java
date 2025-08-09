package com.agendarpgadmin.api.controllers.publics;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
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
}
