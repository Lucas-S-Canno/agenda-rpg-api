package com.agendarpgadmin.api.services.user;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ChangePasswordDTO;
import com.agendarpgadmin.api.dtos.ChangePasswordWithCodeDTO;
import com.agendarpgadmin.api.dtos.NarratorSimpleDTO;
import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.UpdateProfileDTO;
import com.agendarpgadmin.api.dtos.UserDTO;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.UserCacheService;
import com.agendarpgadmin.api.services.user.PasswordChangeVerificationService;
import com.agendarpgadmin.api.services.utils.ConstantUtilsService;
import com.agendarpgadmin.api.services.utils.PasswordHashingService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Observed(name = "user.info.service")
public class UserInfoService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHashingService passwordHashingService;

    @Autowired
    private PasswordChangeVerificationService passwordChangeVerificationService;

    @Autowired
    private UserCacheService userCacheService;

    @Observed(name = "user.info.getall", contextualName = "user-get-all-users")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Observed(name = "user.info.findbyid", contextualName = "user-find-user-by-id")
    public UserDTO findById(UUID id) {
        // Cache read-through: tenta o Redis antes de ir ao banco
        UserEntity cached = userCacheService.getCachedUser(id.toString());
        if (cached != null) {
            return convertToDTO(cached);
        }
        Optional<UserEntity> user = userRepository.findById(id);
        user.ifPresent(userCacheService::cacheUser);
        return user.map(this::convertToDTO).orElse(null);
    }

    @Observed(name = "user.info.getbytipos", contextualName = "user-get-users-by-tipos")
    public List<UserDTO> getUsersByTipos(List<String> tipos) {
        return userRepository.findByTipoIn(tipos).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Observed(name = "user.info.narratorsimple", contextualName = "user-get-narrators-simple")
    public List<NarratorSimpleDTO> getNarratorsSimple() {
        return userRepository.findByTipoIn(List.of(
                        ConstantUtilsService.USER_TYPE_MASTER,
                        ConstantUtilsService.USER_TYPE_COORD,
                        ConstantUtilsService.USER_TYPE_ADMIN
                )).stream()
                .map(user -> new NarratorSimpleDTO(user.getId(), user.getNomeCompleto()))
                .collect(Collectors.toList());
    }

    @Observed(name = "user.info.create", contextualName = "user-create-user")
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = convertToEntity(userDTO);
        userEntity.setPassword(passwordHashingService.hashPassword(userEntity.getPassword()));
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

    @Observed(name = "user.info.delete", contextualName = "user-delete-user")
    public void delete(UUID id) {
        userRepository.deleteById(id);
        userCacheService.evictUser(id.toString());
    }

    @Observed(name = "user.info.update", contextualName = "user-update-user")
    public UserDTO update(UUID id, UserDTO user) {
        UserEntity userEntity = convertToEntity(user);
        userEntity.setId(id);
        
        // Se a senha foi enviada na atualização, fazemos o hash, caso contrário, mantemos a antiga
        Optional<UserEntity> existingUser = userRepository.findById(id);
        if (userEntity.getPassword() != null && !userEntity.getPassword().trim().isEmpty()) {
            userEntity.setPassword(passwordHashingService.hashPassword(userEntity.getPassword()));
        } else if (existingUser.isPresent()) {
            userEntity.setPassword(existingUser.get().getPassword());
        }

        userEntity = userRepository.save(userEntity);
        // Evict cache após qualquer mutação — próxima leitura buscará dado atualizado do banco
        userCacheService.evictUser(id.toString());
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

    @Observed(name = "user.info.findbyemail", contextualName = "user-find-user-by-email")
    public UserDTO findByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.map(this::convertToDTO).orElse(null);
    }

    @Observed(name = "user.info.updateprofile", contextualName = "user-update-profile")
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
        userCacheService.evictUser(user.getId().toString());

        return convertToDTO(user);
    }

    @Observed(name = "user.info.updateprofilevalidation", contextualName = "user-update-profile-with-validation")
    public UserDTO updateProfileWithValidation(String authenticatedEmail, UUID userId, UserDTO userDTO) {
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
        userCacheService.evictUser(targetUser.getId().toString());

        return convertToDTO(targetUser);
    }

    @Observed(name = "user.info.changepassword", contextualName = "user-change-password")
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
        if (!passwordHashingService.verifyPassword(changePasswordDTO.getSenhaAtual(), user.getPassword())) {
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

        // Atualizar a senha com o hash seguro
        user.setPassword(passwordHashingService.hashPassword(changePasswordDTO.getNovaSenha()));
        user = userRepository.save(user);
        // Evict cache: senha alterada → dados em cache estão desatualizados
        userCacheService.evictUser(user.getId().toString());

        return convertToDTO(user);
    }

    @Observed(name = "user.info.changepassword.withcode", contextualName = "user-change-password-with-code")
    public UserDTO changePasswordWithCode(String authenticatedEmail, ChangePasswordWithCodeDTO changePasswordDTO) {
        if (authenticatedEmail == null || authenticatedEmail.trim().isEmpty()) {
            throw new RuntimeException("Token de autenticação inválido");
        }

        Optional<UserEntity> userOptional = userRepository.findByEmail(authenticatedEmail);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UserEntity user = userOptional.get();

        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new RuntimeException("Token de autenticação não corresponde ao usuário");
        }

        if (changePasswordDTO.getTokenVerificacao() == null || changePasswordDTO.getTokenVerificacao().trim().isEmpty()) {
            throw new RuntimeException("Token de validação não informado");
        }

        passwordChangeVerificationService.assertValidatedToken(authenticatedEmail, changePasswordDTO.getTokenVerificacao());

        if (changePasswordDTO.getNovaSenha() == null || changePasswordDTO.getNovaSenha().trim().isEmpty()) {
            throw new RuntimeException("Nova senha não pode ser vazia");
        }

        if (!changePasswordDTO.getNovaSenha().equals(changePasswordDTO.getConfirmacaoNovaSenha())) {
            throw new RuntimeException("Nova senha e confirmação não coincidem");
        }

        user.setPassword(passwordHashingService.hashPassword(changePasswordDTO.getNovaSenha()));
        user = userRepository.save(user);
        passwordChangeVerificationService.consumeValidatedToken(authenticatedEmail, changePasswordDTO.getTokenVerificacao());
        userCacheService.evictUser(user.getId().toString());

        return convertToDTO(user);
    }
}