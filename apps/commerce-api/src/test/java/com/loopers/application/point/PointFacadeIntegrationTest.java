package com.loopers.application.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointFacadeIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanDatabase() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [x]  존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
     */
    @DisplayName("포인트 충전 시,")
    @Nested
    class pointCharge {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void failToCharge_whenUserIdDoesNotExist() {
            // given
            PointCommand command = new PointCommand("oyy", 500L);

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                pointFacade.charge(command);
            });

            assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorType());
        }

    }

}
