package org.example.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "limit_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private BigDecimal limitAmount;

    @Column
    private BigDecimal currentSpent;

    @Column
    private boolean isExceeded;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Limit(BigDecimal limitAmount,
                 BigDecimal currentSpent,
                 boolean isExceeded,
                 Category category,
                 LocalDate startDate,
                 LocalDate endDate,
                 User user) {
        this.limitAmount = limitAmount;
        this.currentSpent = currentSpent;
        this.isExceeded = isExceeded;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Limit limit = (Limit) o;
        return id == limit.id && isExceeded == limit.isExceeded && Objects.equals(limitAmount, limit.limitAmount) && Objects.equals(currentSpent, limit.currentSpent) && category == limit.category && Objects.equals(startDate, limit.startDate) && Objects.equals(endDate, limit.endDate) && Objects.equals(user, limit.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, limitAmount, currentSpent, isExceeded, category, startDate, endDate, user);
    }
}
