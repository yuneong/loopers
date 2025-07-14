package com.loopers.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User V1 API", description = "유저(User) API 입니다.")
public interface UserV1ApiSpec {

    @Operation(
        summary = "회원가입",
        description = "유저 정보를 입력받아 신규 유저를 생성합니다"
    )
    UserV1Dto.UserResponse singUp(
        @RequestHeader(name = "X-USER-ID", required = true)
        @Schema(description = "유저 ID")
        String userId,

        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "유저 정보",
                required = true
        )
        UserV1Dto.UserRequest request
    );

}
