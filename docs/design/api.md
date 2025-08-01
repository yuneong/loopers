# API Documentation

## 유저 (Users)
```
POST /api/v1/users       회원가입
GET /api/v1/users/me     내 정보 조회
```

## 포인트 (Points)
```
POST /api/ve/points/charge     포인트 충전
GET /api/v1/points             보유 포인트 조회
```

## 브랜드 & 상품 (Brands & Products)
```
GET /api/v1/brands/{brandId}         브랜드 정보 조회
GET /api/v1/products                 상품 목록 조회 (brandId, sort, page, size 파라미터)
GET /api/v1/products/{productId}     상품 정보 조회
```

## 좋아요 (Likes)
```
POST /api/v1/likes/products/{productId}        상품 좋아요 등록
DELETE /api/v1/likes/products/{productId}      상품 좋아요 취소
GET /api/v1/likes/me                           내가 좋아요 한 상품 목록 조회
```

## 주문 / 결제 (Orders)
```
POST /api/v1/orders               주문 요청
GET /api/v1/orders                유저의 주문 목록 조회
GET /api/v1/orders/{orderId}      단일 주문 상세 조회
```

## 장바구니 (Carts)
```
POST /api/v1/carts       장바구니 담기 요청
GET /api/v1/carts/me     내 장바구니 조회
```