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
import com.example.trustfundr_be.model.FundraisingCategoryModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingCategory extends JpaRepository<FundraisingCategoryModel, UUID>, FundraisingCategoryCustom {

    Optional<FundraisingCategoryModel> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM FundraisingCategoryModel c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(c.description IS NOT NULL AND LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<FundraisingCategoryModel> searchByKeyword(@Param("q") String q, Sort sort);
}

interface FundraisingCategoryCustom {

    FundraisingCategoryModel createFundraisingCategory(CreateFundraisingCategoryController.CreateFundraisingCategoryRequest request);

    FundraisingCategoryModel updateFundraisingCategory(UUID id, UpdateFundraisingCategoryController.UpdateFundraisingCategoryRequest request);

    FundraisingCategoryModel suspendFundraisingCategory(UUID id);
}

@RequiredArgsConstructor
class FundraisingCategoryImpl implements FundraisingCategoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;

    @Override
    public FundraisingCategoryModel createFundraisingCategory(CreateFundraisingCategoryController.CreateFundraisingCategoryRequest request) {
        Optional<FundraisingCategoryModel> existing = findByNameIgnoreCase(request.getName());
        if (existing.isPresent()) {
            throw new FundraisingCategoryException(HttpStatus.BAD_REQUEST,
                    "Fundraising category already exists");
        }

        FundraisingCategoryModel entity = modelMapper.map(request, FundraisingCategoryModel.class);
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingCategoryModel updateFundraisingCategory(UUID id,
            UpdateFundraisingCategoryController.UpdateFundraisingCategoryRequest request) {
        FundraisingCategoryModel entity = entityManager.find(FundraisingCategoryModel.class, id);
        if (entity == null || entity.isDeleted()) {
            throw new FundraisingCategoryException(HttpStatus.NOT_FOUND, "Fundraising category not found");
        }
        modelMapper.map(request, entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public FundraisingCategoryModel suspendFundraisingCategory(UUID id) {
        FundraisingCategoryModel entity = entityManager.find(FundraisingCategoryModel.class, id);
        if (entity == null) {
            throw new FundraisingCategoryException(HttpStatus.NOT_FOUND, "Fundraising category not found");
        }
        entity.softDelete();
        entityManager.flush();
        return entity;
    }

    private Optional<FundraisingCategoryModel> findByNameIgnoreCase(String name) {
        List<FundraisingCategoryModel> results = entityManager
                .createQuery("SELECT c FROM FundraisingCategoryModel c WHERE LOWER(c.name) = LOWER(:name)",
                        FundraisingCategoryModel.class)
                .setParameter("name", name)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}
