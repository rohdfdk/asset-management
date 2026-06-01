package org.example.assetmanagement.repository;

import org.example.assetmanagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserUsername(String username);
    List<Loan> findByAssetId(Long assetId);
    List<Loan> findByStatus(String status);
    List<Loan> findByStatusAndExpectedReturnDateBefore(String status, LocalDate date);
}
