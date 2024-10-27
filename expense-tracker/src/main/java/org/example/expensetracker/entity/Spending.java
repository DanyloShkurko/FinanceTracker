package org.example.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "spending_details")
@Getter
@Setter
@NoArgsConstructor
public class Spending {
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

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Spending(String title, String description, BigDecimal amount, String currency, LocalDate date, User user) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.user = user;
    }
}
