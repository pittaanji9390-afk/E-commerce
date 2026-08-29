import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Truck, RotateCcw, Award, ArrowRight, Star } from 'lucide-react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { PriceDisplay } from '@/components/ui/PriceDisplay';

export const HomePage: React.FC = () => {
  const mockFeaturedProducts = [
    {
      id: 'p1',
      title: 'Sony WH-1000XM5 Wireless Noise-Canceling Headphones',
      slug: 'sony-wh-1000xm5-headphones',
      seller: 'AudioTech Direct',
      price: 348.0,
      compareAtPrice: 399.99,
      rating: 4.8,
      reviews: 1420,
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60',
      category: 'Electronics',
    },
    {
      id: 'p2',
      title: 'Apple MacBook Pro 14" M3 Pro Chip (18GB / 512GB)',
      slug: 'apple-macbook-pro-14-m3',
      seller: 'MacReseller Enterprise',
      price: 1849.0,
      compareAtPrice: 1999.0,
      rating: 4.9,
      reviews: 890,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&auto=format&fit=crop&q=60',
      category: 'Laptops',
    },
    {
      id: 'p3',
      title: 'Minimalist Heavyweight Organic Cotton Oversized Hoodie',
      slug: 'minimalist-cotton-hoodie',
      seller: 'StreetWear Collective',
      price: 68.0,
      compareAtPrice: 85.0,
      rating: 4.6,
      reviews: 215,
      image: 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500&auto=format&fit=crop&q=60',
      category: 'Fashion',
    },
    {
      id: 'p4',
      title: 'Barista Touch Espresso Machine & Conical Burr Grinder',
      slug: 'barista-touch-espresso-machine',
      seller: 'KitchenPro Gear',
      price: 799.95,
      compareAtPrice: 999.95,
      rating: 4.7,
      reviews: 640,
      image: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=60',
      category: 'Home & Living',
    },
  ];

  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <div className="relative rounded-3xl bg-gradient-to-r from-slate-900 via-brand-900 to-slate-900 text-white overflow-hidden p-8 md:p-14 shadow-xl">
        <div className="relative z-10 max-w-2xl space-y-6">
          <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/30">
            Multi-Vendor Verified Marketplace
          </span>
          <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight leading-tight">
            Discover Verified Merchants. Shop with Total Confidence.
          </h1>
          <p className="text-slate-300 text-base leading-relaxed">
            Shop directly from hundreds of independent brands and certified sellers. Enjoy isolated escrow protection, verified reviews, and unified multi-vendor checkout.
          </p>
          <div className="flex flex-wrap gap-4 pt-2">
            <Link to="/products">
              <Button size="lg" className="bg-brand-500 hover:bg-brand-600 text-white font-semibold">
                Explore Marketplace <ArrowRight className="w-4 h-4 ml-1" />
              </Button>
            </Link>
            <Link to="/seller/onboarding">
              <Button size="lg" variant="outline" className="bg-white/10 text-white border-white/20 hover:bg-white/20 font-semibold">
                Become a Verified Seller
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* Trust Badges */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {[
          { icon: ShieldCheck, title: 'Escrow Protection', desc: 'Sellers paid only after delivery verification' },
          { icon: Truck, title: 'Multi-Carrier Tracking', desc: 'Live checkpoint updates from certified shippers' },
          { icon: RotateCcw, title: '30-Day Easy Returns', desc: 'Direct merchant RMA arbitration support' },
          { icon: Award, title: 'Authentic Products', desc: '100% verified merchant quality audits' },
        ].map((item, idx) => {
          const Icon = item.icon;
          return (
            <div key={idx} className="flex items-start gap-3.5 p-4 rounded-xl bg-white border border-slate-200 shadow-sm">
              <div className="p-2.5 rounded-lg bg-brand-50 text-brand-600">
                <Icon className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-xs font-bold text-slate-900">{item.title}</h4>
                <p className="text-[11px] text-slate-500 mt-0.5">{item.desc}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Trending Products */}
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-900">Trending Marketplace Products</h2>
            <p className="text-xs text-slate-500">Top-rated items from verified independent merchants</p>
          </div>
          <Link to="/products" className="text-xs font-semibold text-brand-600 hover:text-brand-700 flex items-center gap-1">
            View All <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {mockFeaturedProducts.map((p) => (
            <Card key={p.id} hoverable className="p-4 flex flex-col justify-between group">
              <div>
                <div className="aspect-square rounded-lg overflow-hidden bg-slate-100 mb-3 relative">
                  <img
                    src={p.image}
                    alt={p.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                  <span className="absolute top-2 left-2 px-2 py-0.5 text-[10px] font-bold bg-white/90 backdrop-blur-sm rounded text-slate-800 shadow-sm">
                    {p.category}
                  </span>
                </div>
                <div className="text-[11px] font-semibold text-brand-600 mb-1">
                  Sold by: {p.seller}
                </div>
                <h3 className="text-sm font-semibold text-slate-900 line-clamp-2 leading-snug">
                  <Link to={`/products/${p.slug}`} className="hover:text-brand-600">
                    {p.title}
                  </Link>
                </h3>
                <div className="flex items-center gap-1 text-amber-500 mt-2">
                  <Star className="w-3.5 h-3.5 fill-current" />
                  <span className="text-xs font-bold text-slate-700">{p.rating}</span>
                  <span className="text-[11px] text-slate-400">({p.reviews})</span>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
                <PriceDisplay amount={p.price} compareAtAmount={p.compareAtPrice} />
                <Button size="sm" variant="primary">
                  Add to Cart
                </Button>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
};
