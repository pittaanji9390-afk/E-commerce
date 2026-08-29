import React from 'react';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import {
  DollarSign,
  ShoppingBag,
  Package,
  AlertTriangle,
  TrendingUp,
  Truck,
} from 'lucide-react';

export const SellerDashboardPage: React.FC = () => {
  const kpis = [
    {
      title: 'Net Escrow Balance',
      value: '$14,820.50',
      change: '+18.2%',
      isPositive: true,
      icon: DollarSign,
      color: 'text-emerald-600 bg-emerald-50',
    },
    {
      title: 'Pending Fulfillment',
      value: '24 Orders',
      change: '4 priority',
      isPositive: true,
      icon: ShoppingBag,
      color: 'text-blue-600 bg-blue-50',
    },
    {
      title: 'Active Listed SKUs',
      value: '184 SKUs',
      change: '100% active',
      isPositive: true,
      icon: Package,
      color: 'text-indigo-600 bg-indigo-50',
    },
    {
      title: 'Low Stock Alerts',
      value: '3 Variants',
      change: 'Restock needed',
      isPositive: false,
      icon: AlertTriangle,
      color: 'text-amber-600 bg-amber-50',
    },
  ];

  const recentOrders = [
    {
      subOrderId: 'SO-2026-0891-A',
      parentOrder: 'ORDER-2026-0891',
      customer: 'Sarah Jenkins',
      items: '2x Wireless Earbuds (Black)',
      amount: '$198.00',
      status: 'PAID',
      fulfillment: 'READY_TO_SHIP',
    },
    {
      subOrderId: 'SO-2026-0888-A',
      parentOrder: 'ORDER-2026-0888',
      customer: 'David Chen',
      items: '1x Studio Monitor Headphones',
      amount: '$299.00',
      status: 'PAID',
      fulfillment: 'SHIPPED',
    },
    {
      subOrderId: 'SO-2026-0875-A',
      parentOrder: 'ORDER-2026-0875',
      customer: 'Alex Rivera',
      items: '1x USB-C DAC Amplifier',
      amount: '$89.50',
      status: 'PAID',
      fulfillment: 'DELIVERED',
    },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Merchant Operations & Analytics</h1>
          <p className="text-xs text-slate-500 mt-0.5">Real-time revenue, order fulfillment queue, and inventory alerts</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" size="sm">
            Export Report
          </Button>
          <Button size="sm">
            + Create New Product
          </Button>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {kpis.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <Card key={idx} className="p-5 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">{kpi.title}</span>
                <div className="text-2xl font-black text-slate-900 mt-1">{kpi.value}</div>
                <div className="flex items-center gap-1 text-[11px] font-bold text-emerald-600 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  {kpi.change}
                </div>
              </div>
              <div className={`p-3 rounded-2xl ${kpi.color}`}>
                <Icon className="w-6 h-6" />
              </div>
            </Card>
          );
        })}
      </div>

      {/* Pending Fulfillment Sub-Orders */}
      <Card className="p-6 space-y-4">
        <div className="flex items-center justify-between pb-4 border-b border-slate-100">
          <div>
            <h3 className="text-base font-bold text-slate-900">Sub-Order Fulfillment Queue</h3>
            <p className="text-xs text-slate-500">Only showing sub-orders assigned to your merchant store</p>
          </div>
          <span className="text-xs font-semibold text-brand-600 hover:underline cursor-pointer">
            View All Sub-Orders →
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                <th className="pb-3">Sub-Order ID</th>
                <th className="pb-3">Customer</th>
                <th className="pb-3">Ordered Items</th>
                <th className="pb-3">Net Revenue</th>
                <th className="pb-3">Status</th>
                <th className="pb-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {recentOrders.map((ord) => (
                <tr key={ord.subOrderId} className="hover:bg-slate-50/50">
                  <td className="py-3 font-mono font-bold text-brand-600">{ord.subOrderId}</td>
                  <td className="py-3 font-medium text-slate-900">{ord.customer}</td>
                  <td className="py-3">{ord.items}</td>
                  <td className="py-3 font-bold text-slate-900">{ord.amount}</td>
                  <td className="py-3">
                    <Badge variant={ord.fulfillment === 'DELIVERED' ? 'success' : ord.fulfillment === 'SHIPPED' ? 'info' : 'warning'}>
                      {ord.fulfillment}
                    </Badge>
                  </td>
                  <td className="py-3 text-right">
                    {ord.fulfillment === 'READY_TO_SHIP' ? (
                      <Button size="sm" variant="primary" className="text-xs py-1">
                        <Truck className="w-3.5 h-3.5 mr-1" /> Ship Item
                      </Button>
                    ) : (
                      <Button size="sm" variant="outline" className="text-xs py-1">
                        View Details
                      </Button>
                    )}
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
