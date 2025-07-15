package com.loopers.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    /**
     * - [x]  ID가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [x]  이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [x]  생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     */
    @DisplayName("회원 가입 시,")
    @Nested
    class signUp {

        @DisplayName("ID가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "아이디윤영",
                "loopers123",
                "loopers!@#",
                "윤영loopers!@#123",
                " "
        })
        void throwsIllegalArgumentException_whenUserIdIsInvalid(String userId) {
            // given
            String gender = "FEMALE";
            String birth = "1999-08-21";
            String email = "loopers@gmail.com";

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                User.save(userId, gender, birth, email);
            });
        }

        @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "loopers!gmail.com",
                "loopersgmail.com",
                "loopers@.com",
                "loopers@gmail",
                "loopersgmailcom",
        })
        void throwsIllegalArgumentException_whenEmailIsInvalid(String email) {
            // given
            String userId = "loopers";
            String gender = "FEMALE";
            String birth = "1999-08-21";

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                User.save(userId, gender, birth, email);
            });
        }

        @DisplayName("생년월일이 'yyyy-MM-dd' 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "1999/08/21",
                "1999.08.21",
                "1999-08-32",
                "1999-13-01",
                "2023-08-21",
        })
        void throwsIllegalArgumentException_whenBirthIsInvalid(String birth) {
            // given
            String userId = "oyy";
            String gender = "FEMALE";
            String email = "loopers@gmail.com";

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                User.save(
                        userId,
                        gender,
                        birth,
                        email
                );
            });
        }

    }

}
