package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCommand;
import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointV1Dto.ChargeResponse> charge(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PointV1Dto.ChargeRequest request
    ) {
        // request -> command
        PointCommand command = PointV1Dto.ChargeRequest.toCommand(userId, request);
        // service
        PointInfo info = pointFacade.charge(command);
        // result -> response
        PointV1Dto.ChargeResponse response = PointV1Dto.ChargeResponse.from(info);

        return ApiResponse.success(response);
    }

    @GetMapping("")
    @Override
    public ApiResponse<PointV1Dto.FindResponse> getPoint(@RequestHeader("X-USER-ID") String userId) {
        // service
        PointInfo pointInfo = pointFacade.getPoint(userId);
        // result -> response
        PointV1Dto.FindResponse response = PointV1Dto.FindResponse.from(pointInfo);

        return ApiResponse.success(response);
    }

}
