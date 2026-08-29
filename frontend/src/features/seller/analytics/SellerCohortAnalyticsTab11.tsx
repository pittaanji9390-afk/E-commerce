import React from 'react';
import { BarChart3 } from 'lucide-react';

export const SellerCohortAnalyticsTab11: React.FC = () => {
  return (
    <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-6">
      <div className="flex items-center justify-between border-b pb-4">
        <div>
          <h3 className="text-base font-bold text-gray-900 flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-primary-600" /> 30-Day Customer Retention Cohort Model #11
          </h3>
          <p className="text-xs text-gray-500 mt-1">Multi-touch customer lifetime value and repeat purchase frequency curves.</p>
        </div>
        <span className="text-xs font-semibold text-green-600 bg-green-50 px-2.5 py-1 rounded-md">Cohort #11 Active</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Cohort Size</span>
          <p className="text-xl font-bold text-gray-900 mt-1">1800 buyers</p>
        </div>
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Repeat Rate</span>
          <p className="text-xl font-bold text-green-600 mt-1">35.2%</p>
        </div>
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Avg Order Value</span>
          <p className="text-xl font-bold text-gray-900 mt-1">$</p>
        </div>
        <div className="p-4 bg-gray-50 rounded-xl">
          <span className="text-xs text-gray-400 font-bold uppercase">Est. LTV (12M)</span>
          <p className="text-xl font-bold text-primary-600 mt-1">$</p>
        </div>
      </div>
    </div>
  );
};
