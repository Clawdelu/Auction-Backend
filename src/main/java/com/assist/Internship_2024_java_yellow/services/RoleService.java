package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.entities.Role;
import com.assist.Internship_2024_java_yellow.enums.RoleEnum;

public interface RoleService {

    Role findByRoleName(RoleEnum roleEnum);
}
