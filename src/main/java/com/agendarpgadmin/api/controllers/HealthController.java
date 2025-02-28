package com.agendarpgadmin.api.controllers;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseDTO<String> health() {
        ResponseDTO<String> res = new ResponseDTO<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase(),
                "OK"
        );
        return res;
    }

}
