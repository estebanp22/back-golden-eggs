package com.goldeneggs.Dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String username;
    private String password;

    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    private Long roleId;
}
