package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems = new ArrayList<>();

    int totalPrice;

    OrderStatus status;

    ZonedDateTime paidAt;

    public static Order place(User user, List<OrderItem> items) {
        Order order = new Order();

        order.user = user;
        for (OrderItem item : items) {
            order.addItem(item);
        }
        order.totalPrice = order.calculateTotalPrice();
        order.status = OrderStatus.PLACED;
        order.paidAt = ZonedDateTime.now();

        return order;
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
    }

    public int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getPrice)
                .sum();
    }

    public void updateOrderStatus(OrderStatus status) {
        this.status = status;
    }

}
