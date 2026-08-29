# Enterprise Multi-Vendor E-Commerce Marketplace Platform

A production-grade, distributed multi-vendor marketplace platform built with **Spring Boot 3.4 (Java 21)**, **React 18 / TypeScript / Tailwind CSS**, **PostgreSQL 16**, and **Redis 7**. 

Features an architecture designed for high throughput, double-entry financial ledger accounting, multi-warehouse fulfillment, B2B RFQ quotation negotiations, recurring subscriptions, and AI-powered product recommendations.

---

## Table of Contents

- [Architectural Highlights](#architectural-highlights)
- [Tech Stack & Dependencies](#tech-stack--dependencies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Build Instructions](#build-instructions)
- [Run & Execution](#run--execution)
- [Testing & Test Coverage](#testing--test-coverage)
- [Enterprise Feature Domains](#enterprise-feature-domains)
- [API Documentation & Endpoints](#api-documentation--endpoints)
- [Database Schema & Migrations](#database-schema--migrations)
- [Usage Guide](#usage-guide)
- [License](#license)

---

## Architectural Highlights

- **B2B Wholesale & RFQ Engine**: Formal request-for-quote workflows, volume-based bulk discount pricing tiers, and commercial Net-30 credit accounts.
- **WMS Multi-Warehouse Fulfillment**: Multi-facility regional fulfillment centers, bin tracking (`Aisle-Shelf-Slot`), barcode pick-pack-ship lists, and stock transfer routing.
- **Subscriptions & Recurring Billing**: Auto-delivery interval plans (Weekly, Monthly, Quarterly), dunning retry sequences, and automated billing schedulers.
- **CPC Advertising & Sponsored Products**: Keyword auction bidding engine, daily budget capping, click fraud detection, and impression CTR analytics.
- **Customer Loyalty & Rewards**: Tiered progression (Bronze to Diamond), digital gift cards with SHA-256 PIN hashing, and viral referral links.
- **Anti-Fraud Risk Detection**: Real-time velocity scoring, IP reputation checking, device fingerprinting, and automated order quarantine.
- **Personalized Recommendations**: Apriori collaborative filtering for "Frequently Bought Together" bundles and category trending algorithms.
- **Double-Entry Financial Ledger**: Immutable debit/credit accounting with escrow holds and automated merchant payouts.

---

## Tech Stack & Dependencies

### Backend
- **Language & Runtime**: Java 21 (LTS)
- **Framework**: Spring Boot 3.4.2 (Spring Web, Spring Security, Spring Data JPA, Spring Validation)
- **Database**: PostgreSQL 16 with UUID-v7 primary keys & full-text trigram indexes
- **Caching & Locks**: Redis 7 (Distributed locking with Redisson)
- **API Documentation**: SpringDoc OpenAPI 3.0 / Swagger UI
- **Build Tool**: Maven 3.9+

### Frontend
- **Framework**: React 18 + Vite SPA
- **Language**: TypeScript 5.7+
- **Styling**: Tailwind CSS 3.4 + Lucide React Icons
- **State Management**: TanStack React Query v5 & React Hook Form with Zod validation
- **Routing**: React Router DOM v7

---

## Prerequisites

Ensure you have the following installed on your host system:
- **Node.js**: `v20.x` or `v22.x` / `v24.x`
- **npm**: `v10.x` or later
- **Java Development Kit (JDK)**: OpenJDK / Temurin `21`
- **Apache Maven**: `3.9+`
- **Docker & Docker Compose**: `v24+` (optional for containerized deployment)

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/pittaanji9390-afk/E-commerce.git
cd E-commerce
```

### 2. Install Dependencies
Install frontend and root project dependencies:
```bash
npm install
npm run install:all
```

Or using the provided Makefile:
```bash
make install
```

---

## Build Instructions

### 1. Full Project Build (Frontend + Backend)
```bash
npm run build
```

### 2. Frontend Production Bundle
```bash
cd frontend
npm install
npm run build
```

### 3. Backend JAR Packaging
```bash
cd backend
mvn clean package -DskipTests
```

### 4. Docker Container Build
```bash
docker build -t marketplace-platform:latest .
```

---

## Run & Execution

### Option A: Complete Docker Compose Stack (Recommended)
Start PostgreSQL, Redis, Spring Boot backend, and React frontend in isolated containers:
```bash
docker-compose up -d
```
To monitor live container logs:
```bash
docker-compose logs -f
```
To stop all services:
```bash
docker-compose down
```

### Option B: Local Development Mode
Start both backend and frontend concurrently:
```bash
npm run dev
```

Or start components independently in separate terminals:
- **Backend**:
  ```bash
  cd backend
  mvn spring-boot:run
  ```
  *(Service running on `http://localhost:8080`)*

- **Frontend**:
  ```bash
  cd frontend
  npm run dev
  ```
  *(Vite dev server running on `http://localhost:5173`)*

---

## Testing & Test Coverage

### 1. Run Test Suite
Execute unit and integration tests across the marketplace ecosystem:
```bash
npm test
```

### 2. Run Test Coverage Analysis
```bash
npm run test:coverage
```

### 3. Backend Unit & Integration Tests
```bash
npm run test:backend
# Or directly via Maven:
cd backend && mvn test
```

---

## Enterprise Feature Domains

```
com.marketplace
├── advertising      # CPC Sponsored Products, Auction Bidding & Click Analytics
├── analytics        # OLAP Sales Cubes, Cohort Retention & Telemetry
├── b2b              # Wholesale RFQ Engine, Volume Tier Pricing & Net-30 Terms
├── cms              # Storefront Hero Banners & Marketing CMS
├── customer         # Profile Management & Multi-Address Book
├── forex            # Multi-Currency Forex Rates & Hedging Ledger
├── fraud            # Anti-Fraud Risk Scoring & Velocity Enforcers
├── identity         # JWT Authentication, Argon2id Hashing & RBAC
├── inventory        # FIFO Inventory Valuation & Stock Alerts
├── loyalty          # Reward Points, Tier Escalation & Gift Cards
├── messaging        # Real-Time Inquiry Tickets & Live Chat
├── notification     # Multi-Channel Event Dispatch (SMS/Push/Email)
├── order            # Split Orders, Merchant Sub-Orders & State Machine
├── payment          # Stripe Gateway, Escrow Balance & Idempotency
├── pricing          # Dynamic Discount Matrices & Jurisdiction Tax Engine
├── product          # Category Tree, Trigram Search & SKU Matrix
├── recommendation   # Collaborative Filtering & Frequently Bought Together
├── review           # Verified Buyer Reviews & Rolling Star Ratings
├── seller           # Merchant Onboarding, KYC AML & ACH Payouts
├── shipping         # Multi-Carrier Rate Calculators & Tracking Events
└── wms              # Multi-Warehouse Routing & Pick-Pack-Ship Lists
```

---

## API Documentation & Endpoints

Once the backend is running, open the interactive Swagger UI:
- **Swagger Documentation**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3.0 Spec**: `http://localhost:8080/v3/api-docs`

### Core API Groups

| Endpoint Group | Base Path | Description |
| :--- | :--- | :--- |
| **Authentication** | `/api/v1/auth` | User registration, login, token refresh, and MFA |
| **Products & Search** | `/api/v1/products` | Catalog browsing, faceted search, and SKU filters |
| **B2B Wholesale** | `/api/v1/b2b/rfq` | Submit RFQ, negotiate price proposals, and accept quotes |
| **WMS Warehouse** | `/api/v1/wms` | Fulfillment facilities, bin allocation, and pick lists |
| **Subscriptions** | `/api/v1/subscriptions` | Subscribe & Save auto-delivery, pause/resume |
| **Loyalty & Rewards**| `/api/v1/loyalty` | Point balance check, gift card redemption, referral links |
| **Sponsored Ads** | `/api/v1/advertising` | CPC campaign creation, keyword bidding, spend metrics |
| **Recommendations** | `/api/v1/recommendations` | Similar products, trending items, and frequent itemsets |
| **Orders & Checkout**| `/api/v1/orders` | Cart conversion, escrow checkout, and tracking |
| **RMA Returns** | `/api/v1/returns` | Return authorization requests, inspections, and disputes |

---

## Database Schema & Migrations

Database tables are initialized and version-controlled via JPA / Flyway:
- `users`, `roles`, `refresh_tokens`
- `categories`, `products`, `product_variants`, `product_images`
- `orders`, `order_items`, `seller_orders`, `payments`, `seller_payouts`
- `rfq_requests`, `rfq_items`, `bulk_price_tiers`, `b2b_credit_accounts`
- `warehouses`, `warehouse_bins`, `pick_lists`, `pick_list_items`
- `subscription_plans`, `customer_subscriptions`, `subscription_invoices`
- `loyalty_accounts`, `gift_cards`, `ad_campaigns`, `risk_evaluations`

---

## Usage Guide

### 1. Default Demo Credentials
When booted with seed profile, sample accounts are pre-configured:
- **Admin**: `admin@marketplace.com` / `AdminPass123!`
- **Verified Seller**: `seller@techhub.com` / `SellerPass123!`
- **Customer Buyer**: `buyer@example.com` / `BuyerPass123!`

### 2. Common Workflows
- **Customer Shopping**: Browse catalog -> Add to cart / Subscribe & Save -> Checkout with Stripe test card.
- **B2B Procurement**: Open product -> Request Wholesale RFQ -> Receive seller customized counter-offer -> Accept and generate PO.
- **Seller Fulfillment**: View incoming sub-orders -> Generate warehouse pick list -> Verify barcode scan -> Dispatch with carrier tracking.

---

## License

Proprietary & Confidential. All rights reserved. Commercial use requires explicit authorization.

