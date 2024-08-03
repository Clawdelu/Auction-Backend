package com.assist.Internship_2024_java_yellow.entities;

import com.assist.Internship_2024_java_yellow.enums.EntitySizeEnum;
import com.assist.Internship_2024_java_yellow.enums.EntityTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private int id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private EntityTypeEnum entityType;

    @Column(name = "entity_size")
    @Enumerated(EnumType.STRING)
    private EntitySizeEnum entitySize;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "tax_identification_number")
    private String taxIdentificationNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
