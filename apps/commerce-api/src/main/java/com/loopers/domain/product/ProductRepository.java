package com.loopers.domain.product;


import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductRepository {

    Page<Product> findByCondition(ProductSearchCondition condition);

    Optional<Product> findById(Long productId);

}
