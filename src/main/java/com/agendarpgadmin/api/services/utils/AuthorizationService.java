package com.agendarpgadmin.api.services.utils;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorizationService {

    @Autowired
    private JwtUtilsService jwtUtilsService;

    public String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token não fornecido");
        }
        return authorizationHeader.substring(7);
    }

    public UUID getAuthenticatedUserId(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        return UUID.fromString(jwtUtilsService.getUserIdFromToken(token));
    }

    public void ensureEventManagementAccess(String authorizationHeader) {
        ensureRole(authorizationHeader, Set.of(
                ConstantUtilsService.USER_TYPE_ADMIN,
                ConstantUtilsService.USER_TYPE_COORD
        ));
    }

    public void ensureActivityManagementAccess(String authorizationHeader) {
        ensureRole(authorizationHeader, Set.of(
                ConstantUtilsService.USER_TYPE_ADMIN,
                ConstantUtilsService.USER_TYPE_COORD,
                ConstantUtilsService.USER_TYPE_MASTER
        ));
    }

    public void ensureAnyAuthenticated(String authorizationHeader) {
        extractBearerToken(authorizationHeader);
    }

    private void ensureRole(String authorizationHeader, Set<String> allowedTypes) {
        String token = extractBearerToken(authorizationHeader);
        if (!jwtUtilsService.isUserOneOfTypes(token, allowedTypes)) {
            throw new SecurityException("Usuário sem permissão para este recurso");
        }
    }
}

