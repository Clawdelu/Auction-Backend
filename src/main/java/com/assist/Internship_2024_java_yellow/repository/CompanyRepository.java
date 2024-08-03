package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
