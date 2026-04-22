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
import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.FundraisingActivityFavourite;
import com.example.trustfundr_be.model.UserAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public interface FundraisingActivityFavouriteRepository
        extends JpaRepository<FundraisingActivityFavourite, UUID>, FundraisingActivityFavouriteRepositoryCustom {

    @Query("SELECT fav FROM FundraisingActivityFavourite fav JOIN FETCH fav.fundraisingActivity act "
            + "WHERE fav.donee.username = :username ORDER BY fav.createdAt DESC")
    List<FundraisingActivityFavourite> findAllByDoneeUsernameOrderByCreatedAtDesc(@Param("username") String username);

    @Query("SELECT fav FROM FundraisingActivityFavourite fav JOIN FETCH fav.fundraisingActivity act "
            + "WHERE fav.donee.username = :username AND ("
            + "LOWER(act.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(act.description IS NOT NULL AND LOWER(act.description) LIKE LOWER(CONCAT('%', :q, '%')))) "
            + "ORDER BY fav.createdAt DESC")
    List<FundraisingActivityFavourite> searchByDoneeUsername(@Param("username") String username, @Param("q") String q);
}

interface FundraisingActivityFavouriteRepositoryCustom {

    FundraisingActivityFavourite saveFavourite(String doneeUsername, UUID activityId);
}

@RequiredArgsConstructor
class FundraisingActivityFavouriteRepositoryImpl implements FundraisingActivityFavouriteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserAccountRepository userAccountRepository;
    private final FundraisingActivityRepository fundraisingActivityRepository;

    @Override
    public FundraisingActivityFavourite saveFavourite(String doneeUsername, UUID activityId) {
        Optional<FundraisingActivityFavourite> existing = entityManager
                .createQuery(
                        "SELECT fav FROM FundraisingActivityFavourite fav JOIN fav.donee d "
                                + "JOIN fav.fundraisingActivity a WHERE d.username = :username AND a.id = :activityId",
                        FundraisingActivityFavourite.class)
                .setParameter("username", doneeUsername)
                .setParameter("activityId", activityId)
                .getResultStream()
                .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        UserAccount donee = userAccountRepository.findByUsername(doneeUsername)
                .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User account not found"));
        FundraisingActivity activity = fundraisingActivityRepository.findById(activityId)
                .orElseThrow(() -> new FundraisingActivityException(HttpStatus.NOT_FOUND, "Fundraising activity not found"));
        FundraisingActivityFavourite row = new FundraisingActivityFavourite();
        row.setDonee(donee);
        row.setFundraisingActivity(activity);
        entityManager.persist(row);
        entityManager.flush();
        return row;
    }
}
