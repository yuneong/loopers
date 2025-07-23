# [시퀀스 다이어그램]

## 1. 유저 (Users) <-> 브랜드 & 상품 (Brands & Products)

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
    User->>LikeController: POST /api/v1/products/{productId}/likes
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
    User->>LikeController: DELETE /api/v1/products/{productId}/likes
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
   
    User->>LikeController: GET /api/v1/users/me/likes
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
    participant OrderRepository
    participant Order
    participant Stock
    participant Point
    participant PaymentGateway

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
        ProductService-->>CartService: 409 Conflict
    else 상품 재고 ⭕️
        OrderFacade->>PointService: checkUserPoints(userId, totalPrice)
        alt 포인트 보유 ❌
            PointService-->>OrderFacade: 409 Conflict
        else 포인트 보유 ⭕️
            OrderFacade->>OrderService: createOrder(userId, cartItems, point, stocks)

            OrderService->>Order: process()

            activate Order
            Order->>Stock: decrease() 반복
            Stock-->>Order: ok
            Order->>Point: use()
            Point-->>Order: ok
            deactivate Order

            OrderService->>OrderRepository: save(order)

            OrderService->>PaymentGateway: requestPayment(paymentRequest)
            alt 결제 ❌
                PaymentGateway-->>OrderService: 402 Payment Required
            else 결제 ⭕️
                OrderService->>CartService: clearCart(userId, cartItemIds)
                OrderService-->>OrderController: 201 Created
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

    User->>OrderController: GET /api/v1/users/{userId}/orders
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

## 4. 상품 (Products) <-> 장바구니 (Carts)









