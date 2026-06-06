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

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_RETURNED = "RETURNED";
    public static final String STATUS_OVERDUE = "OVERDUE";

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

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, RETURNED, OVERDUE

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
        this.status = STATUS_ACTIVE;
        this.remarks = remarks;
    }

    public void returnAsset(LocalDate returnDate) {
        validateRequired(returnDate, "returnDate");

        if (!(STATUS_ACTIVE.equals(status) || STATUS_OVERDUE.equals(status))) {
            throw new IllegalStateException("Loan is not active");
        }

        this.actualReturnDate = returnDate;
        this.status = STATUS_RETURNED;
    }

    public boolean isOverdue(LocalDate onDate) {
        validateRequired(onDate, "onDate");
        return (STATUS_ACTIVE.equals(status) || STATUS_OVERDUE.equals(status))
                && expectedReturnDate.isBefore(onDate);
    }

    public void markOverdueIfNeeded(LocalDate onDate) {
        validateRequired(onDate, "onDate");

        if (STATUS_ACTIVE.equals(status) && expectedReturnDate.isBefore(onDate)) {
            this.status = STATUS_OVERDUE;
        }
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}