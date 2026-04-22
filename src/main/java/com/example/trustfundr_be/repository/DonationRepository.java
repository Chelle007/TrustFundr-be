package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trustfundr_be.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.fundraisingActivity WHERE d.donor.username = :username "
            + "ORDER BY d.createdAt DESC")
    List<Donation> findByDonorUsernameForHistory(@Param("username") String username);

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.fundraisingActivity act WHERE d.donor.username = :username AND ("
            + "(act IS NOT NULL AND (LOWER(act.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(act.description IS NOT NULL AND LOWER(act.description) LIKE LOWER(CONCAT('%', :q, '%'))))) OR "
            + "(d.memo IS NOT NULL AND LOWER(d.memo) LIKE LOWER(CONCAT('%', :q, '%')))) "
            + "ORDER BY d.createdAt DESC")
    List<Donation> searchDonationHistoryForDonee(@Param("username") String username, @Param("q") String q);
}
