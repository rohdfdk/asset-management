package org.example.assetmanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Loan")
class LoanTest {

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {

        @Test
        void constructor_有効な値を指定した場合_貸出を生成できる() {
            Asset asset = new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null);
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            LocalDate loanDate = LocalDate.now();
            LocalDate expectedReturnDate = loanDate.plusDays(7);

            Loan loan = new Loan(asset, user, loanDate, expectedReturnDate, "備考");

            assertEquals(asset, loan.getAsset());
            assertEquals(user, loan.getUser());
            assertEquals(loanDate, loan.getLoanDate());
            assertEquals(expectedReturnDate, loan.getExpectedReturnDate());
            assertEquals(Loan.STATUS_ACTIVE, loan.getStatus());
            assertEquals("備考", loan.getRemarks());
        }

        @Test
        void constructor_expectedReturnDateがloanDateより前の場合_IllegalArgumentExceptionをスローする() {
            Asset asset = new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null);
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            LocalDate loanDate = LocalDate.now();

            assertThrows(IllegalArgumentException.class,
                    () -> new Loan(asset, user, loanDate, loanDate.minusDays(1), "備考"));
        }
    }

    @Nested
    @DisplayName("returnAsset")
    class ReturnAssetTest {

        @Test
        void returnAsset_ACTIVEの場合_返却日とステータスを更新できる() {
            Loan loan = activeLoan();
            LocalDate returnDate = LocalDate.now();

            loan.returnAsset(returnDate);

            assertEquals(returnDate, loan.getActualReturnDate());
            assertEquals(Loan.STATUS_RETURNED, loan.getStatus());
        }

        @Test
        void returnAsset_RETURNEDの場合_IllegalStateExceptionをスローする() {
            Loan loan = activeLoan();
            loan.returnAsset(LocalDate.now());

            assertThrows(IllegalStateException.class,
                    () -> loan.returnAsset(LocalDate.now().plusDays(1)));
        }
    }

    @Nested
    @DisplayName("overdue")
    class OverdueTest {

        @Test
        void isOverdue_返却予定日を過ぎたACTIVEの場合_trueを返す() {
            Loan loan = new Loan(
                    new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null),
                    new User("john", "encoded-password", "john@example.com", "John Doe", "USER"),
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1),
                    null
            );

            assertTrue(loan.isOverdue(LocalDate.now()));
        }

        @Test
        void markOverdueIfNeeded_期限超過のACTIVEの場合_OVERDUEに遷移する() {
            Loan loan = new Loan(
                    new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null),
                    new User("john", "encoded-password", "john@example.com", "John Doe", "USER"),
                    LocalDate.now().minusDays(10),
                    LocalDate.now().minusDays(1),
                    null
            );

            loan.markOverdueIfNeeded(LocalDate.now());

            assertEquals(Loan.STATUS_OVERDUE, loan.getStatus());
            assertTrue(loan.isOverdue(LocalDate.now()));
        }

        @Test
        void isOverdue_RETURNEDの場合_falseを返す() {
            Loan loan = activeLoan();
            loan.returnAsset(LocalDate.now());

            assertFalse(loan.isOverdue(LocalDate.now().plusDays(10)));
        }
    }

    private Loan activeLoan() {
        return new Loan(
                new Asset("PC-001", "Laptop", null, "IT", AssetStatus.AVAILABLE, null),
                new User("john", "encoded-password", "john@example.com", "John Doe", "USER"),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );
    }
}
