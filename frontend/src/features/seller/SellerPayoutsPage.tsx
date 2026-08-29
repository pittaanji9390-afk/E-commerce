import React, { useState } from 'react';
import { DollarSign, Landmark, Clock, CheckCircle2, History } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';

interface PayoutRecord {
  id: string;
  batchRef: string;
  amount: number;
  bankName: string;
  accountLast4: string;
  status: 'COMPLETED' | 'PROCESSING';
  date: string;
}

export const SellerPayoutsPage: React.FC = () => {
  const [availableBalance, setAvailableBalance] = useState(14850.75);
  const [pendingEscrow] = useState(3240.00);
  const [payoutModalOpen, setPayoutModalOpen] = useState(false);
  const [withdrawAmount, setWithdrawAmount] = useState<number>(5000);
  const [withdrawSuccess, setWithdrawSuccess] = useState(false);

  const [payouts, setPayouts] = useState<PayoutRecord[]>([
    {
      id: 'po-1',
      batchRef: 'PAYOUT-1724839210-984',
      amount: 4200.00,
      bankName: 'JPMorgan Chase',
      accountLast4: '9012',
      status: 'COMPLETED',
      date: 'Aug 24, 2026',
    },
    {
      id: 'po-2',
      batchRef: 'PAYOUT-1724392019-123',
      amount: 6850.00,
      bankName: 'JPMorgan Chase',
      accountLast4: '9012',
      status: 'COMPLETED',
      date: 'Aug 10, 2026',
    },
  ]);

  const handleWithdrawSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (withdrawAmount > availableBalance) return;

    setAvailableBalance((prev) => prev - withdrawAmount);
    setPayouts((prev) => [
      {
        id: `po-${Date.now()}`,
        batchRef: `PAYOUT-${Date.now()}-ACH`,
        amount: withdrawAmount,
        bankName: 'JPMorgan Chase',
        accountLast4: '9012',
        status: 'COMPLETED',
        date: 'Just now',
      },
      ...prev,
    ]);

    setWithdrawSuccess(true);
    setTimeout(() => {
      setWithdrawSuccess(false);
      setPayoutModalOpen(false);
    }, 1600);
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Financial Ledger & Payouts</h1>
          <p className="text-gray-500 text-sm mt-1">Double-entry escrow earnings, payout disbursements, and banking accounts.</p>
        </div>
        <Button onClick={() => setPayoutModalOpen(true)}>
          <Landmark className="w-4 h-4 mr-2" /> Request ACH Payout
        </Button>
      </div>

      {/* Financial Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Available for Withdrawal</span>
            <DollarSign className="w-5 h-5 text-green-600" />
          </div>
          <div className="text-3xl font-black text-gray-900">
            <PriceDisplay amount={availableBalance} />
          </div>
          <span className="text-xs text-gray-400 mt-2 block">Settled funds ready for direct deposit</span>
        </div>

        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Held in Escrow</span>
            <Clock className="w-5 h-5 text-amber-600" />
          </div>
          <div className="text-3xl font-black text-gray-900">
            <PriceDisplay amount={pendingEscrow} />
          </div>
          <span className="text-xs text-gray-400 mt-2 block">Releases upon buyer delivery verification</span>
        </div>

        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Linked Bank Account</span>
            <Landmark className="w-5 h-5 text-primary-600" />
          </div>
          <div className="text-lg font-bold text-gray-900">
            JPMorgan Chase •••• 9012
          </div>
          <Badge variant="success" className="mt-2">Primary ACH Verified</Badge>
        </div>
      </div>

      {/* Payout History Table */}
      <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <h3 className="text-base font-bold text-gray-900 mb-4 flex items-center gap-2">
          <History className="w-5 h-5 text-gray-600" /> Payout Disbursement History
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 font-semibold">Batch Reference</th>
                <th className="px-4 py-3 font-semibold">Disbursed To</th>
                <th className="px-4 py-3 font-semibold">Amount</th>
                <th className="px-4 py-3 font-semibold">Date</th>
                <th className="px-4 py-3 font-semibold">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {payouts.map((po) => (
                <tr key={po.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3.5 font-mono text-xs font-medium text-gray-900">{po.batchRef}</td>
                  <td className="px-4 py-3.5 text-gray-600 text-xs">{po.bankName} (•••• {po.accountLast4})</td>
                  <td className="px-4 py-3.5 font-bold text-gray-900"><PriceDisplay amount={po.amount} /></td>
                  <td className="px-4 py-3.5 text-gray-500 text-xs">{po.date}</td>
                  <td className="px-4 py-3.5">
                    <Badge variant="success" className="flex items-center gap-1 w-fit">
                      <CheckCircle2 className="w-3 h-3" /> Transferred
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Request Payout Modal */}
      <Modal isOpen={payoutModalOpen} onClose={() => setPayoutModalOpen(false)} title="Initiate ACH Payout Transfer">
        {withdrawSuccess ? (
          <div className="text-center py-6">
            <CheckCircle2 className="w-12 h-12 text-green-500 mx-auto mb-3" />
            <h3 className="font-bold text-gray-900">Transfer Initiated</h3>
            <p className="text-sm text-gray-500 mt-1">Funds will arrive in your bank account in 1-2 business days.</p>
          </div>
        ) : (
          <form onSubmit={handleWithdrawSubmit} className="space-y-4">
            <div className="bg-gray-50 p-4 rounded-xl space-y-2 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>Available Balance:</span>
                <span className="font-bold text-green-600"><PriceDisplay amount={availableBalance} /></span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Destination Account:</span>
                <span className="font-medium text-gray-900">JPMorgan Chase •••• 9012</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Withdrawal Amount ($ USD)</label>
              <Input
                type="number"
                min={10}
                max={availableBalance}
                step="0.01"
                value={withdrawAmount}
                onChange={(e) => setWithdrawAmount(parseFloat(e.target.value) || 0)}
                required
              />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" variant="secondary" onClick={() => setPayoutModalOpen(false)}>Cancel</Button>
              <Button type="submit">Confirm Payout Transfer</Button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};
