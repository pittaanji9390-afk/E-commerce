import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Package,
  Layers,
  ShoppingBag,
  TrendingUp,
  RotateCcw,
  Star,
  Ticket,
  CreditCard,
  Settings,
  ArrowLeft,
} from 'lucide-react';
import { cn } from '@/lib/utils';

export const SellerLayout: React.FC = () => {
  const location = useLocation();

  const navItems = [
    { label: 'Overview', href: '/seller/dashboard', icon: LayoutDashboard },
    { label: 'Products', href: '/seller/products', icon: Package },
    { label: 'Inventory Grid', href: '/seller/inventory', icon: Layers },
    { label: 'Orders & Fulfillment', href: '/seller/orders', icon: ShoppingBag },
    { label: 'Returns & RMA', href: '/seller/returns', icon: RotateCcw },
    { label: 'Reviews & Feedback', href: '/seller/reviews', icon: Star },
    { label: 'Coupons & Promos', href: '/seller/coupons', icon: Ticket },
    { label: 'Escrow & Payouts', href: '/seller/payouts', icon: CreditCard },
    { label: 'Store Analytics', href: '/seller/analytics', icon: TrendingUp },
    { label: 'Settings', href: '/seller/settings', icon: Settings },
  ];

  return (
    <div className="flex min-h-screen bg-slate-100">
      {/* Sidebar */}
      <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col fixed inset-y-0 z-30">
        <div className="p-6 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-brand-500 flex items-center justify-center text-white font-bold">
              S
            </div>
            <div>
              <div className="text-sm font-bold text-white leading-tight">Seller Center</div>
              <div className="text-[10px] text-emerald-400 font-medium">● Verified Merchant</div>
            </div>
          </div>
        </div>

        <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.href;
            return (
              <Link
                key={item.href}
                to={item.href}
                className={cn(
                  'flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-semibold transition-colors',
                  isActive
                    ? 'bg-brand-600 text-white shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800'
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

      {/* Main Content Area */}
      <div className="pl-64 flex-1 flex flex-col min-h-screen">
        <header className="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between sticky top-0 z-20">
          <h1 className="text-lg font-bold text-slate-900">Seller Operations Console</h1>
          <div className="flex items-center gap-3">
            <span className="text-xs font-medium text-slate-600 bg-slate-100 px-3 py-1.5 rounded-full border border-slate-200">
              Store: <strong className="text-slate-900">TechMart Official Store</strong>
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
