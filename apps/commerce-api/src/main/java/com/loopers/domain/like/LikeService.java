package com.loopers.domain.like;

import com.loopers.infrastructure.like.dto.LikeCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Map<Long, Long> getLikeCounts(List<Long> productIds) {
        return likeRepository.countGroupByProductIds(productIds).stream()
                .collect(Collectors.toMap(
                        LikeCountDto::getProductId,
                        LikeCountDto::getCount
                ));
    }

    public Long getLikeCount(Long productId) {
        return likeRepository.countGroupByProductId(productId);
    }

}
