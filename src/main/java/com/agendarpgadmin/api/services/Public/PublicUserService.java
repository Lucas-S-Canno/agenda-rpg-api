package com.agendarpgadmin.api.services.Public;

import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.Utils.UtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UtilsService utilsService;

    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = utilsService.convertToEntity(userDTO);
        userEntity = userRepository.save(userEntity);
        return utilsService.convertToDTO(userEntity);
    }

}
