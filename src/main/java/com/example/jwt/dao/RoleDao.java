package com.example.jwt.dao;

import com.example.jwt.dto.enums.RoleName;
import com.example.jwt.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDao extends CrudRepository<Role, Integer> {
    Role findByName(RoleName name);
}
