package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Role;
import com.example.jdshoes.entity.enumClass.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role r where r.name = ?1")
    Role findByRoleName(RoleName roleEmployee);
}