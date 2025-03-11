package com.agendarpgadmin.api.services;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import org.apache.catalina.User;
import org.apache.coyote.Response;
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

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.map(this::convertToDTO).orElse(null);
    }

    public List<UserDTO> getUsersByTipos(List<String> tipos) {
        return userRepository.findByTipoIn(tipos).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = convertToEntity(userDTO);
        userEntity = userRepository.save(userEntity);
        return convertToDTO(userEntity);
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
        UserEntity userEntity = convertToEntity(user);
        userEntity.setId(id);
        userEntity = userRepository.save(userEntity);
        return convertToDTO(userEntity);
    }

    private UserDTO convertToDTO(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getNomeCompleto(),
                userEntity.getDataDeNascimento(),
                userEntity.getTipo(),
                userEntity.getTelefone(),
                userEntity.getMenor(),
                userEntity.getResponsavel(),
                userEntity.getTelefoneResponsavel()
        );
    }

    private UserEntity convertToEntity(UserDTO userDTO) {
        if (!isValidTipo(userDTO.getTipo())) {
            throw new IllegalArgumentException("Tipo inválido: " + userDTO.getTipo());
        }
        return new UserEntity(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getNomeCompleto(),
                userDTO.getDataDeNascimento(),
                userDTO.getTipo(),
                userDTO.getTelefone(),
                userDTO.getMenor(),
                userDTO.getResponsavel(),
                userDTO.getTelefoneResponsavel()
        );
    }

    private boolean isValidTipo(String tipo) {
        List<String> validTipos = Arrays.asList("JGD", "ADM", "NRD", "CRD");
        return validTipos.contains(tipo);
    }
}