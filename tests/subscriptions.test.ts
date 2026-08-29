import { describe, it, expect } from 'vitest';

describe('Subscriptions & Recurring Billing Suite', () => {
  it('should calculate recurring discount percentages', () => {
    const basePrice = 100.00;
    const discountPercentage = 15; // 15% discount for subscribe & save
    const recurringPrice = basePrice * (1 - discountPercentage / 100);

    expect(recurringPrice).toBe(85.00);
  });

  it('should compute next billing date intervals', () => {
    const start = new Date('2026-08-30T00:00:00Z');
    const monthlyNext = new Date(start.getTime() + 30 * 24 * 60 * 60 * 1000);
    expect(monthlyNext.toISOString()).toBe('2026-09-29T00:00:00.000Z');
  });
});
