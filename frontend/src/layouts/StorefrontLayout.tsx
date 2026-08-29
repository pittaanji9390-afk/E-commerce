import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { ShoppingCart, Heart, User, Search, Store, ShieldCheck } from 'lucide-react';

export const StorefrontLayout: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col min-h-screen bg-slate-50">
      {/* Top Banner */}
      <header className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16 gap-4">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2 flex-shrink-0">
              <div className="w-9 h-9 rounded-xl bg-brand-600 flex items-center justify-center text-white font-black text-xl shadow-sm">
                M
              </div>
              <span className="text-xl font-extrabold tracking-tight text-slate-900">
                Market<span className="text-brand-600">Place</span>
              </span>
            </Link>

            {/* Search Bar */}
            <div className="flex-1 max-w-2xl">
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  const form = e.currentTarget;
                  const query = (form.elements.namedItem('q') as HTMLInputElement)?.value;
                  if (query) navigate(`/search?q=${encodeURIComponent(query)}`);
                }}
                className="relative"
              >
                <input
                  type="text"
                  name="q"
                  placeholder="Search over 100,000+ multi-vendor products, brands and categories..."
                  className="w-full pl-10 pr-4 py-2 text-sm rounded-full border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 bg-slate-50/50"
                />
                <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              </form>
            </div>

            {/* Navigation Actions */}
            <div className="flex items-center gap-3">
              <Link
                to="/seller/dashboard"
                className="hidden md:flex items-center gap-1.5 text-xs font-semibold text-slate-700 hover:text-brand-600 px-3 py-2 rounded-lg hover:bg-slate-100 transition-colors"
              >
                <Store className="w-4 h-4 text-slate-500" />
                Seller Portal
              </Link>
              <Link
                to="/admin/dashboard"
                className="hidden md:flex items-center gap-1.5 text-xs font-semibold text-slate-700 hover:text-brand-600 px-3 py-2 rounded-lg hover:bg-slate-100 transition-colors"
              >
                <ShieldCheck className="w-4 h-4 text-slate-500" />
                Admin
              </Link>
              <Link
                to="/account/wishlist"
                className="p-2 text-slate-600 hover:text-brand-600 rounded-lg hover:bg-slate-100 transition-colors relative"
                title="Wishlist"
              >
                <Heart className="w-5 h-5" />
              </Link>
              <Link
                to="/cart"
                className="p-2 text-slate-600 hover:text-brand-600 rounded-lg hover:bg-slate-100 transition-colors relative"
                title="Shopping Cart"
              >
                <ShoppingCart className="w-5 h-5" />
                <span className="absolute top-1 right-1 w-4 h-4 bg-brand-600 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                  2
                </span>
              </Link>
              <Link
                to="/login"
                className="flex items-center gap-1.5 text-sm font-medium text-slate-700 hover:text-brand-600 px-3 py-2 rounded-lg hover:bg-slate-100 transition-colors"
              >
                <User className="w-4 h-4" />
                Sign In
              </Link>
            </div>
          </div>
        </div>

        {/* Categories Bar */}
        <nav className="bg-slate-900 text-slate-300 text-xs font-medium px-4 py-2">
          <div className="max-w-7xl mx-auto flex items-center gap-6 overflow-x-auto scrollbar-none">
            <Link to="/products" className="hover:text-white transition-colors flex-shrink-0">
              All Products
            </Link>
            <Link to="/categories/electronics" className="hover:text-white transition-colors flex-shrink-0">
              Electronics & Gadgets
            </Link>
            <Link to="/categories/fashion" className="hover:text-white transition-colors flex-shrink-0">
              Fashion & Apparel
            </Link>
            <Link to="/categories/home-living" className="hover:text-white transition-colors flex-shrink-0">
              Home & Kitchen
            </Link>
            <Link to="/categories/beauty" className="hover:text-white transition-colors flex-shrink-0">
              Beauty & Health
            </Link>
            <Link to="/categories/sports" className="hover:text-white transition-colors flex-shrink-0">
              Sports & Outdoors
            </Link>
          </div>
        </nav>
      </header>

      {/* Main Outlet */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-slate-900 text-slate-400 text-sm border-t border-slate-800 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div>
              <h4 className="text-white font-semibold mb-3">Enterprise Marketplace</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Production-grade multi-vendor platform with isolated seller escrow, ACID transaction guarantees, and sub-order fulfillment.
              </p>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-3">Shop With Us</h4>
              <ul className="space-y-2 text-xs">
                <li><Link to="/products" className="hover:text-white">Browse Catalog</Link></li>
                <li><Link to="/cart" className="hover:text-white">Shopping Cart</Link></li>
                <li><Link to="/account/orders" className="hover:text-white">Track Orders</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-3">Sell With Us</h4>
              <ul className="space-y-2 text-xs">
                <li><Link to="/seller/onboarding" className="hover:text-white">Seller Registration</Link></li>
                <li><Link to="/seller/dashboard" className="hover:text-white">Merchant Dashboard</Link></li>
                <li><a href="/docs/MARKETPLACE.md" className="hover:text-white">Commission Policies</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-3">Security & Compliance</h4>
              <ul className="space-y-2 text-xs">
                <li><span className="text-emerald-400">✓ 256-bit TLS Encrypted</span></li>
                <li><span className="text-emerald-400">✓ Double-Entry Escrow Ledger</span></li>
                <li><span className="text-emerald-400">✓ Idempotent Payment Webhooks</span></li>
              </ul>
            </div>
          </div>
          <div className="pt-8 border-t border-slate-800 text-center text-xs text-slate-500">
            © 2026 Enterprise Multi-Vendor Marketplace Platform. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
};
