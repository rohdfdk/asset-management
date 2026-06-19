package org.example.assetmanagement.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LoanStatusTest {

    @ParameterizedTest(name = "{0} から {1} への遷移は {2} であること")
    @CsvSource({
            // Active からの遷移
            "ACTIVE, OVERDUE, true", "ACTIVE, RETURNED, true", "ACTIVE, ACTIVE, false",
            // OVERDUE からの遷移
            "OVERDUE, RETURNED, true", "OVERDUE, ACTIVE, false", "OVERDUE, OVERDUE, false",
            // RETURNED からの遷移
            "RETURNED, ACTIVE, false", "RETURNED, OVERDUE, false", "RETURNED, RETURNED, false"
    })
    void canTransitTo_状態遷移マトリクスの検証(LoanStatus current, LoanStatus next, boolean expected) {
        assertThat(current.canTransitTo(next)).isEqualTo(expected);
    }

    @Test
    void canTransitTo_nextStatusがnullの場合_falseを返す() {
        assertThat(LoanStatus.ACTIVE.canTransitTo(null)).isFalse();
    }
}
