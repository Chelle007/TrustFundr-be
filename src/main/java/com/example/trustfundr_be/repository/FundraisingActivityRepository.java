package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.controller.CreateFundraisingActivityController;
import com.example.trustfundr_be.controller.UpdateFundraisingActivityController;
import com.example.trustfundr_be.exception.FundraisingActivityException;
import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.UserAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingActivityRepository
        extends JpaRepository<FundraisingActivity, UUID>, FundraisingActivityRepositoryCustom {

    Optional<FundraisingActivity> findByIdAndOwnerUsername(UUID id, String ownerUsername);

    List<FundraisingActivity> findByOwnerUsernameOrderByCreatedAtDesc(String ownerUsername);

    @Query("SELECT f FROM FundraisingActivity f WHERE f.owner.username = :ownerUsername AND ("
            + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))))")
    List<FundraisingActivity> searchForOwner(@Param("ownerUsername") String ownerUsername, @Param("q") String q,
            Sort sort);

    @Query("SELECT f FROM FundraisingActivity f LEFT JOIN FETCH f.owner ORDER BY f.createdAt DESC")
    List<FundraisingActivity> findAllPublicOrderByCreatedAtDesc();

    @Query("SELECT f FROM FundraisingActivity f LEFT JOIN FETCH f.owner WHERE "
            + "LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(f.description IS NOT NULL AND LOWER(f.description) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "ORDER BY f.createdAt DESC")
    List<FundraisingActivity> searchAllPublic(@Param("q") String q);

    @Query("SELECT f FROM FundraisingActivity f LEFT JOIN FETCH f.owner WHERE f.id = :id")
    Optional<FundraisingActivity> findByIdWithOwner(@Param("id") UUID id);
}

interface FundraisingActivityRepositoryCustom {

    FundraisingActivity createFundraisingActivity(String ownerUsername,
            CreateFundraisingActivityController.CreateFundraisingActivityRequest request);

    FundraisingActivity updateFundraisingActivity(String ownerUsername, UUID id,
            UpdateFundraisingActivityController.UpdateFundraisingActivityRequest request);

    FundraisingActivity suspendFundraisingActivity(String ownerUsername, UUID id);
}

@RequiredArgsConstructor
class FundraisingActivityRepositoryImpl implements FundraisingActivityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    @Override
    public FundraisingActivity createFundraisingActivity(String ownerUsername,
            CreateFundraisingActivityController.CreateFundraisingActivityRequest request) {
        // Owner comes from security context (passed in by controller), not from the HTTP body
        UserAccount owner = userAccountRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new FundraisingActivityException(HttpStatus.NOT_FOUND, "User account not found"));
        FundraisingActivity entity = modelMapper.map(request, FundraisingActivity.class);
        entity.setOwner(owner);
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingActivity updateFundraisingActivity(String ownerUsername, UUID id,
            UpdateFundraisingActivityController.UpdateFundraisingActivityRequest request) {
        FundraisingActivity entity = entityManager.find(FundraisingActivity.class, id);
        if (entity == null) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        // Only the owning fundraiser may update; hide existence for others (404)
        if (entity.getOwner() == null || !ownerUsername.equals(entity.getOwner().getUsername())) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        // Cannot update a suspended (soft-deleted) activity
        if (entity.isDeleted()) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        modelMapper.map(request, entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingActivity suspendFundraisingActivity(String ownerUsername, UUID id) {
        FundraisingActivity entity = entityManager.find(FundraisingActivity.class, id);
        if (entity == null) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        // Only the owning fundraiser may suspend; hide existence for others (404)
        if (entity.getOwner() == null || !ownerUsername.equals(entity.getOwner().getUsername())) {
            throw new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found");
        }
        // Suspend via soft delete (same pattern as user profile / user account)
        entity.softDelete();
        entityManager.flush();
        return entity;
    }
}
