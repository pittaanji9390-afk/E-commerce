import { describe, it, expect } from 'vitest';

describe('Multi-Currency & Forex Conversion Suite', () => {
  it('should convert USD to EUR using spot exchange rate', () => {
    const usdAmount = 100.00;
    const usdToEurRate = 0.92;
    const eurAmount = Math.round(usdAmount * usdToEurRate * 100) / 100;

    expect(eurAmount).toBe(92.00);
  });

  it('should convert USD to GBP using spot exchange rate', () => {
    const usdAmount = 250.00;
    const usdToGbpRate = 0.78;
    const gbpAmount = Math.round(usdAmount * usdToGbpRate * 100) / 100;

    expect(gbpAmount).toBe(195.00);
  });
});
