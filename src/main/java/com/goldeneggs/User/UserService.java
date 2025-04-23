package com.goldeneggs.User;


import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Dto.UpdateUserDto;
import com.goldeneggs.Role.Role;

import java.util.List;

public interface UserService {

    User save(RegisterDto registerDto) throws Exception;

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

    User updateUser(Long id, UpdateUserDto updateUserDto);

    User disableUser(Long id);

    User updatePassword(Long userId, String newPassword);

    User activateUser(Long id);
}





