package com.goldeneggs.Dto;

import lombok.Data;

/**
 * Data Transfer Object for login credentials.
 * Used to capture the username and password during authentication.
 */
@Data
public class LoginDto {

    /**
     * The username of the user attempting to log in.
     * It cannot be null or blank.
     */
    private String username;

    /**
     * The password of the user attempting to log in.
     * It cannot be null or blank.
     */
    private String password;
}