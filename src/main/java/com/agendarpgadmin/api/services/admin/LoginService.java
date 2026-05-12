package com.agendarpgadmin.api.services.admin;
import java.util.UUID;

import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.utils.PasswordHashingService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHashingService passwordHashingService;

    private final String SECRET_KEY = "secretaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final long validityInMilliseconds = 86400000; // 1 dia

    public String authenticateUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null && passwordHashingService.verifyPassword(password, user.getPassword())) {
            // Verificar se o email foi verificado
            if (!Boolean.TRUE.equals(user.getEmailVerified())) {
                throw new IllegalStateException("Email não verificado");
            }
            return generateToken(user);
        }
        return null;
    }

    private String generateToken(UserEntity user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("nomeCompleto", user.getNomeCompleto())
                .claim("tipo", user.getTipo())
                .claim("id", user.getId())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
