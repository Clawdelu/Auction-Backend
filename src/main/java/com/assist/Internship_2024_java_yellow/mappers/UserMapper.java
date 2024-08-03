package com.assist.Internship_2024_java_yellow.mappers;

import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.dtos.AdminDto;
import com.assist.Internship_2024_java_yellow.dtos.UserDTO;
import com.assist.Internship_2024_java_yellow.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    @Mapping(target = "password", ignore = true)
    User toUser(UserRegisterRequest userRegisterRequest);

    default User toUser(UserRegisterRequest userRegisterRequest, String encryptedPassword) {
        User user = toUser(userRegisterRequest);
        user.setPassword(encryptedPassword);
        return user;
    }

    @Mapping(target = "password", ignore = true)
    User toUser(AdminDto adminDto);

    default User toUser(AdminDto adminDto, String encryptedPassword) {
        User user = toUser(adminDto);
        user.setPassword(encryptedPassword);
        return user;
    }
}





