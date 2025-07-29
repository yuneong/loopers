package com.loopers.application.like;

import com.loopers.domain.product.Product;

public record LikeContent(
        Product product,
        Long likeCount,
        String likedYn
) {

    public static LikeContent from(Product product, Long likeCount, String likedYn) {
        return new LikeContent(
                product,
                likeCount,
                likedYn
        );
    }

}
