import { describe, it, expect } from 'vitest';

describe('B2B Wholesale & RFQ Engine Suite', () => {
  it('should calculate bulk volume tiered pricing correctly', () => {
    const tiers = [
      { min: 1, max: 9, price: 50.00 },
      { min: 10, max: 49, price: 42.50 },
      { min: 50, max: 9999, price: 35.00 }
    ];

    function calculateUnitPrice(qty: number): number {
      const match = tiers.find(t => qty >= t.min && qty <= t.max);
      return match ? match.price : 50.00;
    }

    expect(calculateUnitPrice(5)).toBe(50.00);
    expect(calculateUnitPrice(25)).toBe(42.50);
    expect(calculateUnitPrice(100)).toBe(35.00);
  });

  it('should validate Net-30 payment due date calculation', () => {
    const now = new Date('2026-08-30');
    const net30Date = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000);
    expect(net30Date.toISOString().split('T')[0]).toBe('2026-09-29');
  });
});
