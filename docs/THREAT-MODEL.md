# Enterprise Multi-Vendor Marketplace - Comprehensive Threat Model

## 1. STRIDE Analysis & Attack Matrix

| Category | Threat Vector | Impact | Mitigation Strategy | Verification / Test |
| :--- | :--- | :--- | :--- | :--- |
| **Spoofing** | Fake Payment Webhook | Critical | HMAC-SHA256 signature verification over raw request body; replay time window check (<300s) | `PaymentWebhookSecurityTest` |
| **Tampering** | Frontend Price / Total Alteration | Critical | Total prices, taxes, and discounts are strictly calculated on backend from catalog DB | `CheckoutPriceIntegrityTest` |
| **Repudiation** | Dispute / Unauthorized Action | High | Tamper-evident `audit_logs` storing actor ID, IP, timestamp, action, and JSON diff | `AuditLogIntegrityTest` |
| **Information Disclosure** | Cross-Seller Order / Financial Leak | Critical | Tenant-scoped repository queries using JPA Specifications and entity ownership checks | `CrossSellerAccessTest` |
| **Denial of Service** | Flash Sale Inventory Exhaustion / Race | Critical | Atomic DB updates (`UPDATE ... WHERE available >= :qty`) and Redis token bucket rate limiting | `ConcurrentInventoryTest` (50 threads) |
| **Elevation of Privilege** | Normal User invoking Admin APIs | Critical | Stateless Spring Security JWT filter + `@PreAuthorize("hasRole('ADMIN')")` method security | `RbacSecurityTest` |
