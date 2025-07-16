package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCommand;
import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInfo;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointV1Dto.chargeResponse> charge(@Valid @RequestBody PointV1Dto.chargeRequest request) {
        // request -> command
        PointCommand command = PointV1Dto.chargeRequest.toCommand(request);
        // service
        PointInfo info = pointFacade.charge(command);
        // result -> response
        PointV1Dto.chargeResponse response = PointV1Dto.chargeResponse.from(info);

        return ApiResponse.success(response);
    }

}
