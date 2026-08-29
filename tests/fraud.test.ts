import { describe, it, expect } from 'vitest';

describe('Anti-Fraud Risk Detection Suite', () => {
  it('should flag high value transactions for manual review', () => {
    function assessRisk(orderTotal: number, isNewCustomer: boolean): { score: number; level: string } {
      let score = 10;
      if (orderTotal > 2000) score += 40;
      if (isNewCustomer) score += 20;

      let level = 'LOW';
      if (score >= 70) level = 'CRITICAL';
      else if (score >= 50) level = 'HIGH';
      else if (score >= 30) level = 'MEDIUM';

      return { score, level };
    }

    const safeOrder = assessRisk(150, false);
    expect(safeOrder.level).toBe('LOW');

    const highRiskOrder = assessRisk(3500, true);
    expect(highRiskOrder.score).toBe(70);
    expect(highRiskOrder.level).toBe('CRITICAL');
  });
});
