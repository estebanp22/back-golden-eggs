package com.goldeneggs.User;


import com.goldeneggs.Role.Role;

import java.util.List;

public interface UserService {

    //User saveUser(User user, Set<UserRole> userRoles) throws Exception;

    void save(User user) throws Exception;

    User getUser(String username);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    User updateUser(String username, User newUser, Role role);

    User disableUser(User user);

    User updatePassword(User user, String emailRequest);

    User activateUser(User user);

    User updatePasswordModule(User user, String newPassword);

}





