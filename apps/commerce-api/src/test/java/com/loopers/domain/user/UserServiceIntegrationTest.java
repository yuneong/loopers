package com.loopers.domain.user;

import com.loopers.application.user.UserCommand;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanDatabase() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [x]  회원 가입시 User 저장이 수행된다. ( spy 검증 )
     * - [x]  이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     */
    @DisplayName("회원 가입 시,")
    @Nested
    class signUp {

        @DisplayName("User 저장이 수행된다.")
        @Test
        void successToSave_whenSigningUp() {
            // given
            UserCommand command = new UserCommand(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );

            // when
            User user = userService.signUp(command);

            // then
            verify(userRepository, times(1)).save(any(User.class));
            assertThat(user.getUserId()).isEqualTo("oyy");
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다.")
        @Test
        void failToSignUp_whenUserIdAlreadyExists() {
            // given
            UserCommand command = new UserCommand(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            User user1 = userService.signUp(command);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                userService.signUp(command);
            });
        }
    }

    /**
     * - [x]  해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
     * - [x]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @DisplayName("회원 정보 조회 시,")
    @Nested
    class getMyInfo {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsMyInfo_whenUserExists() {
            // given
            UserCommand command = new UserCommand(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            User user = userService.signUp(command);

            // when
            User findUser = userService.getMyInfo(user.getUserId());

            // then
            assertAll(
                    () -> assertThat(findUser).isNotNull(),
                    () -> assertThat(findUser.getUserId()).isEqualTo(user.getUserId()),
                    () -> assertThat(findUser.getGender()).isEqualTo(user.getGender()),
                    () -> assertThat(findUser.getBirth()).isEqualTo(user.getBirth()),
                    () -> assertThat(findUser.getEmail()).isEqualTo(user.getEmail())
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsNull_whenUserDoesNotExist() {
            // given
            String userId = UUID.randomUUID().toString(); // 존재하지 않을만한 ID 생성

            // when
            User findUser = userService.getMyInfo(userId);

            // then
            assertThat(findUser).isNull();
            assertThat(findUser).isEqualTo(null);
        }
    }

}
