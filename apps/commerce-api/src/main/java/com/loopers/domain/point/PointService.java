package com.loopers.domain.point;

import com.loopers.application.point.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point create(String userId) {
        // create new Point
        Point point = Point.create(userId);
        // repository
        return pointRepository.save(point);
    }

    @Transactional
    public Point charge(PointCommand command) {
        // validation point exists for userId
        String userId = command.userId();
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found for userId: " + userId));

        // command -> domain
        point.charge(command.amount());
        // repository
        return pointRepository.save(point);
    }

    public Point getPoint(String userId) {
        // repository
        return pointRepository.findByUserId(userId).orElse(null);
    }

}
