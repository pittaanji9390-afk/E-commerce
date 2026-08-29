import React, { useState } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { Rating } from '@/components/ui/Rating';
import { Filter, SlidersHorizontal } from 'lucide-react';
import { Link } from 'react-router-dom';

export const ProductsPage: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const [priceRange, setPriceRange] = useState<number>(2000);

  const categories = ['All', 'Electronics', 'Laptops', 'Fashion', 'Home & Living', 'Audio'];

  const products = [
    {
      id: 'p1',
      title: 'Sony WH-1000XM5 Wireless Headphones',
      slug: 'sony-wh-1000xm5-headphones',
      seller: 'AudioTech Direct',
      category: 'Electronics',
      price: 348.0,
      compareAtPrice: 399.99,
      rating: 4.8,
      reviews: 1420,
      stock: 45,
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60',
    },
    {
      id: 'p2',
      title: 'Apple MacBook Pro 14" M3 Pro',
      slug: 'apple-macbook-pro-14-m3',
      seller: 'MacReseller Enterprise',
      category: 'Laptops',
      price: 1849.0,
      compareAtPrice: 1999.0,
      rating: 4.9,
      reviews: 890,
      stock: 12,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&auto=format&fit=crop&q=60',
    },
    {
      id: 'p3',
      title: 'Minimalist Heavyweight Cotton Hoodie',
      slug: 'minimalist-cotton-hoodie',
      seller: 'StreetWear Collective',
      category: 'Fashion',
      price: 68.0,
      compareAtPrice: 85.0,
      rating: 4.6,
      reviews: 215,
      stock: 80,
      image: 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500&auto=format&fit=crop&q=60',
    },
    {
      id: 'p4',
      title: 'Barista Touch Espresso Machine',
      slug: 'barista-touch-espresso-machine',
      seller: 'KitchenPro Gear',
      category: 'Home & Living',
      price: 799.95,
      compareAtPrice: 999.95,
      rating: 4.7,
      reviews: 640,
      stock: 8,
      image: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=60',
    },
  ];

  const filtered = products.filter((p) => {
    const matchCategory = selectedCategory === 'All' || p.category === selectedCategory;
    const matchPrice = p.price <= priceRange;
    return matchCategory && matchPrice;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-200 pb-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Marketplace Catalog</h1>
          <p className="text-xs text-slate-500 mt-0.5">Showing {filtered.length} products from verified independent vendors</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" className="text-xs">
            <SlidersHorizontal className="w-3.5 h-3.5 mr-1" /> Sort: Featured
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        {/* Filters Sidebar */}
        <div className="space-y-6 bg-white p-5 rounded-2xl border border-slate-200 h-fit">
          <div className="flex items-center gap-2 font-bold text-sm text-slate-900 pb-3 border-b border-slate-100">
            <Filter className="w-4 h-4 text-brand-600" />
            Filters
          </div>

          {/* Categories Filter */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">Categories</label>
            <div className="space-y-1">
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`block w-full text-left text-xs px-2.5 py-1.5 rounded-lg transition-colors ${
                    selectedCategory === cat
                      ? 'bg-brand-50 text-brand-700 font-semibold'
                      : 'text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          {/* Price Filter */}
          <div className="space-y-2 pt-4 border-t border-slate-100">
            <div className="flex items-center justify-between text-xs font-bold text-slate-700 uppercase tracking-wider">
              <span>Max Price</span>
              <span className="text-brand-600">${priceRange}</span>
            </div>
            <input
              type="range"
              min={20}
              max={2500}
              step={10}
              value={priceRange}
              onChange={(e) => setPriceRange(Number(e.target.value))}
              className="w-full accent-brand-600 cursor-pointer"
            />
          </div>
        </div>

        {/* Product Grid */}
        <div className="md:col-span-3 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filtered.map((product) => (
            <Card key={product.id} hoverable className="p-4 flex flex-col justify-between group">
              <div>
                <div className="aspect-square rounded-xl overflow-hidden bg-slate-100 mb-3 relative">
                  <img
                    src={product.image}
                    alt={product.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                  <Badge variant="neutral" className="absolute top-2 left-2 bg-white/90 shadow-sm">
                    {product.category}
                  </Badge>
                </div>
                <div className="text-[11px] font-semibold text-brand-600 mb-1">
                  Sold by: {product.seller}
                </div>
                <h3 className="text-sm font-semibold text-slate-900 line-clamp-2">
                  <Link to={`/products/${product.slug}`} className="hover:text-brand-600">
                    {product.title}
                  </Link>
                </h3>
                <div className="mt-2">
                  <Rating value={product.rating} count={product.reviews} />
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
                <PriceDisplay amount={product.price} compareAtAmount={product.compareAtPrice} />
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
