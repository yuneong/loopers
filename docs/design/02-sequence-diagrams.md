# [시퀀스 다이어그램]

## 1. 유저 (Users) <-> 브랜드 & 상품 (Brands & Products)

---

## 2. 유저 (Users) <-> 좋아요 (Likes)

### ✅ 2-1. 좋아요 등록, 취소
```mermaid
sequenceDiagram
    actor User
    participant LikeController
    participant LikeService
    participant ProductService
    participant LikeRepository

    User->>LikeController: POST /api/v1/products/{productId}/likes
    note right of User: Header: X-USER-ID: {userId}
    alt X-USER-ID 헤더 ❌
        LikeController-->>User: 401 Unauthorized
    end

    LikeController->>LikeService: toggleLike(userId, productId)
    
    LikeService->>LikeRepository: existsLike(userId, productId)
    alt 좋아요 ❌ → 좋아요 등록
        LikeService->>LikeRepository: save(userId, productId)
        LikeService->>ProductService: incrementLikes(productId)
        ProductService-->>LikeService: likeCount: N+1
        LikeService-->>LikeController: { likedYn: Y, likeCount: N+1 }
    else 좋아요 ⭕️ → 좋아요 삭제
        LikeService->>LikeRepository: delete(userId, productId)
        LikeService->>ProductService: decrementLikes(productId)
        ProductService-->>LikeService: likeCount: N-1
        LikeService-->>LikeController: { likedYn: N, likeCount: N-1 }
    end

    LikeController-->>User: 200 OK + { likedYn, likeCount }
```

### ✅ 2-2. 내가 좋아요 한 상품 목록 조회
```mermaid
sequenceDiagram
    actor User
    participant LikeController
    participant LikeService
    participant LikeRepository
    participant ProductService
   
    User->>LikeController: GET /api/v1/users/{userId}/likes
    
    LikeController->>LikeService: getLikedProducts(userId)
    
    LikeService->>LikeRepository: findByUserId(userId)
    LikeRepository-->>LikeService: likedProductIds
    
    LikeService->>ProductService: getProductsByIds(likedProductIds)
    ProductService-->>LikeService: likedProductsDetail
    
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









