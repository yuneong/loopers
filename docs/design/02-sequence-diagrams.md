# [시퀀스 다이어그램]

## 1. 유저 (Users) <-> 브랜드 & 상품 (Brands & Products)

### ✅ 1-1. 브랜드 정보 조회
```mermaid
sequenceDiagram
    actor User
    participant BrandController
    participant BrandFacade
    participant BrandService

    User->>BrandController: GET /api/v1/brands/{brandId}
    alt brandId 존재 ❌
        BrandController-->>User: 404 Not Found
    end
    
    BrandController->>BrandService: getBrandDetail(brandId)
    BrandService-->>User: brandDetail
```

### ✅ 1-2. GET /api/v1/products 상품 목록 조회
```mermaid
sequenceDiagram
    actor User
    participant ProductController
    participant ProductFacade
    participant ProductService
    participant LikeService

    %% /api/v1/products?brandId=1&sort=createdAt,desc&sort=price,asc&sort=likes,desc&page=0&size=20
    User->>ProductController: GET /api/v1/products?brandId={}&sort={}&page={}&size={}
    note right of User: Header: X-USER-ID: {userId}

    alt 정렬 조건 ❌
        alt default 정렬 적용
            ProductController->>ProductFacade: getProductList(brandId, defaultPageable)
        else 에러 반환
            ProductController-->>User: 400 Bad Request
        end
    else 정렬 조건 ⭕️
        ProductController->>ProductFacade: getProductList(brandId, pageable)
        ProductFacade->>ProductService: getProducts(brandId, pageable)
        ProductService-->>ProductFacade: productList

        alt 로그인 (userId) ⭕️
            ProductFacade->>LikeService: hasUserLikedProducts(userId, productList)
            LikeService-->>ProductFacade: likedYnList
            ProductFacade-->>User: productList + likedYnList
        else 로그인 (userId) ❌
            ProductFacade-->>User: productList
        end
    end
```

### ✅ 1-3. GET /api/v1/products/{productId} 상품 정보 조회
```mermaid
sequenceDiagram
    actor User
    participant ProductController
    participant ProductFacade
    participant ProductService
    participant LikeService

    User->>ProductController: GET /api/v1/products/{productId}
    note right of User: Header: X-USER-ID: {userId}
    alt productId ❌
        ProductController-->>User: 404 Not Found
    end
    
    ProductController->>ProductFacade: getProductDetail(productId)
    ProductFacade->>ProductService: getProductDetail(productId)
    ProductService-->>ProductFacade: productDetail

    alt 로그인 (userId) ⭕️
        ProductFacade->>LikeService: hasUserLikedProduct(userId, productId)
        LikeService-->>ProductFacade: likedYn
        ProductFacade-->>User: productDetail + likedYn
    else 로그인 (userId) ❌
        ProductFacade-->>User: productDetail
    end
```

---

## 2. 유저 (Users) <-> 좋아요 (Likes)

### ✅ 2-1. 좋아요 등록, 취소
```mermaid
sequenceDiagram
    actor User
    participant LikeController
    participant LikeFacade
    participant LikeService
    participant ProductService
    participant LikeRepository

    %% 좋아요 생성
    User->>LikeController: POST /api/v1/likes/products/{productId}
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        LikeController-->>User: 401 Unauthorized
    end

    LikeController->>LikeFacade: like(userId, productId)
    LikeFacade->>LikeService: like(userId, productId)
    LikeService->>LikeRepository: update likedYn = 'Y' 
    alt updatedRow == 0
        LikeService->>LikeRepository: save(userId, productId, likedYn='Y')
    end
    LikeFacade->>ProductService: incrementLikes(productId)
    ProductService->>LikeFacade: totalLikes
    LikeFacade-->LikeController: { likedYn: Y, totalLikes }
    LikeController-->>User: 200 OK + { likedYn: Y, totalLikes }

    %% 좋아요 삭제
    User->>LikeController: DELETE /api/v1/likes/products/{productId}
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        LikeController-->>User: 401 Unauthorized
    end

    LikeController->>LikeFacade: unLike(userId, productId)
    LikeFacade->>LikeService: unLike(userId, productId)
    LikeService->>LikeRepository: update likedYn = 'N' 
    LikeFacade->>ProductService: decrementLikes(productId)
    ProductService->>LikeFacade: totalLikes
    LikeFacade-->LikeController: { likedYn: N, totalLikes }
    LikeController-->>User: 200 OK + { likedYn: N, totalLikes }
```

### ✅ 2-2. 내가 좋아요 한 상품 목록 조회
```mermaid
sequenceDiagram
    actor User
    participant LikeController
    participant LikeFacade
    participant LikeService
    participant LikeRepository
    participant ProductService
   
    User->>LikeController: GET /api/v1/likes/me
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        LikeController-->>User: 401 Unauthorized
    end
    
    LikeController->>LikeFacade: getLikedProducts(userId)
    LikeFacade->>LikeService: getLikedProducts(userId)
    
    LikeService->>LikeRepository: findByUserId(userId)
    LikeRepository-->>LikeFacade: likedProductIds
    
    LikeFacade->>ProductService: getProductsByIds(likedProductIds)
    ProductService-->>LikeFacade: likedProductsDetail
    
    LikeFacade-->>LikeController: likedProductsDetail
    LikeController-->>User: 200 OK + likedProductsDetail
```

