package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "points")
public class Point extends BaseEntity {

    private String userId;
    private long amount;
    private long balance; // 총 보유 잔액

    public static Point charge(
            String userId,
            long amount
    ) {
        Point point = new Point();
        point.userId = userId;
        point.amount = amount;
        point.balance += amount; // 충전 시 잔액 증가

        return point;
    }

}
