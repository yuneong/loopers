package com.loopers.domain.point;

import com.loopers.application.point.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public Point charge(PointCommand command) {
        // command -> domain
        Point point = PointCommand.toDomain(command);
        // repository
        return pointRepository.charge(point);
    }

}
