# Enterprise Multi-Vendor Marketplace - Database Architecture & Schema Specification

## 1. Database Standards & Conventions

1. **RDBMS**: PostgreSQL 16
2. **Migrations**: Flyway Versioned Migrations (`V1__...sql`, `V2__...sql`)
3. **Naming Conventions**:
   - Tables: Lowercase snake_case plural (e.g. `products`, `seller_orders`, `inventory_transactions`)
   - Primary Keys: UUID v4 or BIGSERIAL (`id`)
   - Foreign Keys: `{singular_table}_id` (e.g. `seller_id`, `parent_order_id`)
   - Monetary Amounts: `NUMERIC(15, 2)` NOT NULL with `currency VARCHAR(3) DEFAULT 'USD'`
   - Audit Columns: `created_at TIMESTAMP WITH TIME ZONE`, `updated_at TIMESTAMP WITH TIME ZONE`, `created_by VARCHAR(100)`, `updated_by VARCHAR(100)`, `version BIGINT DEFAULT 0` (for optimistic locking)

---

## 2. Core Relational Entity Inventory

### 2.1 Identity & Access Control
- `users`: Core authentication identity, password hash, status, MFA secret.
- `user_roles`: Mapping of users to roles (`ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, `ROLE_CATALOG_ADMIN`, `ROLE_FINANCE_ADMIN`, `ROLE_SUPPORT_AGENT`, `ROLE_MODERATOR`, `ROLE_SELLER`, `ROLE_CUSTOMER`).
- `refresh_tokens`: Rotational JWT refresh tokens with expiration and revocation metadata.

### 2.2 Customer & Seller Stakeholders
- `customers`: Customer personal metadata, avatar, preferences.
- `customer_addresses`: Multi-address book (shipping, billing, default flags).
- `sellers`: Merchant business name, slug, contact details, status (`PENDING`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `SUSPENDED`, `DEACTIVATED`), commission rate override.
- `seller_verifications`: KYC business registration docs, tax IDs, verification audit logs.
- `seller_bank_accounts`: Disbursal routing numbers and bank details for automated payouts.

### 2.3 Catalog & Discovery
- `categories`: Self-referential hierarchical category tree (parent_id, path, level, slug).
- `brands`: Approved manufacturer brands.
- `category_attributes`: Dynamic attribute definitions per category (RAM, Size, Color, etc.).
- `products`: Base catalog items with seller ownership, slug, base price, status (`DRAFT`, `PENDING_REVIEW`, `ACTIVE`, `INACTIVE`, `REJECTED`, `ARCHIVED`).
- `product_variants`: SKU-level dimensions (size, color, barcode, price adjustment).
- `product_attribute_values`: Dynamic attribute values attached to products/variants.
- `product_images`: Ordered image gallery URLs, alt text, primary image flag.

### 2.4 Inventory & Warehousing
- `inventory`: Variant stock balances (`on_hand`, `reserved`, `available = on_hand - reserved`, `low_stock_threshold`).
- `inventory_transactions`: Immutable log of stock mutations (`PURCHASE`, `RETURN`, `RESTOCK`, `ADJUSTMENT`, `RESERVATION`, `RELEASE`, `DAMAGE`, `LOSS`).

### 2.5 Shopping & Promotions
- `carts`: Customer and anonymous shopping carts.
- `cart_items`: Variant reference, snapshot price, quantity.
- `wishlists`: Saved items per customer.
- `coupons`: Platform and seller discount codes, percentage/fixed discounts, expiration, usage limits, minimum spend.
- `coupon_redemptions`: Audit log of user redemptions with atomic concurrency counter.

### 2.6 Order Processing & Fulfillment
- `orders`: Global composite order header, customer reference, gross totals, payment status, idempotency key.
- `seller_orders`: Split sub-order per vendor, subtotal, commission amount, payout status, fulfillment status.
- `order_items`: Immutable price/tax/title snapshot per purchased item.
- `shipments`: Tracking number, carrier, shipping label URL, dispatch status.
- `shipment_events`: Granular carrier tracking checkpoints.

### 2.7 Payments & Financial Ledger
- `payments`: Global transaction records, provider reference (Stripe/Razorpay), payment method, status.
- `payment_webhooks`: Audit log of inbound provider webhooks for replay prevention and signature validation.
- `refunds`: Full and partial refund records linked to payment and return requests.
- `seller_ledger`: Immutable double-entry financial ledger entries (`ORDER_REVENUE`, `COMMISSION_DEDUCTION`, `REFUND_DEDUCTION`, `SHIPPING_FEE`, `PAYOUT_DISBURSEMENT`).
- `seller_payouts`: Batch payout execution records, bank transfer reference, approval audit.

### 2.8 Customer Service & Governance
- `returns`: Return authorization requests, return reasons, inspection outcome.
- `disputes`: Customer-seller dispute arbitration records, admin verdict.
- `reviews`: Product reviews by verified purchasers, star ratings, moderation state.
- `seller_reviews`: Direct seller ratings across shipping, communication, accuracy.
- `notifications`: In-app notification messages.
- `audit_logs`: Comprehensive administrative and security audit trail.
