package com.goldeneggs.Dto;


import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;

    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
}
