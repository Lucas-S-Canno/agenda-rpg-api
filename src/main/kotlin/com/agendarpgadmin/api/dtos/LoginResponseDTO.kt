package com.agendarpgadmin.api.dtos

/**
 * DTO para resposta de login contendo Access e Refresh Tokens.
 */
data class LoginResponseDTO(
    val accessToken: String,
    val refreshToken: String
)
