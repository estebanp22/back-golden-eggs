package com.goldeneggs.Users.Service.Role;


import com.goldeneggs.Users.Model.Role.Role;

import java.util.List;

public interface RoleService {

    Role get(Long id);

    List<Role> getAll();

    Role insert(Role role);

    void update(Role role);

}
