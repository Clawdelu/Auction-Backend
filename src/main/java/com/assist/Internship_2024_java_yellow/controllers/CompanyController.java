package com.assist.Internship_2024_java_yellow.controllers;

import com.assist.Internship_2024_java_yellow.dtos.CompanyDTO;
import com.assist.Internship_2024_java_yellow.services.CompanyService;
import com.assist.Internship_2024_java_yellow.services.UserService;
import com.assist.Internship_2024_java_yellow.services.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final UserService userService;

    @Operation(summary = "Update company details.")
    @PutMapping("/api/legal/company")
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDTO companyDTO) {
        companyService.editCompany(companyDTO);
        return ResponseEntity.status(201).body("Company updated successfully.");
    }

    @GetMapping("/api/legal/company")
    public ResponseEntity<?> getUserCompany() {
        return ResponseEntity.status(200).body(userService.getCompany());
    }
}
