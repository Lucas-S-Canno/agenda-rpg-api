package com.agendarpgadmin.api.services.AdminApp;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.Utils.UtilsService;
import com.agendarpgadmin.api.services.Utils.JwtUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private JwtUtilsService jwtUtilsService;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(utilsService::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.map(utilsService::convertToDTO).orElse(null);
    }

    public List<UserDTO> getUsersByTipos(List<String> tipos) {
        return userRepository.findByTipoIn(tipos).stream().map(utilsService::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = utilsService.convertToEntity(userDTO);
        userEntity = userRepository.save(userEntity);
        return utilsService.convertToDTO(userEntity);
    }

    public ResponseDTO<UserDTO> getUserById(UserDTO user) {
        ResponseDTO<UserDTO> response;
        if(user == null) {
            response = new ResponseDTO<>(
                    HttpStatus.NOT_FOUND.value(),
                    "User Not Found",
                    null
            );
        } else {
            response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    user
            );
        }
        return response;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO update(Long id, UserDTO user) {
        UserEntity userEntity = utilsService.convertToEntity(user);
        userEntity.setId(id);
        userEntity = userRepository.save(userEntity);
        return utilsService.convertToDTO(userEntity);
    }

    public UserDTO validateAdminUser(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token is required");
        }

        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        String email = jwtUtilsService.getEmailFromToken(rawToken);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inválido no token");
        }

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserEntity user = userOpt.get();
        String tipo = user.getTipo();

        if (tipo == null || !( "ADM".equals(tipo) || "CRD".equals(tipo) )) {
            throw new SecurityException("User is not ADM or CRD");
        }

        return utilsService.convertToDTO(user);
    }
}