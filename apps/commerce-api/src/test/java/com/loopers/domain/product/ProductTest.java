package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @DisplayName("상품 생성 시,")
    @Nested
    class CreateProduct {

        Brand dummyBrand = new Brand();

        @DisplayName("브랜드가 null이면 예외가 발생한다")
        @Test
        void throwsException_whenBrandIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    Product.create(
                            null,
                            "상품명",
                            "상품설명",
                            "https://example.com/image.jpg",
                            1000,
                            10
                    )
            );
        }

        @DisplayName("상품명은 한글 또는 영어, 숫자만 허용되며, 공백이나 특수문자가 포함되면 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "예시상품@@#",
                "@#$%^&*()_+",
                "a12 345$#Q"
        })
        void throwsIllegalArgumentException_whenNameIsInvalid(String name) {
            assertThrows(IllegalArgumentException.class, () ->
                    Product.create(
                            dummyBrand,
                            name,
                            "상품설명",
                            "https://example.com/image.jpg",
                            1000,
                            10
                    )
            );
        }

        @DisplayName("가격이 0 이하이면 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {
                0,
                -1,
                -100
        })
        void throwsIllegalArgumentException_whenPriceIsNegative(int price) {
            assertThrows(IllegalArgumentException.class, () ->
                    Product.create(
                            dummyBrand,
                            "상품명",
                            "상품설명",
                            "https://example.com/image.jpg",
                            price,
                            10
                    )
            );
        }

        @DisplayName("재고가 0 이하이면 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {
                0,
                -1,
                -100
        })
        void throwsIllegalArgumentException_whenStockIsNegative(int stock) {
            assertThrows(IllegalArgumentException.class, () ->
                    Product.create(
                            dummyBrand,
                            "상품명",
                            "설명",
                            "https://example.com/image.jpg",
                            1000,
                            stock
                    )
            );
        }

        @DisplayName("imageUrl이 빈 문자열이거나 형식이 올바르지 않으면 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "  ",
                "img.jpg",
                "https://example.com/image",
                "https://example.com/image.jpggg",
        })
        void throwsIllegalArgumentException_whenImageUrlIsInvalid(String imageUrl) {
            assertThrows(IllegalArgumentException.class, () ->
                    Product.create(
                            dummyBrand,
                            "상품명",
                            "설명",
                            imageUrl,
                            1000,
                            10
                    )
            );
        }

        @Test
        @DisplayName("정상적인 값이면 Product가 생성된다.")
        void success_whenAllValuesAreValid() {
            Product product = Product.create(
                    dummyBrand,
                    "상품명",
                    "설명",
                    "https://example.com/image.jpg",
                    1000,
                    10
            );

            assertNotNull(product);
            assertEquals("상품명", product.getName());
            assertEquals("ACTIVE", product.getStatus());
        }
    }

}
