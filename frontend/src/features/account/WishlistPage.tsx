import React, { useState } from 'react';
import { Heart, ShoppingBag, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { Rating } from '@/components/ui/Rating';
import { Badge } from '@/components/ui/Badge';
import { Link } from 'react-router-dom';

interface WishlistItem {
  id: string;
  productId: string;
  title: string;
  slug: string;
  price: number;
  rating: number;
  ratingCount: number;
  sellerName: string;
  image: string;
  inStock: boolean;
}

const mockWishlist: WishlistItem[] = [
  {
    id: 'w-1',
    productId: 'p-1',
    title: 'Sony WH-1000XM5 Wireless Noise Canceling Headphones',
    slug: 'sony-wh-1000xm5-wireless-headphones',
    price: 399.99,
    rating: 4.9,
    ratingCount: 84,
    sellerName: 'Apex Innovations LLC',
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600',
    inStock: true,
  },
  {
    id: 'w-2',
    productId: 'p-2',
    title: 'Apple MacBook Pro 16" M3 Max Studio Spec',
    slug: 'apple-macbook-pro-16-m3-max',
    price: 3499.00,
    rating: 5.0,
    ratingCount: 42,
    sellerName: 'Apex Innovations LLC',
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600',
    inStock: true,
  },
];

export const WishlistPage: React.FC = () => {
  const [items, setItems] = useState<WishlistItem[]>(mockWishlist);
  const [movedMessage, setMovedMessage] = useState<string | null>(null);

  const handleRemove = (id: string) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
  };

  const handleMoveToCart = (item: WishlistItem) => {
    setMovedMessage(`"${item.title}" added to your cart!`);
    setTimeout(() => setMovedMessage(null), 3000);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Heart className="w-6 h-6 text-red-500 fill-red-500" /> Saved Items & Wishlist
          </h1>
          <p className="text-gray-500 text-sm mt-1">{items.length} saved products ready for purchase.</p>
        </div>
      </div>

      {movedMessage && (
        <div className="mb-6 p-4 bg-green-50 border border-green-200 text-green-800 rounded-xl text-sm font-medium flex items-center justify-between">
          <span>{movedMessage}</span>
          <Link to="/cart" className="underline font-semibold">View Cart</Link>
        </div>
      )}

      {items.length === 0 ? (
        <div className="text-center py-16 bg-white border border-gray-200 rounded-2xl">
          <Heart className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <h3 className="text-lg font-bold text-gray-900">Your wishlist is empty</h3>
          <p className="text-sm text-gray-500 mt-1 mb-6">Explore our marketplace catalog and save products you love.</p>
          <Link to="/products">
            <Button>Explore Products</Button>
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map((item) => (
            <div key={item.id} className="bg-white border border-gray-200 rounded-xl overflow-hidden hover:shadow-md transition-shadow flex flex-col justify-between">
              <Link to={`/products/${item.slug}`} className="block relative aspect-square bg-gray-50 overflow-hidden">
                <img src={item.image} alt={item.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                <Badge variant={item.inStock ? 'success' : 'neutral'} className="absolute top-3 left-3">
                  {item.inStock ? 'In Stock' : 'Backorder'}
                </Badge>
              </Link>

              <div className="p-5 flex-1 flex flex-col justify-between">
                <div>
                  <span className="text-xs text-primary-600 font-medium">{item.sellerName}</span>
                  <Link to={`/products/${item.slug}`} className="block font-semibold text-gray-900 text-base mt-1 line-clamp-2 hover:text-primary-600">
                    {item.title}
                  </Link>
                  <div className="mt-2 flex items-center gap-2">
                    <Rating value={item.rating} />
                    <span className="text-xs text-gray-400">({item.ratingCount})</span>
                  </div>
                  <div className="mt-3">
                    <PriceDisplay amount={item.price} />
                  </div>
                </div>

                <div className="flex items-center gap-2 mt-6 pt-4 border-t border-gray-100">
                  <Button className="flex-1" size="sm" onClick={() => handleMoveToCart(item)}>
                    <ShoppingBag className="w-4 h-4 mr-2" /> Add to Cart
                  </Button>
                  <Button variant="ghost" size="sm" onClick={() => handleRemove(item.id)} className="text-gray-400 hover:text-red-500">
                    <Trash2 className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
