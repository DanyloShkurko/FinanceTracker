package org.example.expensetracker.repository;

import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findByUser(User user);
}
