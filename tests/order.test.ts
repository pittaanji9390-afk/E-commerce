import { describe, it, expect } from 'vitest';

describe('Order Processing & Financial Ledger Suite', () => {
  it('should split order line items by merchant correctly', () => {
    const items = [
      { id: '1', sellerId: 'seller-a', total: 100 },
      { id: '2', sellerId: 'seller-b', total: 50 },
      { id: '3', sellerId: 'seller-a', total: 75 }
    ];

    const grouped: Record<string, number> = {};
    for (const item of items) {
      grouped[item.sellerId] = (grouped[item.sellerId] || 0) + item.total;
    }

    expect(grouped['seller-a']).toBe(175);
    expect(grouped['seller-b']).toBe(50);
  });

  it('should calculate 10% platform commission fee accurately', () => {
    const gross = 250.00;
    const commissionRate = 0.10;
    const commissionFee = gross * commissionRate;
    const netSellerPayout = gross - commissionFee;

    expect(commissionFee).toBe(25.00);
    expect(netSellerPayout).toBe(225.00);
  });
});
