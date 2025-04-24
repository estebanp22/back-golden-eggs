package com.goldeneggs.Dto;

import lombok.Data;

/**
 * Data Transfer Object for authentication response containing the access token.
 */
@Data
public class AuthResponseDTO {

    /**
     * The access token provided for the authenticated user.
     */
    private String accessToken;

    /**
     * The token type, always "Bearer " in this case.
     */
    private String tokenType = "Bearer ";

    /**
     * Constructor for creating the response DTO with the access token.
     *
     * @param accessToken The access token to be sent in the response.
     */
    public AuthResponseDTO(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        this.accessToken = accessToken;
    }
}