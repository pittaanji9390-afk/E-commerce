import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import {
  ShieldAlert,
  Users,
  Store,
  Layers,
  ShoppingBag,
  CircleDollarSign,
  Scale,
  FileText,
  Activity,
  Sliders,
  ArrowLeft,
} from 'lucide-react';
import { cn } from '@/lib/utils';

export const AdminLayout: React.FC = () => {
  const location = useLocation();

  const adminNav = [
    { label: 'Marketplace KPIs', href: '/admin/dashboard', icon: Activity },
    { label: 'Seller Verification', href: '/admin/sellers', icon: Store },
    { label: 'Customer Accounts', href: '/admin/customers', icon: Users },
    { label: 'Catalog Moderation', href: '/admin/products', icon: Layers },
    { label: 'Global Orders', href: '/admin/orders', icon: ShoppingBag },
    { label: 'Escrow & Payouts', href: '/admin/payouts', icon: CircleDollarSign },
    { label: 'Dispute Arbitration', href: '/admin/disputes', icon: Scale },
    { label: 'Tamper Audit Logs', href: '/admin/audit-logs', icon: FileText },
    { label: 'Platform Settings', href: '/admin/settings', icon: Sliders },
  ];

  return (
    <div className="flex min-h-screen bg-slate-100">
      {/* Admin Sidebar */}
      <aside className="w-64 bg-slate-950 text-slate-300 flex flex-col fixed inset-y-0 z-30 border-r border-slate-800">
        <div className="p-6 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-rose-600 flex items-center justify-center text-white font-bold">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div>
              <div className="text-sm font-bold text-white leading-tight">Admin Governance</div>
              <div className="text-[10px] text-rose-400 font-medium">Platform SuperAdmin</div>
            </div>
          </div>
        </div>

        <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
          {adminNav.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.href;
            return (
              <Link
                key={item.href}
                to={item.href}
                className={cn(
                  'flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-semibold transition-colors',
                  isActive
                    ? 'bg-rose-600 text-white shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-900'
                )}
              >
                <Icon className="w-4 h-4" />
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-slate-800">
          <Link
            to="/"
            className="flex items-center gap-2 text-xs font-medium text-slate-400 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Marketplace
          </Link>
        </div>
      </aside>

      {/* Main Container */}
      <div className="pl-64 flex-1 flex flex-col min-h-screen">
        <header className="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between sticky top-0 z-20">
          <h1 className="text-lg font-bold text-slate-900">Governance & Ledger Control</h1>
          <div className="flex items-center gap-2">
            <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
              ● All Systems Operational
            </span>
          </div>
        </header>

        <main className="p-8 flex-1">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
