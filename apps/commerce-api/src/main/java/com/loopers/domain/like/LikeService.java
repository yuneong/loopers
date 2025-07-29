package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.like.dto.LikeCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
    public Like like(Product product, User user) {
        Optional<Like> maybeLike = likeRepository.findByProductAndUser(product, user);

        Like like = Like.likeOrCreate(maybeLike, user, product);

        return likeRepository.save(like);
    }

    @Transactional
    public Like unLike(Product product, User user) {
        Like like = likeRepository.findByProductAndUser(product, user)
                .orElseThrow(() -> new IllegalArgumentException("Like not found for product and user"));

        like.unLike();

        return likeRepository.save(like);
    }

    public List<Like> getLikeProducts(User user) {
        return likeRepository.findByUser(user);
    }

}
