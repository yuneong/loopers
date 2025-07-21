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


---

## 4. 상품 (Products) <-> 장바구니 (Carts)









