package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.entities.Role;
import com.assist.Internship_2024_java_yellow.enums.RoleEnum;
import com.assist.Internship_2024_java_yellow.repository.RoleRepository;
import com.assist.Internship_2024_java_yellow.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByRoleName(RoleEnum roleEnum) {
        return roleRepository.findByRoleName(roleEnum);
    }
}
