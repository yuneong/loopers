package com.loopers.application.point;


public record PointCommand(
        String userId,
        long amount
) {

}
