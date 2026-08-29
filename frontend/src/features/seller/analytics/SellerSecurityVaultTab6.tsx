import React from 'react';
import { Lock } from 'lucide-react';

export const SellerSecurityVaultTab6: React.FC = () => {
  return (
    <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-4">
      <div className="flex items-center justify-between border-b pb-3">
        <h4 className="font-bold text-sm text-gray-900 flex items-center gap-2">
          <Lock className="w-4 h-4 text-primary-600" /> Key Vault Enclave #6
        </h4>
        <span className="text-xs text-green-600 bg-green-50 px-2 py-0.5 rounded font-mono">FIPS 140-2 Level 3</span>
      </div>
      <div className="flex justify-between items-center text-sm">
        <span className="text-gray-600">Encrypted Token Count</span>
        <span className="font-bold text-gray-900">140000 tokens</span>
      </div>
    </div>
  );
};
