package com.assist.Internship_2024_java_yellow.mappers;

import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.dtos.CompanyDTO;
import com.assist.Internship_2024_java_yellow.entities.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);


    Company toCompany(UserRegisterRequest userRegisterRequest);
    CompanyDTO companyToCompanyDTO(Company company);
}





