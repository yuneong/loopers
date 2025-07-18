package com.loopers.application.point;

import com.loopers.domain.point.Point;

public record PointCommand(
        String userId,
        long amount
) {
    public static Point toDomain(PointCommand command) {
        return Point.charge(
                command.userId,
                command.amount
        );
    }
}
