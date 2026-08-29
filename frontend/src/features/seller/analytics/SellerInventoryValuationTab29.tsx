import React from 'react';
import { Layers } from 'lucide-react';

export const SellerInventoryValuationTab29: React.FC = () => {
  return (
    <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-4">
      <div className="flex items-center justify-between border-b pb-3">
        <h4 className="font-bold text-sm text-gray-900 flex items-center gap-2">
          <Layers className="w-4 h-4 text-primary-600" /> FIFO Warehouse Valuation Ledger #29
        </h4>
        <span className="text-xs text-gray-400 font-mono">Valuation Model #29</span>
      </div>
      <div className="flex justify-between items-center text-sm">
        <span className="text-gray-600">Total Asset Holding Cost</span>
        <span className="font-bold text-gray-900">$</span>
      </div>
    </div>
  );
};
