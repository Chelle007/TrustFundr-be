package com.example.trustfundr_be.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.controller.CreateFundraisingActivityController;
import com.example.trustfundr_be.controller.UpdateFundraisingActivityController;
import com.example.trustfundr_be.exception.FundraisingActivityException;
import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.model.UserAccountModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingActivity
        extends JpaRepository<FundraisingActivityModel, UUID>, FundraisingActivityCustom {

    Optional<FundraisingActivityModel> findByIdAndOwnerUsername(UUID id, String ownerUsername);

    @Query("SELECT f FROM FundraisingActivityModel f WHERE f.imageUrl IS NULL OR TRIM(f.imageUrl) = ''")
    Page<FundraisingActivityModel> findMissingHeroImages(Pageable pageable);

    @Query("SELECT f FROM FundraisingActivityModel f WHERE f.owner.username = :ownerUsername AND f.completedAt IS NULL "
            + "ORDER BY f.createdAt DESC")
    List<FundraisingActivityModel> findActiveByOwnerUsernameOrderByCreatedAtDesc(
            @Param("ownerUsername") String ownerUsername);

    @Query("SELECT f FROM FundraisingActivityModel f WHERE f.owner.username = :ownerUsername AND f.completedAt IS NULL AND ("
            + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
            + "(f.category IS NOT NULL AND LOWER(f.category) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
            + "(f.location IS NOT NULL AND LOWER(f.location) LIKE LOWER(CONCAT('%', :q, '%'))))")
    List<FundraisingActivityModel> searchForOwner(@Param("ownerUsername") String ownerUsername, @Param("q") String q,
            Sort sort);

    @Query("SELECT f FROM FundraisingActivityModel f WHERE f.owner.username = :ownerUsername AND f.completedAt IS NOT NULL "
            + "ORDER BY f.completedAt DESC")
    List<FundraisingActivityModel> findCompletedByOwnerUsernameOrderByCompletedAtDesc(
            @Param("ownerUsername") String ownerUsername);

    @Query("SELECT f FROM FundraisingActivityModel f WHERE f.owner.username = :ownerUsername AND f.completedAt IS NOT NULL AND ("
            + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
            + "(f.category IS NOT NULL AND LOWER(f.category) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
            + "(f.location IS NOT NULL AND LOWER(f.location) LIKE LOWER(CONCAT('%', :q, '%')))) "
            + "ORDER BY f.completedAt DESC")
    List<FundraisingActivityModel> searchCompletedForOwner(@Param("ownerUsername") String ownerUsername, @Param("q") String q);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE FundraisingActivityModel f SET f.viewCount = f.viewCount + 1 WHERE f.id = :id AND f.deletedAt IS NULL")
    void incrementViewCountById(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE FundraisingActivityModel f SET f.favouriteCount = f.favouriteCount + 1 WHERE f.id = :id AND f.deletedAt IS NULL")
    void incrementFavouriteCountById(@Param("id") UUID id);

    @Query("SELECT f FROM FundraisingActivityModel f ORDER BY f.createdAt DESC")
    Page<FundraisingActivityModel> findAllPublicPage(Pageable pageable);

    @Query(
            value = "SELECT f FROM FundraisingActivityModel f WHERE "
                    + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
                    + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
                    + "(f.category IS NOT NULL AND LOWER(f.category) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
                    + "(f.location IS NOT NULL AND LOWER(f.location) LIKE LOWER(CONCAT('%', :q, '%'))) "
                    + "ORDER BY f.createdAt DESC",
            countQuery = "SELECT count(f) FROM FundraisingActivityModel f WHERE "
                    + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
                    + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
                    + "(f.category IS NOT NULL AND LOWER(f.category) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
                    + "(f.location IS NOT NULL AND LOWER(f.location) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<FundraisingActivityModel> searchAllPublicPage(@Param("q") String q, Pageable pageable);

    @Query("SELECT f FROM FundraisingActivityModel f LEFT JOIN FETCH f.owner WHERE f.id = :id")
    Optional<FundraisingActivityModel> findByIdWithOwner(@Param("id") UUID id);

    @Query("SELECT COUNT(f) FROM FundraisingActivityModel f WHERE f.createdAt >= :start AND f.createdAt < :end")
    long countCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query(
            "SELECT COUNT(f) FROM FundraisingActivityModel f WHERE f.completedAt IS NOT NULL AND f.completedAt >= :start AND f.completedAt < :end")
    long countCompletedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query(
            "SELECT COALESCE(SUM(f.viewCount), 0) FROM FundraisingActivityModel f WHERE f.createdAt >= :start AND f.createdAt < :end")
    long sumViewCountCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query(
            "SELECT COALESCE(SUM(f.favouriteCount), 0) FROM FundraisingActivityModel f WHERE f.createdAt >= :start AND f.createdAt < :end")
    long sumFavouriteCountCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);
}

interface FundraisingActivityCustom {

    FundraisingActivityModel createFundraisingActivity(String ownerUsername,
            CreateFundraisingActivityController.CreateFundraisingActivityRequest request);

    FundraisingActivityModel updateFundraisingActivity(String ownerUsername, UUID id,
            UpdateFundraisingActivityController.UpdateFundraisingActivityRequest request);

    FundraisingActivityModel suspendFundraisingActivity(String ownerUsername, UUID id);
}

@RequiredArgsConstructor
class FundraisingActivityImpl implements FundraisingActivityCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserAccount userAccountRepository;
    private final ModelMapper modelMapper;

    @Override
    public FundraisingActivityModel createFundraisingActivity(String ownerUsername,
            CreateFundraisingActivityController.CreateFundraisingActivityRequest request) {
        UserAccountModel owner = userAccountRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new FundraisingActivityException(HttpStatus.NOT_FOUND, "User account not found"));
        FundraisingActivityModel entity = modelMapper.map(request, FundraisingActivityModel.class);
        entity.setOwner(owner);
        entity.setViewCount(0);
        entity.setFavouriteCount(0);
        entity.setCompletedAt(null);
        if (entity.getCurrentAmount() == null) {
            entity.setCurrentAmount(BigDecimal.ZERO);
        }
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingActivityModel updateFundraisingActivity(String ownerUsername, UUID id,
            UpdateFundraisingActivityController.UpdateFundraisingActivityRequest request) {
        FundraisingActivityModel entity = entityManager.find(FundraisingActivityModel.class, id);
        if (entity == null) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        if (entity.getOwner() == null || !ownerUsername.equals(entity.getOwner().getUsername())) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        if (entity.isDeleted()) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        if (entity.getCompletedAt() != null) {
            throw new FundraisingActivityException(HttpStatus.BAD_REQUEST,
                    "Cannot update a completed fundraising activity");
        }
        entity.setTitle(request.getTitle().trim());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setLocation(request.getLocation());
        entity.setGoalAmount(request.getGoalAmount());
        if (request.getCurrentAmount() != null) {
            entity.setCurrentAmount(request.getCurrentAmount());
        }
        entity.setImageUrl(request.getImageUrl());
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingActivityModel suspendFundraisingActivity(String ownerUsername, UUID id) {
        FundraisingActivityModel entity = entityManager.find(FundraisingActivityModel.class, id);
        if (entity == null) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        if (entity.getOwner() == null || !ownerUsername.equals(entity.getOwner().getUsername())) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        entity.softDelete();
        entityManager.flush();
        return entity;
    }
}
