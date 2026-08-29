import React from 'react';
import { formatCurrency } from '@/lib/utils';

export interface PriceDisplayProps {
  amount: number;
  compareAtAmount?: number;
  currency?: string;
  className?: string;
}

export const PriceDisplay: React.FC<PriceDisplayProps> = ({
  amount,
  compareAtAmount,
  currency = 'USD',
  className,
}) => {
  const hasDiscount = compareAtAmount && compareAtAmount > amount;
  const discountPercent = hasDiscount
    ? Math.round(((compareAtAmount - amount) / compareAtAmount) * 100)
    : 0;

  return (
    <div className={`flex items-baseline gap-2 ${className || ''}`}>
      <span className="text-lg font-bold text-slate-900">
        {formatCurrency(amount, currency)}
      </span>
      {hasDiscount && (
        <>
          <span className="text-sm text-slate-400 line-through">
            {formatCurrency(compareAtAmount, currency)}
          </span>
          <span className="text-xs font-semibold text-emerald-600">
            {discountPercent}% OFF
          </span>
        </>
      )}
    </div>
  );
};
