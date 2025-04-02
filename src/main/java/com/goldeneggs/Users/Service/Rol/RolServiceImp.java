package com.goldeneggs.Users.Service.Rol;

import com.goldeneggs.Users.Model.Rol.Rol;
import com.goldeneggs.Users.Repository.Rol.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImp implements RolService {

    @Autowired
    RolRepository rolRepository;

    @Override
    public Rol get(Long id) {
        return rolRepository.getById(id);
    }

    @Override
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @Override
    public Rol insert(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public void update(Rol rol) {

    }
}
