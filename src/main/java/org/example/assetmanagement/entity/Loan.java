package org.example.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Loan(Asset asset, User user, LocalDate loanDate, LocalDate expectedReturnDate, String remarks) {
        validateRequired(asset, "asset");
        validateRequired(user, "user");
        validateRequired(loanDate, "loanDate");
        validateRequired(expectedReturnDate, "expectedReturnDate");

        if (expectedReturnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("expectedReturnDate must be on or after loanDate");
        }

        this.asset = asset;
        this.user = user;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = LoanStatus.ACTIVE;
        this.remarks = remarks;
    }

    public void returnAsset(LocalDate returnDate) {
        validateRequired(returnDate, "returnDate");

        if (LoanStatus.RETURNED.equals(status)) {
            return;
        }

        if (!(LoanStatus.ACTIVE.equals(status) || LoanStatus.OVERDUE.equals(status))) {
            throw new IllegalStateException("Loan is not active");
        }

        this.actualReturnDate = returnDate;
        this.status = LoanStatus.RETURNED;
    }

    public boolean isOverdue(LocalDate onDate) {
        validateRequired(onDate, "onDate");
        return (LoanStatus.ACTIVE.equals(status) || LoanStatus.OVERDUE.equals(status))
                && expectedReturnDate.isBefore(onDate);
    }

    public void markOverdueIfNeeded(LocalDate onDate) {
        validateRequired(onDate, "onDate");

        if (LoanStatus.ACTIVE.equals(status) && expectedReturnDate.isBefore(onDate)) {
            this.status = LoanStatus.OVERDUE;
        }
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}