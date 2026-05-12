package com.example.trustfundr_be.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trustfundr_be.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    long countByCreatedAtBefore(Instant cutoff);

    long countByDonorUsername(String username);

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.fundraisingActivity WHERE d.donor.username = :username "
            + "ORDER BY d.createdAt DESC")
    List<Donation> findByDonorUsernameForHistory(@Param("username") String username);

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.fundraisingActivity act WHERE d.donor.username = :username AND ("
            + "(act IS NOT NULL AND (LOWER(act.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(act.description IS NOT NULL AND LOWER(act.description) LIKE LOWER(CONCAT('%', :q, '%'))))) OR "
            + "(d.memo IS NOT NULL AND LOWER(d.memo) LIKE LOWER(CONCAT('%', :q, '%')))) "
            + "ORDER BY d.createdAt DESC")
    List<Donation> searchDonationHistoryForDonee(@Param("username") String username, @Param("q") String q);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.createdAt >= :start AND d.createdAt < :end")
    long countDonationsBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query(
            "SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.createdAt >= :start AND d.createdAt < :end")
    BigDecimal sumDonationAmountBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT d FROM Donation d JOIN FETCH d.fundraisingActivity "
            + "WHERE d.createdAt >= :start AND d.createdAt < :end "
            + "ORDER BY d.amount DESC, d.createdAt DESC")
    List<Donation> findTopDonationsBetween(@Param("start") Instant start, @Param("end") Instant end,
            Pageable pageable);
}
