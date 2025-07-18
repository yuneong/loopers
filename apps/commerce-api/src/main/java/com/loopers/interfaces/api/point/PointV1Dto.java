package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCommand;
import com.loopers.application.point.PointInfo;

public class PointV1Dto {

    // 포인트 충전
    public record ChargeRequest(
            long amount
    ) {
        public static PointCommand toCommand(String userId, ChargeRequest request) {
            return new PointCommand(
                    userId,
                    request.amount
            );
        }
    }

    public record ChargeResponse(
            String userId,
            long balance
    ) {
        public static ChargeResponse from(PointInfo info) {
            return new ChargeResponse(
                    info.userId(),
                    info.balance()
            );
        }
    }

    // 포인트 조회
    public record FindResponse(
            String userId,
            long balance
    ) {
        public static FindResponse from(PointInfo info) {
            return new FindResponse(
                    info.userId(),
                    info.balance()
            );
        }
    }

}
