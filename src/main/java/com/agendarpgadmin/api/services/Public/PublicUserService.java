package com.agendarpgadmin.api.services.Public;

import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.Utils.UtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class PublicUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Validar dados obrigatórios antes de criar o usuário
        validateUserData(userDTO);

        // Verificar se email já existe
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        UserEntity userEntity = utilsService.convertToEntity(userDTO);
        userEntity.setEmailVerified(false); // Garantir que começa como não verificado
        userEntity = userRepository.save(userEntity);

        // Criar e enviar token de verificação
        emailVerificationService.createAndSendVerificationLink(userEntity.getId());

        return utilsService.convertToDTO(userEntity);
    }

    public UserDTO findById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.map(utilsService::convertToDTO).orElse(null);
    }

    private void validateUserData(UserDTO userDTO) {
        // Validar campos obrigatórios
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (userDTO.getNomeCompleto() == null || userDTO.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }
        if (userDTO.getDataDeNascimento() == null || userDTO.getDataDeNascimento().trim().isEmpty()) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }
        if (userDTO.getTipo() == null || userDTO.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo é obrigatório");
        }
        if (userDTO.getTelefone() == null || userDTO.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }
        if (userDTO.getApelido() == null || userDTO.getApelido().trim().isEmpty()) {
            throw new IllegalArgumentException("Apelido é obrigatório");
        }
        if (userDTO.getMenor() == null || userDTO.getMenor().trim().isEmpty()) {
            throw new IllegalArgumentException("Informação sobre menor de idade é obrigatória");
        }

        // Validar campos condicionais para menores de idade
        if ("true".equalsIgnoreCase(userDTO.getMenor())) {
            if (userDTO.getResponsavel() == null || userDTO.getResponsavel().trim().isEmpty()) {
                throw new IllegalArgumentException("Responsável é obrigatório para menores de idade");
            }
            if (userDTO.getTelefoneResponsavel() == null || userDTO.getTelefoneResponsavel().trim().isEmpty()) {
                throw new IllegalArgumentException("Telefone do responsável é obrigatório para menores de idade");
            }
        }

        // Validar formato de email
        if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }

        // Validar data de nascimento e consistência com campo menor
        try {
            LocalDate birthDate = LocalDate.parse(userDTO.getDataDeNascimento());
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            boolean isMinor = age < 18;

            if (isMinor && !"true".equalsIgnoreCase(userDTO.getMenor())) {
                throw new IllegalArgumentException("Usuário é menor de idade mas campo 'menor' não está marcado como true");
            }
            if (!isMinor && "true".equalsIgnoreCase(userDTO.getMenor())) {
                throw new IllegalArgumentException("Usuário é maior de idade mas campo 'menor' está marcado como true");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
    }
}
