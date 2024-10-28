package org.example.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_details")
@Getter
@Setter
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private BigDecimal amount;

    @Column
    private String currency;

    @Column
    private LocalDate date;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Expense(String title, String description, BigDecimal amount, String currency, LocalDate date, Category category, User user) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.category = category;
        this.user = user;
    }
}
