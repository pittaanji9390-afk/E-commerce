import React, { useState } from 'react';
import { Truck, RefreshCw, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { Modal } from '@/components/ui/Modal';

interface MockOrder {
  id: string;
  orderNumber: string;
  date: string;
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'REFUNDED';
  total: number;
  items: {
    title: string;
    variant: string;
    price: number;
    quantity: number;
    sellerName: string;
    image: string;
  }[];
}

const mockOrders: MockOrder[] = [
  {
    id: 'ord-1',
    orderNumber: 'ORD-1724930129-842',
    date: 'August 28, 2026',
    status: 'DELIVERED',
    total: 399.99,
    items: [
      {
        title: 'Sony WH-1000XM5 Wireless Headphones',
        variant: 'Midnight Black',
        price: 399.99,
        quantity: 1,
        sellerName: 'Apex Innovations LLC',
        image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
      },
    ],
  },
  {
    id: 'ord-2',
    orderNumber: 'ORD-1724819230-109',
    date: 'August 22, 2026',
    status: 'SHIPPED',
    total: 129.50,
    items: [
      {
        title: 'Premium Italian Leather Bifold Wallet',
        variant: 'Chestnut Brown',
        price: 129.50,
        quantity: 1,
        sellerName: 'Artisan Studio',
        image: 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=200',
      },
    ],
  },
];

export const CustomerOrdersPage: React.FC = () => {
  const [selectedOrder, setSelectedOrder] = useState<MockOrder | null>(null);
  const [rmaModalOpen, setRmaModalOpen] = useState(false);
  const [rmaReason, setRmaReason] = useState('DAMAGED');
  const [rmaNotes, setRmaNotes] = useState('');
  const [rmaSubmitted, setRmaSubmitted] = useState(false);

  const getStatusBadge = (status: MockOrder['status']) => {
    switch (status) {
      case 'DELIVERED':
        return <Badge variant="success">Delivered</Badge>;
      case 'SHIPPED':
        return <Badge variant="info">In Transit</Badge>;
      case 'PROCESSING':
        return <Badge variant="warning">Processing</Badge>;
      default:
        return <Badge variant="neutral">{status}</Badge>;
    }
  };

  const handleRmaSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setRmaSubmitted(true);
    setTimeout(() => {
      setRmaSubmitted(false);
      setRmaModalOpen(false);
      setRmaNotes('');
    }, 1800);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Your Orders & Fulfillment</h1>
          <p className="text-gray-500 text-sm mt-1">Track packages, manage return authorizations, and download invoices.</p>
        </div>
      </div>

      <div className="space-y-6">
        {mockOrders.map((order) => (
          <div key={order.id} className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:border-gray-300 transition-colors">
            <div className="bg-gray-50/80 px-6 py-4 border-b border-gray-200 flex flex-wrap items-center justify-between gap-4">
              <div className="flex flex-wrap items-center gap-6 text-sm text-gray-600">
                <div>
                  <span className="block text-xs uppercase font-medium text-gray-400">Order Placed</span>
                  <span className="font-medium text-gray-900">{order.date}</span>
                </div>
                <div>
                  <span className="block text-xs uppercase font-medium text-gray-400">Total</span>
                  <span className="font-medium text-gray-900"><PriceDisplay amount={order.total} /></span>
                </div>
                <div>
                  <span className="block text-xs uppercase font-medium text-gray-400">Order ID</span>
                  <span className="font-mono text-xs text-gray-700">{order.orderNumber}</span>
                </div>
              </div>
              <div className="flex items-center gap-3">
                {getStatusBadge(order.status)}
                <Button size="sm" variant="secondary" onClick={() => setSelectedOrder(order)}>
                  View Details
                </Button>
              </div>
            </div>

            <div className="p-6 divide-y divide-gray-100">
              {order.items.map((item, idx) => (
                <div key={idx} className="flex items-center justify-between py-4 first:pt-0 last:pb-0">
                  <div className="flex items-center gap-4">
                    <img src={item.image} alt={item.title} className="w-16 h-16 object-cover rounded-lg border border-gray-200" />
                    <div>
                      <h4 className="font-medium text-gray-900">{item.title}</h4>
                      <p className="text-xs text-gray-500 mt-0.5">Variant: {item.variant} • Sold by <span className="text-primary-600 font-medium">{item.sellerName}</span></p>
                      <p className="text-xs text-gray-500 mt-0.5">Qty: {item.quantity} × ${item.price.toFixed(2)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    {order.status === 'DELIVERED' && (
                      <Button size="sm" variant="outline" onClick={() => { setSelectedOrder(order); setRmaModalOpen(true); }}>
                        <RefreshCw className="w-3.5 h-3.5 mr-1.5" /> Return / Refund
                      </Button>
                    )}
                    <Button size="sm" variant="secondary">
                      Buy Again
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Order Details Modal */}
      {selectedOrder && (
        <Modal isOpen={!!selectedOrder && !rmaModalOpen} onClose={() => setSelectedOrder(null)} title={`Order Summary #${selectedOrder.orderNumber}`}>
          <div className="space-y-6">
            <div className="bg-blue-50 border border-blue-100 rounded-lg p-4 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Truck className="w-6 h-6 text-blue-600" />
                <div>
                  <h4 className="font-semibold text-blue-900 text-sm">Estimated Delivery</h4>
                  <p className="text-xs text-blue-700">Delivered via FedEx Priority (Tracking #FDX984210938)</p>
                </div>
              </div>
              <Badge variant="success">Verified Delivery</Badge>
            </div>

            <div className="divide-y divide-gray-100">
              {selectedOrder.items.map((item, idx) => (
                <div key={idx} className="flex items-center justify-between py-3">
                  <div>
                    <h5 className="font-medium text-sm text-gray-900">{item.title}</h5>
                    <p className="text-xs text-gray-500">{item.variant}</p>
                  </div>
                  <span className="text-sm font-semibold"><PriceDisplay amount={item.price * item.quantity} /></span>
                </div>
              ))}
            </div>

            <div className="border-t border-gray-200 pt-4 space-y-2 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>Subtotal</span>
                <span>${selectedOrder.total.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Shipping</span>
                <span className="text-green-600 font-medium">Free</span>
              </div>
              <div className="flex justify-between text-base font-bold text-gray-900 pt-2 border-t">
                <span>Grand Total</span>
                <PriceDisplay amount={selectedOrder.total} />
              </div>
            </div>
          </div>
        </Modal>
      )}

      {/* Return RMA Modal */}
      {selectedOrder && (
        <Modal isOpen={rmaModalOpen} onClose={() => setRmaModalOpen(false)} title="Request Return Merchandise Authorization (RMA)">
          {rmaSubmitted ? (
            <div className="text-center py-6">
              <CheckCircle className="w-12 h-12 text-green-500 mx-auto mb-3" />
              <h3 className="font-bold text-gray-900">RMA Request Submitted</h3>
              <p className="text-sm text-gray-500 mt-1">The seller will review your request within 24 business hours.</p>
            </div>
          ) : (
            <form onSubmit={handleRmaSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Reason for Return</label>
                <select
                  value={rmaReason}
                  onChange={(e) => setRmaReason(e.target.value)}
                  className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="DAMAGED">Damaged during shipping</option>
                  <option value="DEFECTIVE">Defective or not functioning</option>
                  <option value="WRONG_ITEM">Received wrong item</option>
                  <option value="NOT_AS_DESCRIBED">Item not as described</option>
                  <option value="SIZE_ISSUE">Incorrect size / fit</option>
                  <option value="CHANGED_MIND">No longer needed</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Additional Details</label>
                <textarea
                  value={rmaNotes}
                  onChange={(e) => setRmaNotes(e.target.value)}
                  required
                  rows={3}
                  placeholder="Describe the issue with the item..."
                  className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t">
                <Button type="button" variant="secondary" onClick={() => setRmaModalOpen(false)}>Cancel</Button>
                <Button type="submit">Submit Return Request</Button>
              </div>
            </form>
          )}
        </Modal>
      )}
    </div>
  );
};
