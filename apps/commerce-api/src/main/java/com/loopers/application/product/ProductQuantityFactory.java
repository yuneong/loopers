package com.loopers.application.product;

import com.loopers.application.order.OrderItemCommand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductQuantityFactory {

    public static List<ProductQuantity> createFrom(
            List<OrderItemCommand> itemCommands,
            List<Product> products
    ) {

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return itemCommands.stream()
                .map(item -> {
                    return ProductQuantity.of(productMap.get(item.productId()), item.quantity());
                })
                .toList();
    }

}
