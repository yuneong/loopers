package com.loopers.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    /**
     * - [x]  0 이하의 정수로 포인트를 충전 시 실패한다.
     */
    @DisplayName("포인트 충전 시,")
    @Nested
    class pointCharge {

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {0L, -100L, -1L})
        void throwsIllegalArgumentException_whenChargeAmountIsZeroOrNegative(long amount) {
            // given
            String userId = "oyy";
            Point point = Point.create(userId);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                point.charge(amount);
            });
        }
    }

}
