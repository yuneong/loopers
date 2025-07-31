package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    int quantity;

    int price;

    public static OrderItem create(Product product, int quantity, int price) {
        OrderItem orderItem = new OrderItem();

        orderItem.product = product;
        orderItem.quantity = quantity;
        orderItem.price = price;

        return orderItem;
    }

}
