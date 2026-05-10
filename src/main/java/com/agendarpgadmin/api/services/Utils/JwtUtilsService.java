package com.agendarpgadmin.api.services.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class JwtUtilsService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Boolean checkIfUserIsDeterminedType(String token, String determinedUserType) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String userType = claims.get("tipo", String.class);

        // Verificar se o usuário é do tipo JGD (não autorizado)
        if (determinedUserType.equals(userType)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id", Integer.class).toString();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        Object email = claims.get("sub");
        return email == null ? null : email.toString();
    }

    public String getUserTypeFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("tipo", String.class);
    }

    public boolean isUserOneOfTypes(String token, Set<String> allowedTypes) {
        String userType = getUserTypeFromToken(token);
        return userType != null && allowedTypes.contains(userType);
    }
}
