package org.example.assetmanagement.service;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.dto.AssetResponse;
import org.example.assetmanagement.dto.LoanRequest;
import org.example.assetmanagement.dto.LoanResponse;
import org.example.assetmanagement.dto.UserResponse;
import org.example.assetmanagement.entity.Asset;
import org.example.assetmanagement.entity.AssetStatus;
import org.example.assetmanagement.entity.Loan;
import org.example.assetmanagement.entity.LoanStatus;
import org.example.assetmanagement.entity.User;
import org.example.assetmanagement.repository.AssetRepository;
import org.example.assetmanagement.repository.LoanRepository;
import org.example.assetmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LoanResponse> findByUsername(String username) {
        return loanRepository.findByUserUsername(username).stream()
                .map(this::toResponse)
                .toList();
    }

    public LoanResponse findById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
        return toResponse(loan);
    }

    public List<LoanResponse> findActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LoanResponse> findOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loanRepository.findByStatusAndExpectedReturnDateBefore(LoanStatus.ACTIVE, today).stream()
                .peek(loan -> loan.markOverdueIfNeeded(today))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAssetId()));

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new RuntimeException("Asset is not available for loan");
        }

        if (request.getUserId() == null) {
            throw new RuntimeException("User id is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        return createLoanInternal(request, asset, user);
    }

    @Transactional
    public LoanResponse createLoanAsUser(LoanRequest request, String username, boolean admin) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAssetId()));

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new RuntimeException("Asset is not available for loan");
        }

        User user;
        if (admin) {
            if (request.getUserId() == null) {
                throw new RuntimeException("User id is required");
            }

            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));
        } else {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
        }

        return createLoanInternal(request, asset, user);
    }

    private LoanResponse createLoanInternal(LoanRequest request, Asset asset, User user) {
        Loan loan = new Loan(
                asset,
                user,
                request.getLoanDate(),
                request.getExpectedReturnDate(),
                request.getRemarks()
        );

        asset.changeStatus(AssetStatus.LOANED);
        assetRepository.save(asset);

        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    @Transactional
    public LoanResponse returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        return returnLoanInternal(loan);
    }

    @Transactional
    public LoanResponse returnLoanAsUser(Long loanId, String username, boolean admin) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        if (!admin && !loan.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to return this loan");
        }

        return returnLoanInternal(loan);
    }

    private LoanResponse returnLoanInternal(Loan loan) {
        loan.returnAsset(LocalDate.now());

        Asset asset = loan.getAsset();
        asset.changeStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        Loan updated = loanRepository.save(loan);
        return toResponse(updated);
    }

    private LoanResponse toResponse(Loan loan) {
        AssetResponse assetResponse = new AssetResponse(
                loan.getAsset().getId(),
                loan.getAsset().getAssetCode(),
                loan.getAsset().getName(),
                loan.getAsset().getDescription(),
                loan.getAsset().getCategory(),
                loan.getAsset().getStatus().name(),
                loan.getAsset().getLocation(),
                loan.getAsset().getCreatedAt(),
                loan.getAsset().getUpdatedAt()
        );

        UserResponse userResponse = new UserResponse(
                loan.getUser().getId(),
                loan.getUser().getUsername(),
                loan.getUser().getEmail(),
                loan.getUser().getFullName(),
                loan.getUser().getRole(),
                loan.getUser().getStatus(),
                loan.getUser().getCreatedAt(),
                loan.getUser().getUpdatedAt()
        );

        return new LoanResponse(
                loan.getId(),
                assetResponse,
                userResponse,
                loan.getLoanDate(),
                loan.getExpectedReturnDate(),
                loan.getActualReturnDate(),
                loan.getStatus().name(),
                loan.getRemarks(),
                loan.getCreatedAt(),
                loan.getUpdatedAt()
        );
    }
}