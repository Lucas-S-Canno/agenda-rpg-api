package com.agendarpgadmin.api.services.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.utils.PasswordHashingService;
import com.agendarpgadmin.api.services.utils.UtilsService;
import com.agendarpgadmin.api.services.utils.JwtUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
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

    @Autowired
    private PasswordHashingService passwordHashingService;

    public Page<UserDTO> searchUsers(
            int page,
            int size,
            List<String> tipos,
            String menor,
            String sortField,
            String sortDir
    ) {
        // Normalizar campo de ordenação
        String field;
        if ("email".equalsIgnoreCase(sortField)) {
            field = "email";
        } else if ("apelido".equalsIgnoreCase(sortField)) {
            field = "apelido";
        } else {
            field = "nomeCompleto";
        }

        // Direção da ordenação
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(direction, field));

        // Construir Specification para filtros dinâmicos
        Specification<UserEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por tipos
            if (tipos != null && !tipos.isEmpty()) {
                predicates.add(root.get("tipo").in(tipos));
            }

            // Filtro por menor
            if (menor != null && (menor.equalsIgnoreCase("S") || menor.equalsIgnoreCase("N"))) {
                predicates.add(cb.equal(cb.upper(root.get("menor")), menor.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Buscar página de entidades e converter para DTO
        Page<UserEntity> pageEntity = userRepository.findAll(spec, pageable);
        return pageEntity.map(utilsService::convertToDTO);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(utilsService::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO findById(UUID id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.map(utilsService::convertToDTO).orElse(null);
    }

    public List<UserDTO> getUsersByTipos(List<String> tipos) {
        return userRepository.findByTipoIn(tipos).stream().map(utilsService::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = utilsService.convertToEntity(userDTO);
        userEntity.setPassword(passwordHashingService.hashPassword(userEntity.getPassword()));
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

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public UserDTO update(UUID id, UserDTO user) {
        UserEntity userEntity = utilsService.convertToEntity(user);
        userEntity.setId(id);
        
        Optional<UserEntity> existingUser = userRepository.findById(id);
        if (userEntity.getPassword() != null && !userEntity.getPassword().trim().isEmpty()) {
            userEntity.setPassword(passwordHashingService.hashPassword(userEntity.getPassword()));
        } else if (existingUser.isPresent()) {
            userEntity.setPassword(existingUser.get().getPassword());
        }

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
