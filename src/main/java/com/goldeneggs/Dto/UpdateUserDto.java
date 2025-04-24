package com.goldeneggs.Dto;

import lombok.Data;


/**
 * Data Transfer Object for updating user details.
 * Allows partial updates of user attributes.
 */
@Data
public class UpdateUserDto {

    /**
     * Updated username for the user.
     */
    private String username;

    /**
     * Updated password for the user.
     */
    private String password;

    /**
     * Updated name of the user.
     */
    private String name;

    /**
     * Updated phone number of the user.
     */
    private String phoneNumber;

    /**
     * Updated email address of the user.
     */
    private String email;

    /**
     * Updated address of the user.
     */
    private String address;

    /**
     * New role ID to assign to the user.
     */
    private Long roleId;

    /**
     * New role name to assign to the user.
     */
    private String roleName;
}
