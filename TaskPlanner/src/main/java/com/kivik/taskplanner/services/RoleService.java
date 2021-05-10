package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepo;

    public Role findByName(String name) {
        return roleRepo.findByName(name);
    }
}
