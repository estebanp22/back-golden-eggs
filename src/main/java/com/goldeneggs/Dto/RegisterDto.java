package com.goldeneggs.Dto;


import lombok.Data;

/**
 * Data Transfer Object for user registration.
 * Captures the information needed for registering a new user.
 */
@Data
public class RegisterDto {

    /**
     * The username of the user registering.
     */
    private String username;

    /**
     * The password of the user registering.
     */
    private String password;

    /**
     * The user's unique identifier (optional).
     */
    private Long id;

    /**
     * The full name of the user registering.
     */
    private String name;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The email of the user registering.
     */
    private String email;

    /**
     * The address of the user registering.
     */
    private String address;

    /**
     * The role ID assigned to the user.
     */
    private Long roleId;

    /**
     * The role name to assign to the user.
     */
    private String roleName;
}
