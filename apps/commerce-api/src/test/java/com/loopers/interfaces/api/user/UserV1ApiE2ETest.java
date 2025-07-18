package com.loopers.interfaces.api.user;

import com.loopers.domain.user.Gender;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/users";

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void cleanDatabase() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [x]  회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.
     * - [x]  회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/users")
    @Nested
    class signUp {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void successToSignUp_returnsUserInfo() {
            // given
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    ENDPOINT,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(request.userId())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void throwsBadRequest_whenGenderIsNotProvided() {
            // given
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "oyy",
                    null,
                    "1999-08-21",
                    "loopers@gmail.com"
            );

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    ENDPOINT,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is4xxClientError()).isTrue(),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL)
            );
        }

    }

    /**
     * - [x]  내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.
     * - [x]  존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/users/me")
    @Nested
    class getMyInfo {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void successToGetMyInfo_returnsUserInfo() {
            // given
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            testRestTemplate.postForEntity(ENDPOINT, request, Void.class);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "oyy");

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    ENDPOINT + "/me",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(request.userId())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void throwsNotFound_whenUserIdDoesNotExist() {
            // given
            String userId = UUID.randomUUID().toString(); // 존재하지 않을만한 ID 생성

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    ENDPOINT + "/me",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is4xxClientError()).isTrue(),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL)
            );
        }
    }

}
