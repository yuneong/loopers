package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Table(name = "user_coupons")
public class UserCoupon extends BaseEntity {

    private String userId;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "coupon_id")
//    private Coupon coupon;
    private Long couponId;
    private UserCouponStatus status = UserCouponStatus.AVAILABLE;
    private ZonedDateTime usedAt;
    private ZonedDateTime expiredAt;

    public void use() {
        validate();

        status = UserCouponStatus.USED;
        usedAt = ZonedDateTime.now();
    }

    public void validate() {
        if (status != UserCouponStatus.AVAILABLE) {
            throw new IllegalStateException("쿠폰을 사용할 수 없는 상태입니다.");
        }
        if (expiredAt != null && expiredAt.isBefore(ZonedDateTime.now())) {
            throw new IllegalStateException("쿠폰이 만료되었습니다.");
        }
    }

}
