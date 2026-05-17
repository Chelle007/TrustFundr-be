package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.exception.FundraisingActivityException;
import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.FundraisingActivityFavouriteModel;
import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.model.UserAccountModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingActivityFavourite
        extends JpaRepository<FundraisingActivityFavouriteModel, UUID>, FundraisingActivityFavouriteCustom {

    @Query("SELECT fav FROM FundraisingActivityFavouriteModel fav "
            + "JOIN FETCH fav.fundraisingActivity act "
            + "LEFT JOIN FETCH act.fundraisingCategory "
            + "WHERE fav.donee.username = :username ORDER BY fav.createdAt DESC")
    List<FundraisingActivityFavouriteModel> findAllByDoneeUsernameOrderByCreatedAtDesc(@Param("username") String username);

    @Query("SELECT fav FROM FundraisingActivityFavouriteModel fav "
            + "JOIN FETCH fav.fundraisingActivity act "
            + "LEFT JOIN FETCH act.fundraisingCategory "
            + "WHERE fav.donee.username = :username AND ("
            + "LOWER(act.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(act.description IS NOT NULL AND LOWER(act.description) LIKE LOWER(CONCAT('%', :q, '%')))) "
            + "ORDER BY fav.createdAt DESC")
    List<FundraisingActivityFavouriteModel> searchByDoneeUsername(@Param("username") String username, @Param("q") String q);
}

interface FundraisingActivityFavouriteCustom {

    FundraisingActivityFavouriteModel saveFavourite(String doneeUsername, UUID activityId);
}

@RequiredArgsConstructor
class FundraisingActivityFavouriteImpl implements FundraisingActivityFavouriteCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserAccount userAccountRepository;
    private final FundraisingActivity fundraisingActivityRepository;

    @Override
    public FundraisingActivityFavouriteModel saveFavourite(String doneeUsername, UUID activityId) {
        Optional<FundraisingActivityFavouriteModel> existing = entityManager
                .createQuery(
                        "SELECT fav FROM FundraisingActivityFavouriteModel fav JOIN fav.donee d "
                                + "JOIN fav.fundraisingActivity a WHERE d.username = :username AND a.id = :activityId",
                        FundraisingActivityFavouriteModel.class)
                .setParameter("username", doneeUsername)
                .setParameter("activityId", activityId)
                .getResultStream()
                .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        UserAccountModel donee = userAccountRepository.findByUsername(doneeUsername)
                .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User account not found"));
        FundraisingActivityModel activity = fundraisingActivityRepository.findById(activityId)
                .orElseThrow(() -> new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found"));
        FundraisingActivityFavouriteModel row = new FundraisingActivityFavouriteModel();
        row.setDonee(donee);
        row.setFundraisingActivity(activity);
        entityManager.persist(row);
        entityManager.flush();
        fundraisingActivityRepository.incrementFavouriteCountById(activityId);
        return row;
    }
}
