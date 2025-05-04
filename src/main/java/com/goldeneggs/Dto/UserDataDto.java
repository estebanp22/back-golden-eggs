package com.goldeneggs.Dto;

import com.goldeneggs.Role.Role;
import lombok.Data;

import java.util.List;


/**
 * Data Transfer Object for user data.
 */
@Data
public class UserDataDto {


    /**
     * The username of the user.
     */
    private String username;

    /**
     * The user's unique identifier.
     */
    private Long id;

    /**
     * The full name of the user.
     */
    private String name;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The address of the user.
     */
    private String address;

    /**
     * The roles assigned to the user.
     */
    List<Role> roles;

}
