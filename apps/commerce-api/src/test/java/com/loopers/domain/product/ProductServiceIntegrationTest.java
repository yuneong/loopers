package com.loopers.domain.product;

import com.loopers.application.product.ProductCommand;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.order.OrderItem;
import com.loopers.support.TestFixture;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private Brand testBrand;

    @BeforeEach
    void setupTestData() {
        testBrand = brandRepository.save(Brand.create(
                "브랜드명",
                "브랜드설명",
                "https://example.com/brand-image.jpg"
        ));

        List<Product> products = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> Product.create(
                        testBrand,
                        "상품" + i,
                        "상품설명",
                        "https://example.com/product-image.jpg",
                        i * 1000,
                        10
                ))
                .toList();

        productRepository.saveAll(products);
    }

    @AfterEach
    void cleanDatabase() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 목록 조회 시,")
    @Nested
    class getProducts {

        @DisplayName("상품 목록이 정상적으로 조회된다.")
        @Test
        void successToGetProducts() {
            // given
            ProductCommand command = new ProductCommand(
                    testBrand.getId(),
                    0,
                    20,
                    Sort.by(Sort.Direction.ASC, "price")
            );

            // when
            Page<Product> products = productService.getProducts(command);

            // then
            assertThat(products).isNotNull();
            assertThat(products.getTotalElements()).isEqualTo(30);
            assertThat(products.getContent()).hasSize(20);
            assertThat(products.getContent().get(0).getPrice()).isEqualTo(1000);
        }

        @DisplayName("존재하지 않는 브랜드 ID가 주어지면 빈 목록이 반환된다.")
        @Test
        void returnEmpty_whenBrandIdNotExist() {
            // given
            Long notExistBrandId = 999L;
            ProductCommand command = new ProductCommand(
                    notExistBrandId,
                    0,
                    20,
                    Sort.unsorted()
            );

            // when
            Page<Product> products = productService.getProducts(command);

            // then
            assertThat(products).isNotNull();
            assertThat(products.getTotalElements()).isEqualTo(0);
            assertThat(products.getContent()).isEmpty();
        }

        @DisplayName("정렬 기준이 없을 경우 기본 정렬로 동작한다.")
        @Test
        void fallbackToDefaultSort_whenInvalidSortProvided() {
            // given
            ProductCommand command = new ProductCommand(
                    testBrand.getId(),
                    0,
                    20,
                    null
            );

            // when
            Page<Product> products = productService.getProducts(command);
            List<Product> content = products.getContent();

            // then
            assertThat(products).isNotNull();
            assertThat(products.getContent()).isNotEmpty();
            assertThat(content.get(0).getCreatedAt()).isAfterOrEqualTo(content.get(1).getCreatedAt());
        }

        @DisplayName("page가 음수일 경우 기본값(0)으로 조회된다.")
        @Test
        void fallbackToDefaultPage_whenPageIsNegative() {
            // given
            ProductCommand command = new ProductCommand(
                    testBrand.getId(),
                    -1,
                    20,
                    Sort.unsorted()
            );

            // when
            Page<Product> products = productService.getProducts(command);

            // then
            assertThat(products).isNotNull();
            assertThat(products.getContent()).hasSize(20);
        }

        @DisplayName("page가 유효한 범위를 초과하면 빈 목록이 반환된다.")
        @Test
        void returnEmpty_whenPageOutOfRange() {
            // given
            ProductCommand command = new ProductCommand(
                    testBrand.getId(),
                    999,
                    20,
                    Sort.unsorted()
            );

            // when
            Page<Product> products = productService.getProducts(command);

            // then
            assertThat(products).isNotNull();
            assertThat(products.getContent()).isEmpty();
        }

        @DisplayName("size가 0 이하일 경우 기본값(20)으로 조회된다.")
        @Test
        void fallbackToDefaultSize_whenSizeIsInvalid() {
            // given
            ProductCommand command = new ProductCommand(
                    testBrand.getId(),
                    0,
                    0,
                    Sort.unsorted()
            );

            // when
            Page<Product> products = productService.getProducts(command);

            // then
            assertThat(products).isNotNull();
            assertThat(products.getContent()).hasSize(20);
        }
    }

    @DisplayName("상품 상세 조회 시,")
    @Nested
    class getProductDetail {

        @DisplayName("존재하는 상품 ID로 조회 시, 상품 정보가 반환된다.")
        @Test
        void returnsProductDetail_whenProductExists() {
            // given
            Pageable pageable = PageRequest.of(0, 1);
            ProductSearchCondition condition = new ProductSearchCondition(testBrand.getId(), pageable);
            Product product = productRepository.findByCondition(condition).getContent().get(0);

            // when
            Product foundProduct = productService.getProductDetail(product.getId());

            // then
            assertAll(
                    () -> assertThat(foundProduct).isNotNull(),
                    () -> assertThat(foundProduct.getId()).isEqualTo(product.getId()),
                    () -> assertThat(foundProduct.getName()).isEqualTo(product.getName()),
                    () -> assertThat(foundProduct.getPrice()).isEqualTo(product.getPrice())
            );
        }

        @DisplayName("존재하지 않는 상품 ID로 조회 시, 예외가 발생한다.")
        @Test
        void throwIllegalArgumentException_whenProductIdIdNotExist() {
            // given
            Long notExistProductId = 999L;

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                productService.getProductDetail(notExistProductId);
            });
        }

    }

    @DisplayName("상품 ID 목록으로 조회 시,")
    @Nested
    class getProductsByIds {

        @Test
        @DisplayName("존재하는 상품 ID 리스트로 상품들을 정상 조회한다.")
        void success_whenProductIdsExist() {
            // given
            List<Product> savedProducts = productRepository.findByAll().subList(0, 3);
            List<Long> productIds = savedProducts.stream()
                    .map(Product::getId)
                    .toList();

            // when
            List<Product> foundProducts = productService.getProductsByIds(productIds);

            // then
            assertThat(foundProducts).hasSize(3);
            assertThat(foundProducts)
                    .extracting(Product::getId)
                    .containsExactlyElementsOf(productIds);
        }

        @Test
        @DisplayName("빈 ID 리스트를 전달하면 빈 결과를 반환한다.")
        void returnEmpty_whenProductIdsIsEmpty() {
            // given
            List<Long> productIds = List.of();

            // when
            List<Product> products = productService.getProductsByIds(productIds);

            // then
            assertThat(products).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID를 포함해도, 존재하는 상품만 조회된다.")
        void returnPartial_whenSomeIdsDoNotExist() {
            // given
            List<Product> savedProducts = productRepository.findByAll().subList(0, 2);
            List<Long> productIds = savedProducts.stream()
                    .map(Product::getId)
                    .toList();

            List<Long> mixedIds = List.of(productIds.get(0), 999L, productIds.get(1));

            // when
            List<Product> found = productService.getProductsByIds(mixedIds);

            // then
            assertThat(found).hasSize(2);
            assertThat(found)
                    .extracting(Product::getId)
                    .containsExactlyInAnyOrderElementsOf(productIds);
        }

    }

    @DisplayName("상품 재고 차감 시,")
    @Nested
    class CheckAndDecreaseStock {

        @DisplayName("재고가 충분하면 정상적으로 차감된다.")
        @Test
        void successToDecreaseStock() {
            // given
            Product product = productRepository.findByAll().get(0); // 재고 10
            List<OrderItem> items = TestFixture.createOrderItems(product, 3); // 수량 3

            // when
            productService.checkAndDecreaseStock(items);

            // then
            Product updated = productRepository.findById(product.getId()).orElseThrow();
            assertThat(updated.getStock()).isEqualTo(7);
        }

        @DisplayName("차감 수량이 현재 재고보다 많으면 예외가 발생한다.")
        @Test
        void failToDecreaseStock_whenInsufficientStock() {
            // given
            Product product = productRepository.findByAll().get(2); // 재고 10
            List<OrderItem> items = TestFixture.createOrderItems(product, 15); // 수량 15

            // when & then
            assertThrows(IllegalStateException.class, () -> {
                productService.checkAndDecreaseStock(items);
            });
        }
    }

}
