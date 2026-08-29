# Enterprise Multi-Vendor Marketplace - REST API Architecture

## 1. REST Conventions & Standards

- **Base URL**: `/api/v1`
- **Protocol**: HTTPS / WSS
- **Content Negotiation**: `application/json; charset=UTF-8`
- **Authentication**: `Authorization: Bearer <JWT_ACCESS_TOKEN>`
- **Idempotency**: `Idempotency-Key: <UUID_V4>` required on state-mutating checkout, payment, refund, and payout operations.

---

## 2. Standardized Response & Error Envelopes

### 2.1 Success Response (`Result<T>`)
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2026-08-30T01:00:00Z"
}
```

### 2.2 Paginated Response (`PagedResult<T>`)
```json
{
  "success": true,
  "data": [ ... ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2026-08-30T01:00:00Z"
}
```

### 2.3 Standardized Error Response
```json
{
  "timestamp": "2026-08-30T01:00:00Z",
  "requestId": "e8d7a12b-7cf3-4882-9f33-8a7c29352e89",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Input validation failed for requested payload",
  "fieldErrors": [
    {
      "field": "quantity",
      "message": "Quantity must be greater than zero"
    }
  ]
}
```

---

## 3. Core API Endpoint Routes

```text
Authentication & Identity:
  POST   /api/v1/auth/register
  POST   /api/v1/auth/login
  POST   /api/v1/auth/refresh
  POST   /api/v1/auth/logout
  POST   /api/v1/auth/forgot-password
  POST   /api/v1/auth/reset-password
  POST   /api/v1/auth/mfa/setup
  POST   /api/v1/auth/mfa/verify

Customer Operations:
  GET    /api/v1/customers/profile
  PUT    /api/v1/customers/profile
  GET    /api/v1/customers/addresses
  POST   /api/v1/customers/addresses
  PUT    /api/v1/customers/addresses/{id}
  DELETE /api/v1/customers/addresses/{id}

Catalog & Search:
  GET    /api/v1/categories
  GET    /api/v1/categories/{slug}
  GET    /api/v1/brands
  GET    /api/v1/products
  GET    /api/v1/products/{slug}
  GET    /api/v1/search

Shopping & Cart:
  GET    /api/v1/cart
  POST   /api/v1/cart/items
  PUT    /api/v1/cart/items/{itemId}
  DELETE /api/v1/cart/items/{itemId}
  DELETE /api/v1/cart
  GET    /api/v1/wishlist
  POST   /api/v1/wishlist/items
  DELETE /api/v1/wishlist/items/{itemId}

Checkout & Orders:
  POST   /api/v1/checkout/calculate
  POST   /api/v1/checkout/create-order
  GET    /api/v1/orders
  GET    /api/v1/orders/{id}
  POST   /api/v1/orders/{id}/cancel

Payments:
  POST   /api/v1/payments/create-session
  POST   /api/v1/payments/webhook
  GET    /api/v1/payments/{id}/status

Seller Management:
  POST   /api/v1/seller/onboard
  GET    /api/v1/seller/profile
  PUT    /api/v1/seller/profile
  GET    /api/v1/seller/products
  POST   /api/v1/seller/products
  PUT    /api/v1/seller/products/{id}
  GET    /api/v1/seller/inventory
  PUT    /api/v1/seller/inventory/{variantId}
  GET    /api/v1/seller/orders
  GET    /api/v1/seller/orders/{id}
  POST   /api/v1/seller/orders/{id}/ship
  GET    /api/v1/seller/payouts
  GET    /api/v1/seller/analytics

Admin Governance:
  GET    /api/v1/admin/sellers/pending
  POST   /api/v1/admin/sellers/{id}/approve
  POST   /api/v1/admin/sellers/{id}/reject
  GET    /api/v1/admin/products/pending
  POST   /api/v1/admin/products/{id}/approve
  POST   /api/v1/admin/products/{id}/reject
  GET    /api/v1/admin/disputes
  POST   /api/v1/admin/disputes/{id}/resolve
  GET    /api/v1/admin/analytics/overview
  GET    /api/v1/admin/audit-logs
```
