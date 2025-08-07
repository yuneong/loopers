package com.loopers.domain.coupon;


import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findById(Long couponId);

    Coupon save(Coupon coupon);

}
