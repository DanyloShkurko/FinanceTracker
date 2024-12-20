package org.example.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "limit_details")
@Getter
@Setter
@NoArgsConstructor
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

    public Limit(BigDecimal limitAmount, BigDecimal currentSpent, boolean isExceeded, Category category, LocalDate startDate, LocalDate endDate, User user) {
        this.limitAmount = limitAmount;
        this.currentSpent = currentSpent;
        this.isExceeded = isExceeded;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }
}
