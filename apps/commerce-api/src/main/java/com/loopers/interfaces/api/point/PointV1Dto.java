package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCommand;
import com.loopers.application.point.PointInfo;
import jakarta.validation.constraints.NotNull;

public class PointV1Dto {

    // 포인트 충전
    public record chargeRequest(
            @NotNull String userId,
            long amount
    ) {
        public static PointCommand toCommand(chargeRequest request) {
            return new PointCommand(
                    request.userId,
                    request.amount
            );
        }
    }

    public record chargeResponse(
            String userId,
            long balance
    ) {
        public static chargeResponse from(PointInfo info) {
            return new chargeResponse(
                    info.userId(),
                    info.balance()
            );
        }
    }

}
