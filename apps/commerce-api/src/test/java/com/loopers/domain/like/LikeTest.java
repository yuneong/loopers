package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.user.User;
import com.loopers.support.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    User user = TestFixture.createUser();
    Product product = TestFixture.createProduct(TestFixture.createBrand());

    @DisplayName("Like 생성 시 likedYn은 Y 상태이다.")
    @Test
    void createLike() {
        // when
        Like like = Like.create(user, product);

        // then
        assertThat(like.getUser()).isEqualTo(user);
        assertThat(like.getProduct()).isEqualTo(product);
        assertThat(like.getLikedYn()).isEqualTo(LikeStatus.Y);
    }

    @DisplayName("like() 호출 시 상태가 Y로 변경된다.")
    @Test
    void like_shouldSetStatusToY() {
        // given
        Like like = new Like();
        like.unLike();

        // when
        like.like();

        // then
        assertThat(like.isLiked()).isTrue();
        assertThat(like.getLikedYn()).isEqualTo(LikeStatus.Y);
    }

    @DisplayName("unLike() 호출 시 상태가 N으로 변경된다.")
    @Test
    void unLike_shouldSetStatusToN() {
        // given
        Like like = new Like();
        like.like();

        // when
        like.unLike();

        // then
        assertThat(like.isLiked()).isFalse();
        assertThat(like.getLikedYn()).isEqualTo(LikeStatus.N);
    }

    @DisplayName("like()가 여러 번 호출되어도 상태는 Y로 유지된다.")
    @Test
    void like_isIdempotent() {
        // given
        Like like = new Like();
        like.like();

        // when
        like.like();
        like.like();

        // then
        assertThat(like.isLiked()).isTrue();
        assertThat(like.getLikedYn()).isEqualTo(LikeStatus.Y);
    }

    @DisplayName("unLike()가 여러 번 호출되어도 상태는 N로 유지된다.")
    @Test
    void unLike_isIdempotent() {
        // given
        Like like = new Like();
        like.unLike();

        // when
        like.unLike();
        like.unLike();

        // then
        assertThat(like.isLiked()).isFalse();
        assertThat(like.getLikedYn()).isEqualTo(LikeStatus.N);
    }

}
