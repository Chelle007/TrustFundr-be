package com.example.trustfundr_be.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fundraising_activity_favourites", uniqueConstraints = @UniqueConstraint(name = "uk_favourite_donee_activity",
        columnNames = { "donee_account_id", "fundraising_activity_id" }))
@Getter
@Setter
@NoArgsConstructor
public class FundraisingActivityFavouriteModel extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donee_account_id", nullable = false)
    private UserAccountModel donee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_activity_id", nullable = false)
    private FundraisingActivityModel fundraisingActivity;
}
