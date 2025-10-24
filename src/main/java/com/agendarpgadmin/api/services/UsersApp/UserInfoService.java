package com.agendarpgadmin.api.services.UsersApp;

import com.agendarpgadmin.api.dtos.ChangePasswordDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UpdateProfileDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.filters.JwtAuthenticationFilter;
import com.agendarpgadmin.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserInfoService {
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
                userEntity.getTelefoneResponsavel(),
                userEntity.getApelido(),
                userEntity.getEmailVerified()
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
                userDTO.getTelefoneResponsavel(),
                userDTO.getApelido(),
                userDTO.getEmailVerified()
        );
    }

    private boolean isValidTipo(String tipo) {
        List<String> validTipos = Arrays.asList("JGD", "ADM", "NRD", "CRD");
        return validTipos.contains(tipo);
    }

    public UserDTO findByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.map(this::convertToDTO).orElse(null);
    }

    public UserDTO updateProfile(String authenticatedEmail, UpdateProfileDTO updateProfileDTO) {
        // Buscar o usuário atual pelo email autenticado
        Optional<UserEntity> userOptional = userRepository.findByEmail(authenticatedEmail);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UserEntity user = userOptional.get();

        // Validar se email está sendo alterado para um já existente
        if (!user.getEmail().equals(updateProfileDTO.getEmail())) {
            Optional<UserEntity> existingUser = userRepository.findByEmail(updateProfileDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email já está em uso por outro usuário");
            }
        }

        // Atualizar apenas os campos editáveis
        user.setEmail(updateProfileDTO.getEmail());
        user.setApelido(updateProfileDTO.getApelido());
        user.setTelefone(updateProfileDTO.getTelefone());
        user.setResponsavel(updateProfileDTO.getResponsavel());
        user.setTelefoneResponsavel(updateProfileDTO.getTelefoneResponsavel());

        // Salvar as alterações
        user = userRepository.save(user);

        return convertToDTO(user);
    }

    public UserDTO updateProfileWithValidation(String authenticatedEmail, Long userId, UserDTO userDTO) {
        // Buscar o usuário autenticado
        Optional<UserEntity> authenticatedUserOptional = userRepository.findByEmail(authenticatedEmail);
        if (authenticatedUserOptional.isEmpty()) {
            throw new RuntimeException("Usuário autenticado não encontrado");
        }

        // Buscar o usuário que será atualizado
        Optional<UserEntity> targetUserOptional = userRepository.findById(userId);
        if (targetUserOptional.isEmpty()) {
            throw new RuntimeException("Usuário a ser atualizado não encontrado");
        }

        UserEntity authenticatedUser = authenticatedUserOptional.get();
        UserEntity targetUser = targetUserOptional.get();

        // Validar se o usuário pode alterar apenas os próprios dados
        if (!authenticatedUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Você só pode alterar seus próprios dados");
        }

        // Validar se os campos não editáveis não estão sendo alterados
        if (!targetUser.getNomeCompleto().equals(userDTO.getNomeCompleto())) {
            throw new RuntimeException("Nome completo não pode ser alterado");
        }

        if (!targetUser.getDataDeNascimento().equals(userDTO.getDataDeNascimento())) {
            throw new RuntimeException("Data de nascimento não pode ser alterada");
        }

        if (!targetUser.getTipo().equals(userDTO.getTipo())) {
            throw new RuntimeException("Tipo de usuário não pode ser alterado");
        }

        // Validar se email está sendo alterado para um já existente
        if (!targetUser.getEmail().equals(userDTO.getEmail())) {
            Optional<UserEntity> existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email já está em uso por outro usuário");
            }
        }

        // Atualizar apenas os campos editáveis
        targetUser.setEmail(userDTO.getEmail());
        targetUser.setApelido(userDTO.getApelido());
        targetUser.setTelefone(userDTO.getTelefone());
        targetUser.setResponsavel(userDTO.getResponsavel());
        targetUser.setTelefoneResponsavel(userDTO.getTelefoneResponsavel());

        // Manter campos não editáveis (redundante, mas para garantir)
        targetUser.setNomeCompleto(targetUser.getNomeCompleto());
        targetUser.setDataDeNascimento(targetUser.getDataDeNascimento());
        targetUser.setTipo(targetUser.getTipo());

        // Salvar as alterações
        targetUser = userRepository.save(targetUser);

        return convertToDTO(targetUser);
    }

    public UserDTO changePassword(String authenticatedEmail, ChangePasswordDTO changePasswordDTO) {
        // Validar se o email do JWT não é nulo ou vazio
        if (authenticatedEmail == null || authenticatedEmail.trim().isEmpty()) {
            throw new RuntimeException("Token de autenticação inválido");
        }

        // Buscar o usuário autenticado
        Optional<UserEntity> userOptional = userRepository.findByEmail(authenticatedEmail);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UserEntity user = userOptional.get();

        // Validação adicional: verificar se o email do JWT corresponde ao email do usuário
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new RuntimeException("Token de autenticação não corresponde ao usuário");
        }

        // Validar senha atual
        if (!user.getPassword().equals(changePasswordDTO.getSenhaAtual())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Validar se nova senha e confirmação são iguais
        if (!changePasswordDTO.getNovaSenha().equals(changePasswordDTO.getConfirmacaoNovaSenha())) {
            throw new RuntimeException("Nova senha e confirmação não coincidem");
        }

        // Validar se nova senha não é vazia
        if (changePasswordDTO.getNovaSenha() == null || changePasswordDTO.getNovaSenha().trim().isEmpty()) {
            throw new RuntimeException("Nova senha não pode ser vazia");
        }

        // Atualizar a senha
        user.setPassword(changePasswordDTO.getNovaSenha());
        user = userRepository.save(user);

        return convertToDTO(user);
    }
}
