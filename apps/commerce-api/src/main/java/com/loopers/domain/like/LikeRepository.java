package com.loopers.domain.like;


import com.loopers.infrastructure.like.dto.LikeCountDto;

import java.util.List;

public interface LikeRepository {

    List<LikeCountDto> countGroupByProductIds(List<Long> productIds);

    Long countGroupByProductId(Long productId);

}
