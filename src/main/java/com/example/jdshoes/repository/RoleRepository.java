package com.example.jdshoes.repository;


import com.example.jdshoes.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}