package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User V1 API", description = "유저(User) API 입니다.")
public interface UserV1ApiSpec {

    @Operation(
        summary = "회원가입",
        description = "유저 정보를 입력받아 신규 유저를 생성합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> singUp(
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "유저 정보",
                required = true
        )
        UserV1Dto.UserRequest request
    );

    @Operation(
            summary = "내 정보 조회",
            description = "'X-USER-ID' 헤더로 유저 ID를 전달받아 내 정보를 조회합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> getMyInfo(
        @Parameter(
                name = "X-USER-ID",
                description = "유저 ID (헤더)",
                required = true
        )
        @RequestHeader("X-USER-ID") String userId
    );

}
