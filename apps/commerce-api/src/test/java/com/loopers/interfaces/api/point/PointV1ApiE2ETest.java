package com.loopers.interfaces.api.point;

import com.loopers.domain.user.Gender;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/points";
    private static final String ENDPOINT_USER = "/api/v1/users";

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(
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
     * - [x]  존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.
     * - [x]  존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class pointCharge {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void whenUserExistsAndCharge1000_returnsBalance() {
            // given
            UserV1Dto.UserRequest userRequest = new UserV1Dto.UserRequest(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            testRestTemplate.postForEntity(ENDPOINT_USER, userRequest, void.class);

            PointV1Dto.chargeRequest request = new PointV1Dto.chargeRequest("oyy", 1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.chargeResponse>> responseType = new ParameterizedTypeReference<ApiResponse<PointV1Dto.chargeResponse>>() {};

            // when
            ResponseEntity<ApiResponse<PointV1Dto.chargeResponse>> response = testRestTemplate.exchange(
                    ENDPOINT + "/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(1000L)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void throwsNotFound_whenUserNotExists() {
            // given
            UserV1Dto.UserRequest userRequest = new UserV1Dto.UserRequest(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            testRestTemplate.postForEntity(ENDPOINT_USER, userRequest, void.class);

            // when
            PointV1Dto.chargeRequest pointRequest = new PointV1Dto.chargeRequest("oyy11111", 1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.chargeResponse>> responseType = new ParameterizedTypeReference<ApiResponse<PointV1Dto.chargeResponse>>() {};
            ResponseEntity<ApiResponse<PointV1Dto.chargeResponse>> response = testRestTemplate.exchange(
                    ENDPOINT + "/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(pointRequest),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is4xxClientError()).isTrue(),
                    () -> assertThat(response.getStatusCode().value()).isEqualTo(404),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    /**
     * - [x]  포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.
     * - [x]  `X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/points")
    @Nested
    class getPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnsPoint_whenUserExists() {
            // given
            UserV1Dto.UserRequest userRequest = new UserV1Dto.UserRequest(
                    "oyy",
                    Gender.F,
                    "1999-08-21",
                    "loopers@gmail.com"
            );
            testRestTemplate.postForEntity(ENDPOINT_USER, userRequest, void.class);

            PointV1Dto.chargeRequest chargeRequest = new PointV1Dto.chargeRequest("oyy", 700L);
            testRestTemplate.postForEntity(ENDPOINT + "/charge", chargeRequest, void.class);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "oyy");

            ParameterizedTypeReference<ApiResponse<PointV1Dto.FindResponse>> responseType = new ParameterizedTypeReference<ApiResponse<PointV1Dto.FindResponse>>() {};

            // when
            ResponseEntity<ApiResponse<PointV1Dto.FindResponse>> response = testRestTemplate.exchange(
                    ENDPOINT,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(700L)
            );
        }

        @DisplayName("`X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void throwsBadRequest_whenUserIdIsNotInHeader() {
            // given
            HttpHeaders headers = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<PointV1Dto.FindResponse>> responseType = new ParameterizedTypeReference<ApiResponse<PointV1Dto.FindResponse>>() {};

            // when
            ResponseEntity<ApiResponse<PointV1Dto.FindResponse>> response = testRestTemplate.exchange(
                    ENDPOINT,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
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
}
