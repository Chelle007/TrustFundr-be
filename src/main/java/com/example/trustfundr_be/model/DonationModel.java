package com.example.trustfundr_be.model;

import java.math.BigDecimal;

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
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
public class DonationModel extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donee_account_id", nullable = false)
    private UserAccountModel donee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_activity_id", nullable = false)
    private FundraisingActivityModel fundraisingActivity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 2000)
    private String memo;
}
