package org.example.assetmanagement.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatusTest {

    @ParameterizedTest(name = "{0} から {1} への遷移は {2} であること")
    @CsvSource({
            "ACTIVE, INACTIVE, true",      "ACTIVE, ACTIVE, false",
            "INACTIVE, ACTIVE, true",      "INACTIVE, INACTIVE, false"
    })
    void canTransitTo_状態遷移マトリクスの検証(UserStatus current, UserStatus next, boolean expected) {
        assertThat(current.canTransitTo(next)).isEqualTo(expected);
    }

    @Test
    void canTransitTo_nextStatusがnullの場合_falseを返す() {
        assertThat(UserStatus.ACTIVE.canTransitTo(null)).isFalse();
    }
}
