package org.example.expensetracker.repository;

import org.example.expensetracker.entity.Spending;
import org.example.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> {
    List<Spending> findByUser(User user);
}
