package com.loopers.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ProductQuantity {

    private final Product product;
    private final int quantity;

    public void decreaseQuantity(int quantity) {
        product.decreaseStock(quantity);
    }

}
