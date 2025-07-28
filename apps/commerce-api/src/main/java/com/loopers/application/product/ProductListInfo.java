package com.loopers.application.product;

import com.loopers.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public record ProductListInfo(
        List<ProductContent> contents,
        Pageable pageable
) {

    public static ProductListInfo from(Page<Product> products, Map<Long, Long> likeCounts) {
        List<ProductContent> productContents = products.getContent().stream()
                .map(product -> ProductContent.from(
                        product,
                        likeCounts.getOrDefault(product.getId(), 0L).longValue()
                ))
                .toList();

        return new ProductListInfo(
                productContents,
                products.getPageable()
        );
    }

}
