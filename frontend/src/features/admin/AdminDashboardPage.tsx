import React from 'react';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import {
  TrendingUp,
  Store,
  Scale,
  DollarSign,
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const adminKPIs = [
    { title: 'Marketplace GMV (MTD)', value: '$482,900.00', change: '+24.5%', icon: DollarSign, color: 'bg-emerald-50 text-emerald-600' },
    { title: 'Net Platform Commission (10%)', value: '$48,290.00', change: 'Escrow settled', icon: TrendingUp, color: 'bg-indigo-50 text-indigo-600' },
    { title: 'Pending Seller KYC Reviews', value: '7 Applications', change: 'Requires review', icon: Store, color: 'bg-amber-50 text-amber-600' },
    { title: 'Active Customer Disputes', value: '2 Cases', change: 'Arbitration pending', icon: Scale, color: 'bg-rose-50 text-rose-600' },
  ];

  const pendingSellers = [
    { id: 's-k-1', businessName: 'Apex Audio Dynamics LLC', taxId: 'XX-XXXX4912', country: 'US', docType: 'IRS EIN Form + Certificate', submittedAt: '2 hours ago' },
    { id: 's-k-2', businessName: 'Nordic Wool Studio', taxId: 'XX-XXXX8810', country: 'SE', docType: 'EU VAT Registration', submittedAt: '5 hours ago' },
    { id: 's-k-3', businessName: 'Tokyo Artisan Crafts', taxId: 'XX-XXXX3124', country: 'JP', docType: 'National Business Registry', submittedAt: '1 day ago' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Marketplace Executive Control</h1>
          <p className="text-xs text-slate-500 mt-0.5">Platform-wide financial ledger, merchant compliance, and system status</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" size="sm">
            Audit Trail
          </Button>
          <Button size="sm" className="bg-rose-600 hover:bg-rose-700">
            Platform Settings
          </Button>
        </div>
      </div>

      {/* KPI Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {adminKPIs.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <Card key={idx} className="p-5 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">{kpi.title}</span>
                <div className="text-2xl font-black text-slate-900 mt-1">{kpi.value}</div>
                <div className="text-[11px] font-bold text-slate-600 mt-1">{kpi.change}</div>
              </div>
              <div className={`p-3 rounded-2xl ${kpi.color}`}>
                <Icon className="w-6 h-6" />
              </div>
            </Card>
          );
        })}
      </div>

      {/* Pending Seller KYC Queue */}
      <Card className="p-6 space-y-4">
        <div className="flex items-center justify-between pb-4 border-b border-slate-100">
          <div>
            <h3 className="text-base font-bold text-slate-900">Pending Seller Verification Queue</h3>
            <p className="text-xs text-slate-500">Merchants cannot list products until KYC documents are approved</p>
          </div>
          <Badge variant="warning">7 Pending Approval</Badge>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                <th className="pb-3">Legal Business Name</th>
                <th className="pb-3">Tax ID / EIN</th>
                <th className="pb-3">Country</th>
                <th className="pb-3">Verification Documents</th>
                <th className="pb-3">Submitted</th>
                <th className="pb-3 text-right">Arbitration</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {pendingSellers.map((s) => (
                <tr key={s.id} className="hover:bg-slate-50/50">
                  <td className="py-3 font-bold text-slate-900">{s.businessName}</td>
                  <td className="py-3 font-mono">{s.taxId}</td>
                  <td className="py-3">{s.country}</td>
                  <td className="py-3 font-medium text-brand-600">{s.docType}</td>
                  <td className="py-3 text-slate-500">{s.submittedAt}</td>
                  <td className="py-3 text-right space-x-2">
                    <Button size="sm" variant="outline" className="text-xs py-1 border-rose-200 text-rose-600 hover:bg-rose-50">
                      Reject
                    </Button>
                    <Button size="sm" className="text-xs py-1 bg-emerald-600 hover:bg-emerald-700">
                      Approve KYC
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};
