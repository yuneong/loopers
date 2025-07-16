package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Point V1 API", description = "포인트(Point) API 입니다.")
public interface PointV1ApiSpec {

    @Operation(
        summary = "포인트 충전",
        description = "포인트를 충전합니다."
    )
    ApiResponse<PointV1Dto.chargeResponse> charge(
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "포인트 정보",
                required = true
        )
        PointV1Dto.chargeRequest request
    );

}
