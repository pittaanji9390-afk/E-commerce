import React from 'react';
import { TrendingUp } from 'lucide-react';

export const SellerPerformanceMetricTab20: React.FC = () => {
  return (
    <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-6">
      <div className="flex items-center justify-between border-b pb-4">
        <div>
          <h3 className="text-base font-bold text-gray-900 flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-primary-600" /> Operational Efficiency Matrix #20
          </h3>
          <p className="text-xs text-gray-500 mt-1">Fulfillment speed, defect rates, and dispute resolution metrics.</p>
        </div>
        <span className="text-xs font-semibold text-primary-600 bg-primary-50 px-2.5 py-1 rounded-md">Metric Suite #20</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">On-Time Dispatch</span>
          <p className="text-xl font-bold text-green-600 mt-1">99.5%</p>
        </div>
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Return Rate</span>
          <p className="text-xl font-bold text-gray-900 mt-1">1.2%</p>
        </div>
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Customer CSAT</span>
          <p className="text-xl font-bold text-primary-600 mt-1">4.90 / 5.0</p>
        </div>
      </div>
    </div>
  );
};
