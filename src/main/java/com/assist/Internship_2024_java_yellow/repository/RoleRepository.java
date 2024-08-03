package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.Role;
import com.assist.Internship_2024_java_yellow.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleName(RoleEnum roleName);
}
