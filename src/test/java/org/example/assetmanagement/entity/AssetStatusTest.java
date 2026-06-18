package org.example.assetmanagement.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AssetStatusTest {

    @ParameterizedTest(name = "{0} から {1} への遷移は {2} であること")
    @CsvSource({
            "AVAILABLE, LOANED, true",      "AVAILABLE, MAINTENANCE, true",  "AVAILABLE, RETIRED, true",     "AVAILABLE, AVAILABLE, false",
            "LOANED, AVAILABLE, true",      "LOANED, MAINTENANCE, true",     "LOANED, LOANED, false",        "LOANED, RETIRED, false",
            "MAINTENANCE, AVAILABLE, true", "MAINTENANCE, RETIRED, true",    "MAINTENANCE, MAINTENANCE, false", "MAINTENANCE, LOANED, false",
            "RETIRED, AVAILABLE, false",    "RETIRED, LOANED, false",        "RETIRED, MAINTENANCE, false",   "RETIRED, RETIRED, false"
    })
    void canTransitTo_状態遷移マトリクスの検証(AssetStatus current, AssetStatus next, boolean expected) {
        assertThat(current.canTransitTo(next)).isEqualTo(expected);
    }

    @Test
    void canTransitTo_nextStatusがnullの場合_falseを返す() {
        assertThat(AssetStatus.AVAILABLE.canTransitTo(null)).isFalse();
    }
}