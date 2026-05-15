package com.agendarpgadmin.api.dtos

/**
 * DTO para solicitação de renovação de token.
 */
data class RefreshTokenDTO(
    val refreshToken: String
)
