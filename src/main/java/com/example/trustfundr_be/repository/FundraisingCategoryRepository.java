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

import com.example.trustfundr_be.controller.CreateFundraisingCategoryController;
import com.example.trustfundr_be.controller.UpdateFundraisingCategoryController;
import com.example.trustfundr_be.exception.FundraisingCategoryException;
import com.example.trustfundr_be.model.FundraisingCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingCategoryRepository
        extends JpaRepository<FundraisingCategory, UUID>, FundraisingCategoryRepositoryCustom {

    Optional<FundraisingCategory> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM FundraisingCategory c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(c.description IS NOT NULL AND LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<FundraisingCategory> searchByKeyword(@Param("q") String q, Sort sort);
}

interface FundraisingCategoryRepositoryCustom {

    FundraisingCategory createFundraisingCategory(CreateFundraisingCategoryController.CreateFundraisingCategoryRequest request);

    FundraisingCategory updateFundraisingCategory(UUID id, UpdateFundraisingCategoryController.UpdateFundraisingCategoryRequest request);

    FundraisingCategory suspendFundraisingCategory(UUID id);
}

@RequiredArgsConstructor
class FundraisingCategoryRepositoryImpl implements FundraisingCategoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;

    @Override
    public FundraisingCategory createFundraisingCategory(CreateFundraisingCategoryController.CreateFundraisingCategoryRequest request) {
        Optional<FundraisingCategory> existing = findByNameIgnoreCase(request.getName());
        if (existing.isPresent()) {
            throw new FundraisingCategoryException(HttpStatus.BAD_REQUEST,
                    "Fundraising category already exists");
        }

        FundraisingCategory entity = modelMapper.map(request, FundraisingCategory.class);
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingCategory updateFundraisingCategory(UUID id,
            UpdateFundraisingCategoryController.UpdateFundraisingCategoryRequest request) {
        FundraisingCategory entity = entityManager.find(FundraisingCategory.class, id);
        if (entity == null || entity.isDeleted()) {
            throw new FundraisingCategoryException(HttpStatus.NOT_FOUND, "Fundraising category not found");
        }
        modelMapper.map(request, entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingCategory suspendFundraisingCategory(UUID id) {
        FundraisingCategory entity = entityManager.find(FundraisingCategory.class, id);
        if (entity == null) {
            throw new FundraisingCategoryException(HttpStatus.NOT_FOUND, "Fundraising category not found");
        }
        entity.softDelete();
        entityManager.flush();
        return entity;
    }

    private Optional<FundraisingCategory> findByNameIgnoreCase(String name) {
        List<FundraisingCategory> results = entityManager
                .createQuery("SELECT c FROM FundraisingCategory c WHERE LOWER(c.name) = LOWER(:name)",
                        FundraisingCategory.class)
                .setParameter("name", name)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}

