package com.example.trustfundr_be.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fundraising_activities")
@Getter
@Setter
@NoArgsConstructor
public class FundraisingActivityModel extends BaseModel {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 5000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraising_category_id", nullable = false)
    private FundraisingCategoryModel fundraisingCategory;

    @Column(length = 255)
    private String location;

    @Column(name = "goal_amount", precision = 19, scale = 2)
    private BigDecimal goalAmount;

    @Column(name = "current_amount", precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "favourite_count", nullable = false)
    private long favouriteCount;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private UserAccountModel owner;

    @PostLoad
    private void normalizeAmountsAfterLoad() {
        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }
    }

    /** Category display name for JSON / ModelMapper (`category` field on API DTOs). */
    public String getCategory() {
        return fundraisingCategory == null ? null : fundraisingCategory.getName();
    }

    /** Category id for JSON / ModelMapper (`categoryId` on API DTOs). */
    public UUID getCategoryId() {
        return fundraisingCategory == null ? null : fundraisingCategory.getId();
    }
}
