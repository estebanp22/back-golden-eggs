package com.goldeneggs.Users.Service.User;


import com.goldeneggs.Users.Model.Rol.Role;
import com.goldeneggs.Users.Model.User.User;
import com.goldeneggs.Users.Model.User.UserRole;

import java.util.List;
import java.util.Set;

public interface UserService {

    User saveUser(User user, Set<UserRole> userRoles) throws Exception;

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





