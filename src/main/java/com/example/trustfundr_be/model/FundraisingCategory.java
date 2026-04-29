package com.example.trustfundr_be.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fundraising_categories")
@Getter
@Setter
@NoArgsConstructor
public class FundraisingCategory extends BaseModel {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 2000)
    private String description;
}

