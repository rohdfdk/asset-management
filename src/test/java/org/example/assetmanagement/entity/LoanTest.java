package org.example.assetmanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@DisplayName("Loan")
class LoanTest {

    // テスト用の共通モック（最小限の構成）
    private final Asset mockAsset = new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null);
    private final User mockUser = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {

        @Test
        void constructor_有効な値を指定した場合_貸出を生成できる() {
            LocalDate loanDate = LocalDate.now();
            LocalDate expectedReturnDate = loanDate.plusDays(7);

            Loan loan = new Loan(mockAsset, mockUser, loanDate, expectedReturnDate, "備考");

            // AssertJによるオブジェクトの検証
            assertThat(loan.getAsset()).isEqualTo(mockAsset);
            assertThat(loan.getUser()).isEqualTo(mockUser);
            assertThat(loan.getLoanDate()).isEqualTo(loanDate);
            assertThat(loan.getExpectedReturnDate()).isEqualTo(expectedReturnDate);
            assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
            assertThat(loan.getRemarks()).isEqualTo("備考");
        }

        @Test
        void constructor_expectedReturnDateがloanDateより前の場合_IllegalArgumentExceptionをスローする() {
            LocalDate loanDate = LocalDate.now();
            
            assertThatThrownBy(() -> new Loan(mockAsset, mockUser, loanDate, loanDate.minusDays(1), "備考"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("expectedReturnDate must be on or after loanDate");
        }

        @Nested
        @DisplayName("validateRequired - 引数のnullチェック")
        class ValidateRequiredTest {
            @Test
            void constructor_assetがnullの場合_IllegalArgumentExceptionをスローする() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Loan(null, mockUser, LocalDate.now(), LocalDate.now().plusDays(7), null));
            }
            @Test
            void constructor_userがnullの場合_IllegalArgumentExceptionをスローする() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Loan(mockAsset, null, LocalDate.now(), LocalDate.now().plusDays(7), null));
            }
            @Test
            void constructor_loanDateがnullの場合_IllegalArgumentExceptionをスローする() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Loan(mockAsset, mockUser, null, LocalDate.now().plusDays(7), null));
            }
            @Test
            void constructor_expectedReturnDateがnullの場合_IllegalArgumentExceptionをスローする() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> new Loan(mockAsset, mockUser, LocalDate.now(), null, null));
            }
        }
    }

    @Nested
    @DisplayName("returnAsset")
    class ReturnAssetTest {

        @Test
        void returnAsset_ACTIVEの場合_返却日とステータスを更新できる() {
            Loan loan = createActiveLoan();
            LocalDate returnDate = LocalDate.now();

            loan.returnAsset(returnDate);

            assertThat(loan.getActualReturnDate()).isEqualTo(returnDate);
            assertThat(loan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        }

        @Test
        void returnAsset_OVERDUEの場合_返却日とステータスを更新できる() {
            Loan loan = createOverdueLoan();
            LocalDate returnDate = LocalDate.now();

            loan.returnAsset(returnDate);

            assertThat(loan.getActualReturnDate()).isEqualTo(returnDate);
            assertThat(loan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        }

        @Test
        void returnAsset_RETURNEDの場合_IllegalStateExceptionをスローする() {
            Loan loan = createActiveLoan();
            loan.returnAsset(LocalDate.now()); // 一度返却してRETURNEDにする

            assertThatIllegalStateException()
                    .isThrownBy(() -> loan.returnAsset(LocalDate.now().plusDays(1)))
                    .withMessage("Loan is not active");
        }

        @Test
        void returnAsset_returnDateがnullの場合_IllegalArgumentExceptionをスローする() {
            Loan loan = createActiveLoan();
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> loan.returnAsset(null));
        }
    }

    @Nested
    @DisplayName("isOverdue")
    class IsOverdueTest {

        @Test
        void isOverdue_ACTIVEかつ返却予定日を過ぎた場合_trueを返す() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));
            assertThat(loan.isOverdue(LocalDate.now())).isTrue();
        }

        @Test
        void isOverdue_ACTIVEかつ返却予定日当日の場合_falseを返す() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now());
            assertThat(loan.isOverdue(LocalDate.now())).isFalse();
        }

        @Test
        void isOverdue_ACTIVEかつ返却予定日前の場合_falseを返す() {
            Loan loan = createLoanWithDates(LocalDate.now(), LocalDate.now().plusDays(5));
            assertThat(loan.isOverdue(LocalDate.now())).isFalse();
        }

        @Test
        void isOverdue_OVERDUEかつ返却予定日を過ぎた場合_trueを返す() {
            Loan loan = createOverdueLoan();
            assertThat(loan.isOverdue(LocalDate.now())).isTrue();
        }

        @Test
        void isOverdue_RETURNEDの場合_日付に関わらずfalseを返す() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));
            loan.returnAsset(LocalDate.now().minusDays(1));

            assertThat(loan.isOverdue(LocalDate.now())).isFalse();
        }

        @Test
        void isOverdue_onDateがnullの場合_IllegalArgumentExceptionをスローする() {
            Loan loan = createActiveLoan();
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> loan.isOverdue(null));
        }
    }

    @Nested
    @DisplayName("markOverdueIfNeeded")
    class MarkOverdueIfNeededTest {

        @Test
        void markOverdueIfNeeded_ACTIVEかつ期限超過の場合_OVERDUEに遷移する() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));

            loan.markOverdueIfNeeded(LocalDate.now());

            assertThat(loan.getStatus()).isEqualTo(LoanStatus.OVERDUE);
        }

        @Test
        void markOverdueIfNeeded_ACTIVEかつ期限当日の場合_ACTIVEのままである() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now());

            loan.markOverdueIfNeeded(LocalDate.now());

            assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        }

        @Test
        void markOverdueIfNeeded_ACTIVEかつ期限前の場合_ACTIVEのままである() {
            Loan loan = createLoanWithDates(LocalDate.now(), LocalDate.now().plusDays(5));

            loan.markOverdueIfNeeded(LocalDate.now());

            assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        }

        @Test
        void markOverdueIfNeeded_すでにOVERDUEの場合_変化しない() {
            Loan loan = createOverdueLoan();

            loan.markOverdueIfNeeded(LocalDate.now());

            assertThat(loan.getStatus()).isEqualTo(LoanStatus.OVERDUE);
        }

        @Test
        void markOverdueIfNeeded_すでにRETURNEDの場合_変化しない() {
            Loan loan = createLoanWithDates(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));
            loan.returnAsset(LocalDate.now().minusDays(1));

            loan.markOverdueIfNeeded(LocalDate.now());

            assertThat(loan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        }

        @Test
        void markOverdueIfNeeded_onDateがnullの場合_IllegalArgumentExceptionをスローする() {
            Loan loan = createActiveLoan();
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> loan.markOverdueIfNeeded(null));
        }
    }

    // --- ヘルパーメソッド群 ---

    private Loan createActiveLoan() {
        return new Loan(mockAsset, mockUser, LocalDate.now(), LocalDate.now().plusDays(7), null);
    }

    private Loan createLoanWithDates(LocalDate loanDate, LocalDate expectedReturnDate) {
        return new Loan(mockAsset, mockUser, loanDate, expectedReturnDate, null);
    }

    private Loan createOverdueLoan() {
        Loan loan = new Loan(mockAsset, mockUser, LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), null);
        loan.markOverdueIfNeeded(LocalDate.now());
        return loan;
    }
}