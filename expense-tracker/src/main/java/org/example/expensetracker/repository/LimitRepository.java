package org.example.expensetracker.repository;

import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Limit l WHERE l.endDate < :currentDate")
    void deleteExpiredLimits(LocalDate currentDate);

    Optional<Limit> findById(long limitId);
}
