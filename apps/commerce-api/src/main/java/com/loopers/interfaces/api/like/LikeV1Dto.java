package com.loopers.interfaces.api.like;


import com.loopers.application.like.LikeContent;
import com.loopers.application.like.LikeInfo;
import com.loopers.application.like.LikeListInfo;
import com.loopers.domain.product.Product;

import java.util.List;

public class LikeV1Dto {

    public record LikeResponse(
            String likedYn,
            Long likeCount
    ) {
        public static LikeResponse from(LikeInfo info) {
            return new LikeResponse(
                    info.likedYn(),
                    info.likeCount()
            );
        }
    }

    public record LikeContentResponse(
            Product product,
            Long likeCount,
            String likedYn
    ) {
        public static LikeContentResponse from(LikeContent content) {
            return new LikeContentResponse(
                    content.product(),
                    content.likeCount(),
                    content.likedYn()
            );
        }
    }

    public record LikeListResponse(
            List<LikeContentResponse> contents
    ) {
        public static LikeListResponse from(LikeListInfo info) {
            return new LikeListResponse(
                    info.contents().stream()
                            .map(LikeContentResponse::from)
                            .toList()
            );
        }
    }

}
