package com.loopers.application.like;


public record LikeInfo(
        String likedYn,
        Long likeCount
) {

    public static LikeInfo of(String likedYn, Long likeCount) {
        return new LikeInfo(
                likedYn,
                likeCount
        );
    }

}
