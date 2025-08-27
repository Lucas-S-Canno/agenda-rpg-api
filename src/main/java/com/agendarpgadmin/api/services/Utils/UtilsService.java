package com.agendarpgadmin.api.services.Utils;

import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UtilsService {

    public UserDTO convertToDTO(UserEntity userEntity) {
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
                userEntity.getTelefoneResponsavel(),
                userEntity.getApelido()
        );
    }

    public UserEntity convertToEntity(UserDTO userDTO) {
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
                userDTO.getTelefoneResponsavel(),
                userDTO.getApelido()
        );
    }

    private boolean isValidTipo(String tipo) {
        List<String> validTipos = Arrays.asList("JGD", "ADM", "NRD", "CRD");
        return validTipos.contains(tipo);
    }

}
