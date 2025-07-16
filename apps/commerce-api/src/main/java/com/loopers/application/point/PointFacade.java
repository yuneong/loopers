package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {

    private final PointService pointService;

    public PointInfo charge(PointCommand command) {
        // service
        Point point = pointService.charge(command);
        // domain -> result
        return PointInfo.from(point);
    }

}
