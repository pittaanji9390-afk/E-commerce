import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Trash2, Store, ArrowRight, ShieldCheck } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { PriceDisplay } from '@/components/ui/PriceDisplay';

export const CartPage: React.FC = () => {
  const [cartGroups, setCartGroups] = useState([
    {
      sellerId: 's1',
      sellerName: 'AudioTech Direct',
      items: [
        {
          id: 'ci-1',
          title: 'Sony WH-1000XM5 Wireless Headphones',
          variant: 'Black',
          sku: 'SNY-WH1000XM5-BLK',
          unitPrice: 348.0,
          quantity: 1,
          image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200&auto=format&fit=crop&q=60',
        },
      ],
    },
    {
      sellerId: 's2',
      sellerName: 'StreetWear Collective',
      items: [
        {
          id: 'ci-2',
          title: 'Minimalist Heavyweight Organic Cotton Hoodie',
          variant: 'Size L / Charcoal',
          sku: 'STR-HD-CHR-L',
          unitPrice: 68.0,
          quantity: 2,
          image: 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=200&auto=format&fit=crop&q=60',
        },
      ],
    },
  ]);

  const subtotal = cartGroups.reduce((acc, group) => {
    return acc + group.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  }, 0);

  const estimatedTax = subtotal * 0.08;
  const estimatedShipping = 0; // Free promotion
  const grandTotal = subtotal + estimatedTax + estimatedShipping;

  const updateQuantity = (itemId: string, newQty: number) => {
    if (newQty < 1) return;
    setCartGroups((groups) =>
      groups.map((g) => ({
        ...g,
        items: g.items.map((i) => (i.id === itemId ? { ...i, quantity: newQty } : i)),
      }))
    );
  };

  const removeItem = (itemId: string) => {
    setCartGroups((groups) =>
      groups
        .map((g) => ({
          ...g,
          items: g.items.filter((i) => i.id !== itemId),
        }))
        .filter((g) => g.items.length > 0)
    );
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900">Multi-Vendor Shopping Cart</h1>
        <p className="text-xs text-slate-500 mt-1">
          Items from multiple independent vendors will be combined into a single transaction and fulfilled separately.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Cart Items Grouped by Seller */}
        <div className="lg:col-span-2 space-y-6">
          {cartGroups.map((group) => (
            <Card key={group.sellerId} className="p-6 space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <div className="flex items-center gap-2 text-xs font-bold text-slate-900">
                  <Store className="w-4 h-4 text-brand-600" />
                  Vendor Package: <span className="text-brand-600">{group.sellerName}</span>
                </div>
                <span className="text-[11px] text-emerald-600 font-semibold">Standard Shipping: Free</span>
              </div>

              <div className="space-y-4">
                {group.items.map((item) => (
                  <div key={item.id} className="flex items-center gap-4 pb-4 border-b border-slate-100 last:border-0 last:pb-0">
                    <img src={item.image} alt={item.title} className="w-20 h-20 rounded-xl object-cover bg-slate-100 flex-shrink-0" />
                    
                    <div className="flex-1 min-w-0">
                      <h4 className="text-sm font-semibold text-slate-900 truncate">{item.title}</h4>
                      <p className="text-xs text-slate-500 mt-0.5">{item.variant}</p>
                      <div className="text-xs text-slate-400 font-mono mt-0.5">SKU: {item.sku}</div>
                      <PriceDisplay amount={item.unitPrice} className="mt-2 text-sm" />
                    </div>

                    <div className="flex items-center gap-4">
                      <div className="flex items-center rounded-lg border border-slate-200 bg-white p-1">
                        <button
                          onClick={() => updateQuantity(item.id, item.quantity - 1)}
                          className="w-6 h-6 flex items-center justify-center text-slate-600 hover:bg-slate-100 rounded text-xs font-bold"
                        >
                          -
                        </button>
                        <span className="w-8 text-center text-xs font-bold">{item.quantity}</span>
                        <button
                          onClick={() => updateQuantity(item.id, item.quantity + 1)}
                          className="w-6 h-6 flex items-center justify-center text-slate-600 hover:bg-slate-100 rounded text-xs font-bold"
                        >
                          +
                        </button>
                      </div>

                      <button
                        onClick={() => removeItem(item.id)}
                        className="text-slate-400 hover:text-rose-600 transition-colors p-1"
                        title="Remove item"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          ))}
        </div>

        {/* Order Summary & Multi-Vendor Breakdown */}
        <div className="space-y-6">
          <Card className="p-6 space-y-5 bg-slate-50 border-slate-200">
            <h3 className="text-base font-bold text-slate-900">Order Summary</h3>

            <div className="space-y-2.5 text-xs text-slate-600">
              <div className="flex justify-between">
                <span>Subtotal ({cartGroups.reduce((c, g) => c + g.items.reduce((s, i) => s + i.quantity, 0), 0)} items)</span>
                <span className="font-semibold text-slate-900">${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Estimated Shipping</span>
                <span className="font-semibold text-emerald-600">FREE</span>
              </div>
              <div className="flex justify-between">
                <span>Estimated Tax (8%)</span>
                <span className="font-semibold text-slate-900">${estimatedTax.toFixed(2)}</span>
              </div>
              <div className="pt-3 border-t border-slate-200 flex justify-between text-sm font-extrabold text-slate-900">
                <span>Grand Total</span>
                <span className="text-brand-600">${grandTotal.toFixed(2)}</span>
              </div>
            </div>

            <Link to="/checkout" className="block">
              <Button size="lg" className="w-full font-bold">
                Proceed to Checkout <ArrowRight className="w-4 h-4 ml-1" />
              </Button>
            </Link>

            <div className="flex items-center justify-center gap-2 text-[11px] text-slate-500 pt-2">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              Guaranteed Safe & Secure Checkout
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