---

## 3. 유저 (Users) <-> 주문 / 결제 (Orders)

### ✅ 3-1. 주문 요청
```mermaid
sequenceDiagram
    actor User
    participant OrderController
    participant OrderFacade
    participant OrderService
    participant CartService
    participant ProductService
    participant PointService
    participant Order
    participant Stock
    participant Point
    participant OMS(주문 정보 외부 시스템)

    User->>OrderController: POST /api/v1/orders
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        OrderController-->>User: 401 Unauthorized
    end
    
    OrderController->>OrderFacade: createOrder(userId, orderRequest)

    OrderFacade->>CartService: getCartItems(userId, cartItemIds)
    CartService-->>OrderFacade: cartItems

    OrderFacade->>ProductService: checkProductStock(cartItems)
    alt 상품 재고 ❌
        ProductService-->>OrderFacade: 409 Conflict
    else 상품 재고 ⭕️
        OrderFacade->>PointService: checkUserPoints(userId, totalPrice)
        alt 포인트 보유 ❌
            PointService-->>OrderFacade: 409 Conflict
        else 포인트 보유 ⭕️
            OrderFacade->>OrderService: createOrder(userId, cartItems, point, stocks)

            OrderService->>Order: orderProcess()

            activate Order
            Order->>Stock: stockDecrease()
            Stock-->>Order: ok
            Order->>Point: pointUse()
            Point-->>Order: ok
            Order-->>OrderService: orderResult
            deactivate Order

            OrderFacade->>OMS(주문 정보 외부 시스템): sendOrderInfo(order)
            alt OMS(주문 정보 외부 시스템) ❌
                OMS(주문 정보 외부 시스템)-->>OrderService: 500
            else OMS(주문 정보 외부 시스템) ⭕️
                OMS(주문 정보 외부 시스템)-->>OrderService: 200 OK
                
                OrderFacade->>CartService: clearCart(userId, cartItemIds)
                CartService-->>OrderFacade: cartCleared
            end
        end
    end
```

### ✅ 3-2. 유저의 주문 목록 조회
```mermaid
sequenceDiagram
    actor User
    participant OrderController
    participant OrderFacade
    participant OrderService
    participant ProductService

    User->>OrderController: GET /api/v1/orders
    OrderController->>OrderFacade: getOrderList(userId)
    
    OrderFacade->>OrderService: getOrdersByUser(userId)
    OrderService-->>OrderFacade: List<Order>

    OrderFacade->>ProductService: getProductSummaries(itemIds)
    ProductService-->>OrderFacade: productSummaries

    OrderFacade-->>OrderController: OrderListResponse
    OrderController-->>User: 200 OK + OrderListResponse
```

### ✅ 3-3. 단일 주문 상세 조회
```mermaid
sequenceDiagram
    actor User
    participant OrderController
    participant OrderFacade
    participant OrderService
    participant ProductService

    User->>OrderController: GET /api/v1/orders/{orderId}
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        OrderController-->>User: 401 Unauthorized
    end
    
    OrderController->>OrderFacade: getOrderDetail(orderId, userId)
    OrderFacade->>OrderService: getOrderDetail(orderId, userId)
    OrderService-->>OrderFacade: Order

    OrderFacade->>ProductService: getProductDetails(itemIds)
    ProductService-->>OrderFacade: productDetails

    OrderFacade-->>OrderController: OrderDetailResponse
    OrderController-->>User: 200 OK + OrderDetailResponse
```

---

## 4. 유저 (Users) <-> 장바구니 (Carts)

### ✅ 4-1. 장바구니 담기 요청
```mermaid
sequenceDiagram
    actor User
    participant CartController
    participant CartFacade
    participant CartService
    participant ProductService

    User->>CartController: POST /api/v1/carts
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        CartController-->>User: 401 Unauthorized
    end
    
    CartController->>CartFacade: addToCart(userId, cartItems)
    CartFacade->>ProductService: checkProductAvailability(cartItems)
    alt product 존재 ❌
        ProductService-->>CartFacade: 404 Not Found
    else productId 존재 ⭕️
        CartFacade->>CartService: addToCart(userId, cartItems)
        CartService-->>User: 201 Created + cartItemResponse
    end
```

### ✅ 4-2. 내 장바구니 조회
```mermaid
sequenceDiagram
    actor User
    participant CartController
    participant CartFacade
    participant CartService
    participant ProductService

    User->>CartController: GET /api/v1/carts/me
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        CartController-->>User: 401 Unauthorized
    end
    
    CartController->>CartFacade: getCartItems(userId)
    CartFacade->>CartService: getCartItems(userId)
    CartService-->>CartFacade: cartItems

    CartFacade->>ProductService: getProductDetails(cartItemIds)
    ProductService-->>User: productDetails
```







