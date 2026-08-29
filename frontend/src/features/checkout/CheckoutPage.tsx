import React, { useState } from 'react';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ShieldCheck, CheckCircle2, CreditCard, Lock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState<number>(1);
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  const handlePlaceOrder = () => {
    setIsProcessing(true);
    setTimeout(() => {
      setIsProcessing(false);
      navigate('/order-confirmation/ORDER-2026-0001');
    }, 1500);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      {/* Checkout Stages Header */}
      <div className="flex items-center justify-between pb-6 border-b border-slate-200">
        {[
          { num: 1, title: 'Shipping Address' },
          { num: 2, title: 'Delivery Tier' },
          { num: 3, title: 'Payment & Review' },
        ].map((s) => (
          <div key={s.num} className="flex items-center gap-2">
            <div
              className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
                step >= s.num
                  ? 'bg-brand-600 text-white'
                  : 'bg-slate-200 text-slate-600'
              }`}
            >
              {step > s.num ? <CheckCircle2 className="w-4 h-4" /> : s.num}
            </div>
            <span className={`text-xs font-bold ${step >= s.num ? 'text-slate-900' : 'text-slate-400'}`}>
              {s.title}
            </span>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Step Forms */}
        <div className="lg:col-span-2 space-y-6">
          {step === 1 && (
            <Card className="p-6 space-y-4">
              <h3 className="text-base font-bold text-slate-900">1. Shipping Address</h3>
              <div className="grid grid-cols-2 gap-4">
                <Input label="First Name" defaultValue="John" />
                <Input label="Last Name" defaultValue="Doe" />
              </div>
              <Input label="Street Address" defaultValue="100 Enterprise Way, Suite 400" />
              <div className="grid grid-cols-3 gap-4">
                <Input label="City" defaultValue="San Francisco" />
                <Input label="State" defaultValue="CA" />
                <Input label="Postal Code" defaultValue="94105" />
              </div>
              <Input label="Phone Number" defaultValue="+1 (555) 234-5678" />
              <div className="pt-2">
                <Button onClick={() => setStep(2)}>Continue to Delivery</Button>
              </div>
            </Card>
          )}

          {step === 2 && (
            <Card className="p-6 space-y-4">
              <h3 className="text-base font-bold text-slate-900">2. Select Delivery Method</h3>
              <div className="space-y-3">
                <label className="flex items-center justify-between p-4 rounded-xl border-2 border-brand-500 bg-brand-50/50 cursor-pointer">
                  <div className="flex items-center gap-3">
                    <input type="radio" name="shipping" defaultChecked className="accent-brand-600" />
                    <div>
                      <div className="text-xs font-bold text-slate-900">Standard Insured Shipping</div>
                      <div className="text-[11px] text-slate-500">Delivered in 2-4 business days</div>
                    </div>
                  </div>
                  <span className="text-xs font-bold text-emerald-600">FREE</span>
                </label>
                <label className="flex items-center justify-between p-4 rounded-xl border border-slate-200 hover:bg-slate-50 cursor-pointer">
                  <div className="flex items-center gap-3">
                    <input type="radio" name="shipping" className="accent-brand-600" />
                    <div>
                      <div className="text-xs font-bold text-slate-900">Express Priority Dispatch</div>
                      <div className="text-[11px] text-slate-500">Next-day guaranteed air delivery</div>
                    </div>
                  </div>
                  <span className="text-xs font-bold text-slate-900">$19.99</span>
                </label>
              </div>
              <div className="flex gap-3 pt-2">
                <Button variant="outline" onClick={() => setStep(1)}>Back</Button>
                <Button onClick={() => setStep(3)}>Continue to Payment</Button>
              </div>
            </Card>
          )}

          {step === 3 && (
            <Card className="p-6 space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-base font-bold text-slate-900">3. Secure Escrow Payment</h3>
                <span className="flex items-center gap-1 text-[11px] text-emerald-600 font-semibold">
                  <Lock className="w-3.5 h-3.5" /> 256-Bit Encrypted
                </span>
              </div>

              <div className="p-4 rounded-xl border-2 border-brand-500 bg-slate-50 space-y-3">
                <div className="flex items-center gap-2 text-xs font-bold text-slate-900">
                  <CreditCard className="w-4 h-4 text-brand-600" />
                  Stripe Payment Intent (Sandbox Simulation)
                </div>
                <Input label="Card Number" defaultValue="4242 •••• •••• 4242" disabled />
                <div className="grid grid-cols-2 gap-4">
                  <Input label="Expiration" defaultValue="12/28" disabled />
                  <Input label="CVC" defaultValue="•••" disabled />
                </div>
              </div>

              <div className="flex gap-3 pt-2">
                <Button variant="outline" onClick={() => setStep(2)}>Back</Button>
                <Button
                  onClick={handlePlaceOrder}
                  isLoading={isProcessing}
                  className="flex-1 bg-emerald-600 hover:bg-emerald-700 font-bold"
                >
                  Pay & Authorize Order ($522.72)
                </Button>
              </div>
            </Card>
          )}
        </div>

        {/* Breakdown Panel */}
        <div>
          <Card className="p-6 space-y-4 bg-slate-50">
            <h4 className="text-sm font-bold text-slate-900">Order Decomposition</h4>
            <div className="space-y-3 text-xs border-b border-slate-200 pb-4">
              <div>
                <div className="font-semibold text-slate-800">Package 1 (AudioTech Direct)</div>
                <div className="text-slate-500 text-[11px]">• 1x Sony WH-1000XM5 ($348.00)</div>
              </div>
              <div>
                <div className="font-semibold text-slate-800">Package 2 (StreetWear Collective)</div>
                <div className="text-slate-500 text-[11px]">• 2x Cotton Hoodie ($136.00)</div>
              </div>
            </div>

            <div className="space-y-2 text-xs text-slate-600">
              <div className="flex justify-between">
                <span>Items Subtotal</span>
                <span className="font-semibold text-slate-900">$484.00</span>
              </div>
              <div className="flex justify-between">
                <span>Total Tax</span>
                <span className="font-semibold text-slate-900">$38.72</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping</span>
                <span className="font-semibold text-emerald-600">FREE</span>
              </div>
              <div className="pt-2 border-t border-slate-200 flex justify-between text-sm font-bold text-slate-900">
                <span>Total Due</span>
                <span className="text-brand-600">$522.72</span>
              </div>
            </div>

            <div className="flex items-center gap-2 text-[11px] text-slate-500 pt-2">
              <ShieldCheck className="w-4 h-4 text-emerald-600 flex-shrink-0" />
              Funds held in Escrow until packages are delivered.
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
