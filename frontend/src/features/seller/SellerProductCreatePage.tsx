import React, { useState } from 'react';
import { Plus, Image, Trash2, Check, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useNavigate } from 'react-router-dom';

export const SellerProductCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [sku, setSku] = useState('');
  const [categoryId, setCategoryId] = useState('1');
  const [basePrice, setBasePrice] = useState('199.99');
  const [compareAtPrice, setCompareAtPrice] = useState('249.99');
  const [description, setDescription] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [createdSuccess, setCreatedSuccess] = useState(false);

  const [variants, setVariants] = useState([
    { sku: 'VAR-1', title: 'Default Color / Standard', priceAdjustment: 0, initialStock: 25 },
  ]);

  const handleAddVariant = () => {
    setVariants((prev) => [
      ...prev,
      {
        sku: `SKU-${Date.now().toString().substring(7)}`,
        title: 'New Variant (e.g. Black / Large)',
        priceAdjustment: 0,
        initialStock: 10,
      },
    ]);
  };

  const handleRemoveVariant = (index: number) => {
    setVariants((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setCreatedSuccess(true);
    setTimeout(() => {
      navigate('/seller/products');
    }, 1500);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" onClick={() => navigate('/seller/dashboard')}>
          <ArrowLeft className="w-4 h-4 mr-1" /> Back
        </Button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">List New Product</h1>
          <p className="text-gray-500 text-sm mt-0.5">Publish a new marketplace listing with multi-dimensional variants.</p>
        </div>
      </div>

      {createdSuccess && (
        <div className="p-4 bg-green-50 border border-green-200 text-green-800 rounded-xl text-sm font-medium flex items-center gap-2">
          <Check className="w-4 h-4 text-green-600" /> Product listing published successfully! Redirecting...
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Core Product Information */}
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-4">
          <h3 className="font-bold text-gray-900 text-base border-b pb-3">Product Overview</h3>

          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Product Title</label>
            <Input value={title} onChange={(e) => setTitle(e.target.value)} required placeholder="e.g. Studio Pro Wireless ANC Headphones" />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Master SKU</label>
              <Input value={sku} onChange={(e) => setSku(e.target.value)} required placeholder="PROD-STUDIO-01" />
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Category</label>
              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="1">Electronics & Gadgets</option>
                <option value="2">Audio & Headphones</option>
                <option value="3">Apparel & Fashion</option>
                <option value="4">Home & Living</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Base Price ($ USD)</label>
              <Input type="number" step="0.01" value={basePrice} onChange={(e) => setBasePrice(e.target.value)} required />
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Compare-At Price (MSRP)</label>
              <Input type="number" step="0.01" value={compareAtPrice} onChange={(e) => setCompareAtPrice(e.target.value)} />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Full Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
              rows={4}
              placeholder="Highlight technical specifications, key features, and warranty details..."
              className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Primary Image URL</label>
            <div className="flex gap-3">
              <Input value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} placeholder="https://images.unsplash.com/photo-..." className="flex-1" />
              <Button type="button" variant="secondary"><Image className="w-4 h-4 mr-1.5" /> Upload</Button>
            </div>
          </div>
        </div>

        {/* Variants Builder */}
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between border-b pb-3">
            <h3 className="font-bold text-gray-900 text-base">SKU Variants & Initial Inventory</h3>
            <Button type="button" size="sm" variant="outline" onClick={handleAddVariant}>
              <Plus className="w-3.5 h-3.5 mr-1" /> Add Variant
            </Button>
          </div>

          <div className="space-y-3">
            {variants.map((v, idx) => (
              <div key={idx} className="p-4 bg-gray-50 rounded-xl border border-gray-200 grid grid-cols-1 sm:grid-cols-4 gap-3 items-center">
                <div>
                  <label className="block text-[10px] font-bold uppercase text-gray-400">Variant Title</label>
                  <Input
                    value={v.title}
                    onChange={(e) => {
                      const updated = [...variants];
                      updated[idx].title = e.target.value;
                      setVariants(updated);
                    }}
                    className="text-xs"
                  />
                </div>
                <div>
                  <label className="block text-[10px] font-bold uppercase text-gray-400">Variant SKU</label>
                  <Input
                    value={v.sku}
                    onChange={(e) => {
                      const updated = [...variants];
                      updated[idx].sku = e.target.value;
                      setVariants(updated);
                    }}
                    className="text-xs"
                  />
                </div>
                <div>
                  <label className="block text-[10px] font-bold uppercase text-gray-400">Stock Qty</label>
                  <Input
                    type="number"
                    min={0}
                    value={v.initialStock}
                    onChange={(e) => {
                      const updated = [...variants];
                      updated[idx].initialStock = parseInt(e.target.value) || 0;
                      setVariants(updated);
                    }}
                    className="text-xs"
                  />
                </div>
                <div className="flex items-center justify-between pt-3 sm:pt-0">
                  <span className="text-xs text-gray-500">+${v.priceAdjustment}</span>
                  {variants.length > 1 && (
                    <Button type="button" variant="ghost" size="sm" onClick={() => handleRemoveVariant(idx)} className="text-red-500 hover:text-red-600 p-1">
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Button type="button" variant="secondary" onClick={() => navigate('/seller/dashboard')}>Cancel</Button>
          <Button type="submit">Publish Product Listing</Button>
        </div>
      </form>
    </div>
  );
};
