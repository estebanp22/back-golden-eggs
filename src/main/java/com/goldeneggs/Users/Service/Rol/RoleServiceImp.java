package com.goldeneggs.Users.Service.Rol;

import com.goldeneggs.Users.Model.Rol.Role;
import com.goldeneggs.Users.Repository.Rol.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role get(Long id) {
        return roleRepository.getById(id);
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role insert(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void update(Role role) {

    }
}
