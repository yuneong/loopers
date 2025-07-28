package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeRepository;
import com.loopers.infrastructure.like.dto.LikeCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public List<LikeCountDto> countGroupByProductIds(List<Long> productIds) {
        return likeJpaRepository.countGroupByProductIds(productIds);
    }

    @Override
    public Long countGroupByProductId(Long productId) {
        return likeJpaRepository.countGroupByProductId(productId);
    }

}
