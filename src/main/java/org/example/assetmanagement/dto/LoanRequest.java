package org.example.assetmanagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    @NotNull(message = "資産IDは必須です")
    private Long assetId;

    @NotNull(message = "ユーザーIDは必須です")
    private Long userId;

    @NotNull(message = "貸出日は必須です")
    private LocalDate loanDate;

    @NotNull(message = "返却予定日は必須です")
    @Future(message = "返却予定日は未来の日付を指定してください")
    private LocalDate expectedReturnDate;

    private String remarks;
}