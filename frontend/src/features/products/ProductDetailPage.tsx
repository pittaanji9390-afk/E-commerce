import React, { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Rating } from '@/components/ui/Rating';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { ShieldCheck, Truck, RotateCcw, Store, Check } from 'lucide-react';

export const ProductDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const [selectedVariant, setSelectedVariant] = useState<string>('v1');
  const [quantity, setQuantity] = useState<number>(1);

  const product = {
    id: 'p1',
    title: 'Sony WH-1000XM5 Wireless Noise-Canceling Headphones',
    slug: slug || 'sony-wh-1000xm5-headphones',
    sku: 'SNY-WH1000XM5-BLK',
    seller: 'AudioTech Direct',
    sellerSlug: 'audiotech-direct',
    sellerRating: 4.9,
    basePrice: 348.0,
    compareAtPrice: 399.99,
    rating: 4.8,
    reviews: 1420,
    category: 'Electronics',
    inStock: true,
    availableStock: 45,
    description:
      'The Sony WH-1000XM5 headphones rewrite the rules for distraction-free listening. Two processors control 8 microphones for unprecedented noise cancellation and exceptional call quality.',
    features: [
      'Industry-leading Auto NC Optimizer with 8 microphones',
      'Up to 30-hour battery life with quick charging (3 min for 3 hours)',
      'Ultra-comfortable, lightweight design with soft fit leather',
      'Multipoint connection allows switching between devices',
    ],
    variants: [
      { id: 'v1', name: 'Black', inStock: true },
      { id: 'v2', name: 'Silver', inStock: true },
      { id: 'v3', name: 'Midnight Blue', inStock: false },
    ],
    images: [
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&auto=format&fit=crop&q=80',
    ],
  };

  return (
    <div className="space-y-10">
      {/* Breadcrumb */}
      <nav className="text-xs text-slate-500 flex items-center gap-2">
        <Link to="/" className="hover:text-slate-900">Home</Link>
        <span>/</span>
        <Link to="/products" className="hover:text-slate-900">Products</Link>
        <span>/</span>
        <span className="text-slate-900 font-medium">{product.title}</span>
      </nav>

      {/* Main Product Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        {/* Gallery */}
        <div className="space-y-4">
          <div className="aspect-square rounded-3xl overflow-hidden bg-slate-100 border border-slate-200">
            <img
              src={product.images[0]}
              alt={product.title}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="grid grid-cols-4 gap-4">
            {product.images.map((img, idx) => (
              <div key={idx} className="aspect-square rounded-xl overflow-hidden border-2 border-brand-500 cursor-pointer">
                <img src={img} alt="" className="w-full h-full object-cover" />
              </div>
            ))}
          </div>
        </div>

        {/* Product Details & Actions */}
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Badge variant="info">{product.category}</Badge>
              <Badge variant="success">In Stock ({product.availableStock} units)</Badge>
            </div>
            <h1 className="text-2xl lg:text-3xl font-extrabold text-slate-900 leading-tight">
              {product.title}
            </h1>
            <div className="flex items-center gap-4 mt-3">
              <Rating value={product.rating} count={product.reviews} />
              <span className="text-xs text-slate-400">SKU: {product.sku}</span>
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-200 flex items-center justify-between">
            <div>
              <div className="text-xs text-slate-500 font-medium">Merchant Price</div>
              <PriceDisplay amount={product.basePrice} compareAtAmount={product.compareAtPrice} />
            </div>
            <div className="text-right">
              <div className="text-xs text-emerald-600 font-bold">Free 2-Day Shipping</div>
              <div className="text-[11px] text-slate-400">Fulfilled directly by seller</div>
            </div>
          </div>

          {/* Seller Card */}
          <Card className="p-4 bg-white flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-brand-50 text-brand-600 flex items-center justify-center font-bold">
                <Store className="w-5 h-5" />
              </div>
              <div>
                <div className="text-xs text-slate-500">Sold & Shipped by</div>
                <Link to={`/sellers/${product.sellerSlug}`} className="text-sm font-bold text-slate-900 hover:text-brand-600">
                  {product.seller}
                </Link>
              </div>
            </div>
            <div className="text-right">
              <span className="text-xs font-bold text-slate-800">★ {product.sellerRating} / 5.0</span>
              <div className="text-[10px] text-emerald-600 font-medium">Verified Merchant</div>
            </div>
          </Card>

          {/* Variant Selector */}
          <div className="space-y-3">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Select Color
            </label>
            <div className="flex flex-wrap gap-2.5">
              {product.variants.map((v) => (
                <button
                  key={v.id}
                  onClick={() => setSelectedVariant(v.id)}
                  disabled={!v.inStock}
                  className={`px-4 py-2 text-xs font-semibold rounded-xl border transition-all ${
                    selectedVariant === v.id
                      ? 'border-brand-600 bg-brand-50 text-brand-700 ring-2 ring-brand-500/20'
                      : v.inStock
                      ? 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50'
                      : 'border-slate-200 bg-slate-100 text-slate-400 cursor-not-allowed line-through'
                  }`}
                >
                  {v.name} {!v.inStock && '(Out of Stock)'}
                </button>
              ))}
            </div>
          </div>

          {/* Quantity & Buy */}
          <div className="flex items-center gap-4 pt-2">
            <div className="flex items-center rounded-xl border border-slate-200 bg-white p-1">
              <button
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="w-8 h-8 flex items-center justify-center rounded-lg text-slate-600 hover:bg-slate-100 font-bold"
              >
                -
              </button>
              <span className="w-10 text-center text-sm font-bold text-slate-900">{quantity}</span>
              <button
                onClick={() => setQuantity(quantity + 1)}
                className="w-8 h-8 flex items-center justify-center rounded-lg text-slate-600 hover:bg-slate-100 font-bold"
              >
                +
              </button>
            </div>

            <Button size="lg" className="flex-1 font-bold">
              Add {quantity} to Cart - ${(product.basePrice * quantity).toFixed(2)}
            </Button>
          </div>

          {/* Value Props */}
          <div className="grid grid-cols-3 gap-3 pt-4 border-t border-slate-100 text-center">
            <div className="p-3 rounded-xl bg-slate-50">
              <ShieldCheck className="w-4 h-4 mx-auto text-brand-600 mb-1" />
              <span className="text-[11px] font-semibold text-slate-700">Escrow Secure</span>
            </div>
            <div className="p-3 rounded-xl bg-slate-50">
              <Truck className="w-4 h-4 mx-auto text-brand-600 mb-1" />
              <span className="text-[11px] font-semibold text-slate-700">Tracked Shipping</span>
            </div>
            <div className="p-3 rounded-xl bg-slate-50">
              <RotateCcw className="w-4 h-4 mx-auto text-brand-600 mb-1" />
              <span className="text-[11px] font-semibold text-slate-700">30-Day RMA</span>
            </div>
          </div>

          {/* Description & Specs */}
          <div className="space-y-4 pt-4 border-t border-slate-200">
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider">Description</h3>
            <p className="text-xs text-slate-600 leading-relaxed">{product.description}</p>
            <ul className="space-y-2 pt-2">
              {product.features.map((f, i) => (
                <li key={i} className="flex items-center gap-2 text-xs text-slate-700">
                  <Check className="w-3.5 h-3.5 text-emerald-500 flex-shrink-0" />
                  {f}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
