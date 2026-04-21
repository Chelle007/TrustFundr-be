package com.example.trustfundr_be.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fundraising_activities")
@Getter
@Setter
@NoArgsConstructor
public class FundraisingActivity extends BaseModel {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 5000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private UserAccount owner;
}
