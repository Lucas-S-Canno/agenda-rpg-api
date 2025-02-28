package com.agendarpgadmin.api.services;

import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = convertToEntity(userDTO);
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