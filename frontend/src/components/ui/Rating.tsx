import React from 'react';
import { Star } from 'lucide-react';

export interface RatingProps {
  value: number;
  count?: number;
  showCount?: boolean;
}

export const Rating: React.FC<RatingProps> = ({ value, count, showCount = true }) => {
  return (
    <div className="flex items-center gap-1.5 text-amber-500">
      <div className="flex items-center">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`w-4 h-4 ${
              star <= Math.round(value)
                ? 'fill-amber-400 text-amber-400'
                : 'fill-slate-200 text-slate-200'
            }`}
          />
        ))}
      </div>
      <span className="text-xs font-semibold text-slate-700">{value.toFixed(1)}</span>
      {showCount && count !== undefined && (
        <span className="text-xs text-slate-400">({count})</span>
      )}
    </div>
  );
};
