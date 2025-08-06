package com.loopers.domain.point;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.TestFixture;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = TestFixture.createUser();
        savedUser = userRepository.save(user);
    }

    @AfterEach
    void cleanDatabase() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 생성(create)")
    @Nested
    class CreatePoint {

        @DisplayName("포인트가 정상적으로 생성된다.")
        @Test
        void create_success() {
            // when
            Point point = pointService.create(savedUser);

            // then
            assertAll(
                    () -> assertThat(point).isNotNull(),
                    () -> assertThat(point.getUser()).isEqualTo(savedUser),
                    () -> assertThat(point.getBalance()).isEqualTo(0)
            );
        }
    }


    @DisplayName("포인트 충전(charge)")
    @Nested
    class ChargePoint {

        @DisplayName("정상적으로 포인트가 충전된다.")
        @Test
        void charge_success() {
            // given: 포인트가 없는 유저에 대해 생성 후 충전
            pointService.create(savedUser);

            // when
            Point charged = pointService.charge(savedUser, 5000L);

            // then
            assertAll(
                    () -> assertThat(charged).isNotNull(),
                    () -> assertThat(charged.getBalance()).isEqualTo(5000L)
            );
        }

        @DisplayName("존재하지 않는 유저의 포인트 충전 시 예외가 발생한다.")
        @Test
        void charge_fail_whenPointNotFound() {
            // given
            User user = userRepository.save(User.create("ghost", Gender.F, "2001-01-01", "ghost@example.com"));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                pointService.charge(user, 1000L);
            });
        }
    }

    @Nested
    @DisplayName("포인트 조회(getPoint)")
    class GetPoint {

        @Test
        @DisplayName("포인트가 존재하면 정상적으로 조회된다.")
        void getPoint_success() {
            // given
            Point created = pointService.create(savedUser);

            // when
            Point found = pointService.getPoint(savedUser);

            // then
            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(created.getId());
            assertThat(found.getUser()).isEqualTo(savedUser);
        }

        @Test
        @DisplayName("포인트가 존재하지 않으면 예외가 발생한다.")
        void getPoint_fail_whenPointNotFound() {
            // given (user save - x, point - x)
            User user = userRepository.save(User.create("ghost", Gender.F, "2001-01-01", "ghost@example.com"));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                pointService.getPoint(user);
            });
        }
    }

    @Nested
    @DisplayName("포인트 차감(checkAndUsePoint)")
    class UsePoint {

        @Test
        @DisplayName("충분한 포인트가 있을 경우 차감이 정상적으로 이뤄진다.")
        void usePoint_success() {
            // given
            pointService.create(savedUser);
            pointService.charge(savedUser, 10000L);
            Point point = pointService.getPoint(savedUser);

            // when
            Point used = pointService.checkAndUsePoint(point, 4000);

            // then
            assertThat(used.getBalance()).isEqualTo(6000L);
        }

        @Test
        @DisplayName("보유 포인트보다 많은 금액을 사용하려 할 경우 예외가 발생한다.")
        void usePoint_fail_dueToInsufficientBalance() {
            // given
            pointService.create(savedUser);
            Point point = pointService.getPoint(savedUser);

            // when & then
            assertThrows(IllegalStateException.class, () -> {
                pointService.checkAndUsePoint(point, 5000);
            });
        }
    }
}
