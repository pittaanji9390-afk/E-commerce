# Enterprise Multi-Vendor Marketplace - System Architecture Specification

## 1. Executive Summary

This architecture document defines the structural, operational, and domain foundations of an enterprise multi-vendor e-commerce marketplace platform targeting 60,000+ meaningful lines of code. The system coordinates three distinct stakeholder experiences (Customer Storefront, Seller Dashboard, Admin Operations Console) on top of a resilient, modular backend built with Java 21, Spring Boot 3.4, PostgreSQL 16, Redis 7, RabbitMQ, and React 19 / TypeScript / Vite.

---

## 2. High-Level Architectural Principles

1. **Modular Monolith by Design**: Modules are isolated into distinct domain packages with strictly bounded contexts. Cross-module communication occurs via defined service interfaces and asynchronous domain events.
2. **Zero-Trust Pricing & Calculations**: The backend recalculates all item prices, taxes, shipping rates, and coupon discounts from database records at checkout time. Client-submitted prices are completely ignored.
3. **Strict Tenant & Cross-Seller Isolation**: Every seller query enforces `seller_id` scoping at the database level. Sellers can never view or modify products, inventory, orders, reviews, or payouts belonging to other sellers.
4. **Financial Integrity & Double-Entry Escrow**: Monetary values strictly use `BigDecimal` / `NUMERIC(15, 2)`. Seller payouts are calculated strictly through immutable double-entry ledger postings.
5. **Concurrency-Safe Operations**: Critical business workflows (inventory reservation, coupon redemption, payment webhook processing) use pessimistic database locks (`SELECT FOR UPDATE`), atomic conditional updates, and idempotency tracking.

---

## 3. Modular Monolith Decomposition

```text
com.marketplace
├── shared/             # Shared kernel: BaseEntity, Money, DomainEvent, Exceptions, Result
├── security/           # Spring Security, JWT rotation, RBAC permission matrix, MFA TOTP
├── identity/           # User registration, authentication, sessions, password lifecycle
├── customer/           # Customer profiles, saved addresses, communication preferences
├── seller/             # Seller onboarding, KYC verification, store profile, bank accounts
├── catalog/            # Categories, hierarchical trees, brands, dynamic attributes
├── product/            # Products, multi-dimensional variants, image gallery, SKU indexing
├── inventory/          # Real-time stock, reservations, transaction audit log, low-stock alerts
├── cart/               # Shopping carts, guest carts, cart item validation
├── wishlist/           # Customer wishlist items, move-to-cart orchestration
├── pricing/            # Promotion rules engine, dynamic tax calculation
├── coupon/             # Percentage & fixed coupons, usage quotas, seller/global rules
├── checkout/           # Checkout saga, multi-seller split orchestrator, idempotency guards
├── order/              # Parent Orders, Seller Sub-Orders, immutable OrderItem snapshots
├── payment/            # Gateway abstraction (Stripe/Razorpay), HMAC webhook verification
├── shipping/           # Carrier methods, zones, tracking events, fulfillment labels
├── returnorder/        # Customer return requests, item selection, inspection workflow
├── dispute/            # Dispute mediation, evidence upload, admin resolution
├── refund/             # Partial/full refunds, gateway refund synchronization
├── commission/         # Configurable tier commission engine (global, category, seller)
├── payout/             # Seller escrow ledger, automated payout batches, statements
├── review/             # Verified purchase reviews, seller feedback, moderation
├── notification/       # In-app notification inbox, async transactional emails
├── search/             # Faceted catalog search, trigram matching, relevance ranking
├── analytics/          # Marketplace KPIs (GMV, AOV, churn, conversion rates)
├── admin/              # Operational queues, governance, system configuration
└── audit/              # Immutable audit logging of administrative and financial actions
```

---

## 4. Multi-Seller Composite Order Lifecycle

When an order containing items from multiple sellers is processed:
1. **Checkout Validation**: The system validates availability and locks inventory records across all selected variant SKUs.
2. **Parent Order Creation**: A global order (`ORDER-XXXXX`) is recorded representing the total transaction.
3. **Sub-Order Partitioning**: Independent `seller_orders` (`SO-XXXXX-A`, `SO-XXXXX-B`) are created for each vendor with their respective items, commissions, and shipping requirements.
4. **Payment Settlement**: A single payment intent is authorized. Upon webhook confirmation, sub-orders transition to `PAID` / `PROCESSING`.
5. **Independent Fulfillment**: Each seller fulfills and ships their sub-order independently with distinct carriers and tracking numbers.
6. **Escrow & Payout**: Funds are held in escrow until delivery confirmation + return window expiration, after which net proceeds are posted to the seller's ledger.
