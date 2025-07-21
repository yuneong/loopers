package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Point V1 API", description = "포인트(Point) API 입니다.")
public interface PointV1ApiSpec {

    @Operation(
        summary = "포인트 충전",
        description = "포인트를 충전합니다."
    )
    ApiResponse<PointV1Dto.ChargeResponse> charge(
            @Parameter(
                    name = "X-USER-ID",
                    description = "유저 ID (헤더)",
                    required = true
            )
            @RequestHeader("X-USER-ID") String userId,

            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "포인트 정보",
                    required = true
            )
            PointV1Dto.ChargeRequest request
    );

    @Operation(
        summary = "보유 포인트 조회",
        description = "사용자의 보유 포인트를 조회합니다."
    )
    ApiResponse<PointV1Dto.FindResponse> getPoint(
            @Parameter(
                    name = "X-USER-ID",
                    description = "유저 ID (헤더)",
                    required = true
            )
            @RequestHeader("X-USER-ID") String userId
    );

}
