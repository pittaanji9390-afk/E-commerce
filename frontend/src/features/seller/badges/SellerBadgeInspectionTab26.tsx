import React from 'react';
import { Award, CheckCircle2 } from 'lucide-react';

export const SellerBadgeInspectionTab26: React.FC = () => {
  return (
    <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-4">
      <div className="flex items-center justify-between border-b pb-3">
        <h4 className="font-bold text-sm text-gray-900 flex items-center gap-2">
          <Award className="w-4 h-4 text-primary-600" /> Merchant Certification Enclave #26
        </h4>
        <span className="text-xs text-green-600 bg-green-50 px-2 py-0.5 rounded font-medium flex items-center gap-1">
          <CheckCircle2 className="w-3 h-3" /> Certified Top-Seller
        </span>
      </div>
      <p className="text-xs text-gray-500">Meets 99.5% on-time dispatch and zero RMA dispute criteria.</p>
    </div>
  );
};
