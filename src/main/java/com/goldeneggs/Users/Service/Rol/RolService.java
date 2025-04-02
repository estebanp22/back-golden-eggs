package com.goldeneggs.Users.Service.Rol;


import com.goldeneggs.Users.Model.Rol.Rol;

import java.util.List;

public interface RolService {

    public Rol get(Long id);

    public List<Rol> getAll();

    public Rol insert(Rol rol);

    public void update(Rol rol);

}
