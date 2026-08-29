import React, { useState } from 'react';
import { Plus, AlertTriangle, CheckCircle, Search } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';

interface VariantInventoryRow {
  variantId: string;
  sku: string;
  title: string;
  productTitle: string;
  onHand: number;
  reserved: number;
  available: number;
  lowStockThreshold: number;
}

const mockInventoryData: VariantInventoryRow[] = [
  {
    variantId: 'v-1',
    sku: 'SONY-WH1000XM5-BLK',
    title: 'Midnight Black',
    productTitle: 'Sony WH-1000XM5 Wireless Headphones',
    onHand: 50,
    reserved: 4,
    available: 46,
    lowStockThreshold: 5,
  },
  {
    variantId: 'v-2',
    sku: 'SONY-WH1000XM5-SLV',
    title: 'Platinum Silver',
    productTitle: 'Sony WH-1000XM5 Wireless Headphones',
    onHand: 6,
    reserved: 2,
    available: 4,
    lowStockThreshold: 5,
  },
  {
    variantId: 'v-3',
    sku: 'APEX-HUB-PRO',
    title: 'Space Gray 10-in-1',
    productTitle: 'Apex USB4 Thunderbolt Hub Pro',
    onHand: 120,
    reserved: 12,
    available: 108,
    lowStockThreshold: 10,
  },
];

export const SellerInventoryPage: React.FC = () => {
  const [inventory, setInventory] = useState<VariantInventoryRow[]>(mockInventoryData);
  const [search, setSearch] = useState('');
  const [selectedVariant, setSelectedVariant] = useState<VariantInventoryRow | null>(null);
  const [restockQty, setRestockQty] = useState<number>(25);
  const [restockSuccess, setRestockSuccess] = useState(false);

  const filtered = inventory.filter(
    (item) =>
      item.sku.toLowerCase().includes(search.toLowerCase()) ||
      item.productTitle.toLowerCase().includes(search.toLowerCase())
  );

  const handleRestockSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedVariant) return;

    setInventory((prev) =>
      prev.map((item) =>
        item.variantId === selectedVariant.variantId
          ? {
              ...item,
              onHand: item.onHand + restockQty,
              available: item.available + restockQty,
            }
          : item
      )
    );

    setRestockSuccess(true);
    setTimeout(() => {
      setRestockSuccess(false);
      setSelectedVariant(null);
    }, 1500);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Inventory & Stock Balances</h1>
          <p className="text-gray-500 text-sm mt-1">Real-time concurrency warehouse ledger and low-stock replenishment.</p>
        </div>
      </div>

      <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <div className="flex items-center justify-between gap-4 mb-6">
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by SKU or Product Title..."
              className="pl-9"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 font-semibold">SKU / Variant</th>
                <th className="px-4 py-3 font-semibold">Product Name</th>
                <th className="px-4 py-3 font-semibold text-center">On Hand</th>
                <th className="px-4 py-3 font-semibold text-center">Reserved</th>
                <th className="px-4 py-3 font-semibold text-center">Available</th>
                <th className="px-4 py-3 font-semibold">Status</th>
                <th className="px-4 py-3 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((row) => (
                <tr key={row.variantId} className="hover:bg-gray-50/80">
                  <td className="px-4 py-3.5">
                    <span className="font-mono text-xs font-semibold text-gray-900 block">{row.sku}</span>
                    <span className="text-xs text-gray-500">{row.title}</span>
                  </td>
                  <td className="px-4 py-3.5 text-gray-900 font-medium max-w-xs truncate">{row.productTitle}</td>
                  <td className="px-4 py-3.5 text-center font-semibold text-gray-800">{row.onHand}</td>
                  <td className="px-4 py-3.5 text-center text-amber-600 font-medium">{row.reserved}</td>
                  <td className="px-4 py-3.5 text-center font-bold text-green-600">{row.available}</td>
                  <td className="px-4 py-3.5">
                    {row.available <= row.lowStockThreshold ? (
                      <Badge variant="warning" className="flex items-center gap-1 w-fit">
                        <AlertTriangle className="w-3 h-3" /> Low Stock
                      </Badge>
                    ) : (
                      <Badge variant="success" className="w-fit">Healthy</Badge>
                    )}
                  </td>
                  <td className="px-4 py-3.5 text-right">
                    <Button size="sm" variant="outline" onClick={() => setSelectedVariant(row)}>
                      <Plus className="w-3.5 h-3.5 mr-1" /> Restock
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Restock Batch Modal */}
      {selectedVariant && (
        <Modal isOpen={!!selectedVariant} onClose={() => setSelectedVariant(null)} title={`Restock ${selectedVariant.sku}`}>
          {restockSuccess ? (
            <div className="text-center py-6">
              <CheckCircle className="w-12 h-12 text-green-500 mx-auto mb-3" />
              <h3 className="font-bold text-gray-900">Restock Successful</h3>
              <p className="text-sm text-gray-500 mt-1">Inventory ledger updated with new batch shipment.</p>
            </div>
          ) : (
            <form onSubmit={handleRestockSubmit} className="space-y-4">
              <div className="bg-gray-50 p-4 rounded-xl space-y-1 text-sm">
                <div className="flex justify-between text-gray-600">
                  <span>Product:</span>
                  <span className="font-medium text-gray-900">{selectedVariant.productTitle}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Current Available:</span>
                  <span className="font-bold text-green-600">{selectedVariant.available} units</span>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Batch Units to Restock</label>
                <Input
                  type="number"
                  min={1}
                  value={restockQty}
                  onChange={(e) => setRestockQty(parseInt(e.target.value) || 0)}
                  required
                />
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t">
                <Button type="button" variant="secondary" onClick={() => setSelectedVariant(null)}>Cancel</Button>
                <Button type="submit">Commit Restock</Button>
              </div>
            </form>
          )}
        </Modal>
      )}
    </div>
  );
};
