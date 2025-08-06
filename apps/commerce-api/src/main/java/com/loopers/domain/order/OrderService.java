package com.loopers.domain.order;

import com.loopers.application.order.OrderItemCommand;
import com.loopers.application.order.OrderItemFactory;
import com.loopers.domain.product.Product;
import com.loopers.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(
            List<OrderItemCommand> itemCommands,
            User user,
            List<Product> products
    ) {
        List<OrderItem> items = OrderItemFactory.createFrom(itemCommands, products);

        return Order.place(user, items);
    }

    public Order saveOrder(Order order) {
        // 주문 상태 변경
        order.updateOrderStatus(OrderStatus.PAID);

        return orderRepository.save(order);
    }

    public List<Order> getOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderDetail(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));
    }

}
