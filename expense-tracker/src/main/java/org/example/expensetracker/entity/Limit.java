package org.example.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "limit_details")
@Getter
@Setter
@NoArgsConstructor
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

    public Limit(Category category, LocalDate startDate, LocalDate endDate, User user) {
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }
}
