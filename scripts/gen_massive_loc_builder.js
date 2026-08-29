const fs = require('fs');
const path = require('path');
const { write } = require('./generator_helper');

console.log('Generating Deep Enterprise Migrations, Specifications, and Frontend Dashboards...');

// 1. SQL Migrations
write('backend/src/main/resources/db/migration/V2__b2b_wms_subscriptions.sql', `
-- ==============================================================================
-- V2: B2B WHOLESALE, WMS FULFILLMENT, SUBSCRIPTIONS & LOYALTY
-- ==============================================================================

CREATE TABLE bulk_price_tiers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE CASCADE,
    min_quantity INT NOT NULL CHECK (min_quantity > 0),
    max_quantity INT,
    unit_price NUMERIC(15, 2) NOT NULL CHECK (unit_price >= 0),
    discount_percentage NUMERIC(5, 2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rfq_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    seller_id UUID NOT NULL REFERENCES sellers(id) ON DELETE RESTRICT,
    company_name VARCHAR(200) NOT NULL,
    tax_exemption_number VARCHAR(100),
    credit_terms VARCHAR(30) NOT NULL DEFAULT 'PREPAID',
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    target_price NUMERIC(15, 2),
    quoted_total NUMERIC(15, 2),
    buyer_message TEXT,
    seller_notes TEXT,
    valid_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rfq_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_id UUID NOT NULL REFERENCES rfq_requests(id) ON DELETE CASCADE,
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE RESTRICT,
    requested_quantity INT NOT NULL CHECK (requested_quantity > 0),
    target_unit_price NUMERIC(15, 2),
    offered_unit_price NUMERIC(15, 2)
);

CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    warehouse_type VARCHAR(50) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE warehouse_bins (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    zone_code VARCHAR(20) NOT NULL,
    aisle VARCHAR(20) NOT NULL,
    shelf VARCHAR(20) NOT NULL,
    bin_code VARCHAR(50) NOT NULL UNIQUE,
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    max_capacity INT NOT NULL DEFAULT 500,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pick_lists (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pick_list_number VARCHAR(50) NOT NULL UNIQUE,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    seller_order_id UUID NOT NULL REFERENCES seller_orders(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    assigned_picker VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pick_list_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pick_list_id UUID NOT NULL REFERENCES pick_lists(id) ON DELETE CASCADE,
    bin_id UUID NOT NULL REFERENCES warehouse_bins(id) ON DELETE RESTRICT,
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE RESTRICT,
    quantity_to_pick INT NOT NULL CHECK (quantity_to_pick > 0),
    quantity_picked INT NOT NULL DEFAULT 0,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE CASCADE,
    frequency VARCHAR(30) NOT NULL,
    discount_percentage NUMERIC(5, 2) NOT NULL DEFAULT 10.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subscription_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    plan_id UUID NOT NULL REFERENCES subscription_plans(id) ON DELETE RESTRICT,
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE RESTRICT,
    shipping_address_id UUID NOT NULL REFERENCES customer_addresses(id) ON DELETE RESTRICT,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    recurring_price NUMERIC(15, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    next_billing_date TIMESTAMP WITH TIME ZONE NOT NULL,
    last_billed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loyalty_accounts (
    id UUID PRIMARY KEY REFERENCES customers(id) ON DELETE CASCADE,
    current_points_balance INT NOT NULL DEFAULT 0,
    lifetime_points_earned INT NOT NULL DEFAULT 0,
    tier VARCHAR(30) NOT NULL DEFAULT 'BRONZE',
    referral_code VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
`);

// 2. Additional Frontend Pages (Affiliate Dashboard, Repricer Rule Editor, Ads Campaign Builder)
write('frontend/src/features/seller/SellerRepricerPage.tsx', `
import React, { useState } from 'react';
import { TrendingDown, Zap, ShieldAlert, CheckCircle2, Plus } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';

interface RepricerRuleRow {
  id: string;
  sku: string;
  title: string;
  strategy: string;
  floorPrice: number;
  ceilingPrice: number;
  currentPrice: number;
  active: boolean;
}

const mockRules: RepricerRuleRow[] = [
  {
    id: 'rep-1',
    sku: 'SONY-WH1000XM5-BLK',
    title: 'Sony WH-1000XM5 Wireless Headphones',
    strategy: 'BEAT_BY_PENNY',
    floorPrice: 349.99,
    ceilingPrice: 429.99,
    currentPrice: 399.99,
    active: true,
  },
];

export const SellerRepricerPage: React.FC = () => {
  const [rules] = useState<RepricerRuleRow[]>(mockRules);

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Zap className="w-6 h-6 text-amber-500" /> Automated Algorithmic Repricer
          </h1>
          <p className="text-gray-500 text-sm mt-1">Real-time Buy Box competitive pricing engine with margin floor guardrails.</p>
        </div>
        <Button>
          <Plus className="w-4 h-4 mr-2" /> Add Repricing Rule
        </Button>
      </div>

      <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 font-semibold">SKU / Product</th>
                <th className="px-4 py-3 font-semibold">Algorithm Strategy</th>
                <th className="px-4 py-3 font-semibold text-center">Floor Guardrail</th>
                <th className="px-4 py-3 font-semibold text-center">Ceiling</th>
                <th className="px-4 py-3 font-semibold text-center">Current Live Price</th>
                <th className="px-4 py-3 font-semibold">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {rules.map((rule) => (
                <tr key={rule.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3.5">
                    <span className="font-mono text-xs font-semibold text-gray-900 block">{rule.sku}</span>
                    <span className="text-xs text-gray-500">{rule.title}</span>
                  </td>
                  <td className="px-4 py-3.5 font-medium text-primary-600">{rule.strategy}</td>
                  <td className="px-4 py-3.5 text-center font-bold text-red-600"><PriceDisplay amount={rule.floorPrice} /></td>
                  <td className="px-4 py-3.5 text-center font-bold text-gray-800"><PriceDisplay amount={rule.ceilingPrice} /></td>
                  <td className="px-4 py-3.5 text-center font-black text-green-600"><PriceDisplay amount={rule.currentPrice} /></td>
                  <td className="px-4 py-3.5">
                    <Badge variant={rule.active ? 'success' : 'neutral'}>
                      {rule.active ? 'Monitoring Buy Box' : 'Paused'}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
`);

write('frontend/src/features/seller/SellerAdvertisingPage.tsx', `
import React, { useState } from 'react';
import { Target, BarChart2, Plus, DollarSign, MousePointerClick, Eye } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';

interface CampaignRow {
  id: string;
  name: string;
  productTitle: string;
  dailyBudget: number;
  cpcBid: number;
  impressions: number;
  clicks: number;
  spend: number;
  status: 'ACTIVE' | 'PAUSED';
}

const mockCampaigns: CampaignRow[] = [
  {
    id: 'ad-1',
    name: 'Q3 Premium Audio Discovery',
    productTitle: 'Sony WH-1000XM5 Wireless Headphones',
    dailyBudget: 50.00,
    cpcBid: 0.85,
    impressions: 48200,
    clicks: 1420,
    spend: 1207.00,
    status: 'ACTIVE',
  },
];

export const SellerAdvertisingPage: React.FC = () => {
  const [campaigns] = useState<CampaignRow[]>(mockCampaigns);

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Target className="w-6 h-6 text-rose-600" /> Sponsored Products Advertising
          </h1>
          <p className="text-gray-500 text-sm mt-1">Cost-Per-Click keyword auction campaigns to boost product visibility.</p>
        </div>
        <Button>
          <Plus className="w-4 h-4 mr-2" /> Launch Ad Campaign
        </Button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Total Impressions</span>
            <Eye className="w-5 h-5 text-primary-600" />
          </div>
          <div className="text-3xl font-black text-gray-900">48.2k</div>
        </div>

        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Total Clicks</span>
            <MousePointerClick className="w-5 h-5 text-green-600" />
          </div>
          <div className="text-3xl font-black text-gray-900">1,420</div>
          <span className="text-xs text-green-600 font-semibold mt-1 block">2.94% CTR</span>
        </div>

        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between text-gray-500 mb-2">
            <span className="text-xs uppercase font-semibold">Total Ad Spend</span>
            <DollarSign className="w-5 h-5 text-amber-600" />
          </div>
          <div className="text-3xl font-black text-gray-900"><PriceDisplay amount={1207.00} /></div>
        </div>
      </div>

      <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 font-semibold">Campaign</th>
                <th className="px-4 py-3 font-semibold">Target Product</th>
                <th className="px-4 py-3 font-semibold text-center">Daily Budget</th>
                <th className="px-4 py-3 font-semibold text-center">Max CPC Bid</th>
                <th className="px-4 py-3 font-semibold text-center">Clicks</th>
                <th className="px-4 py-3 font-semibold text-center">Total Spend</th>
                <th className="px-4 py-3 font-semibold">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {campaigns.map((c) => (
                <tr key={c.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3.5 font-bold text-gray-900">{c.name}</td>
                  <td className="px-4 py-3.5 text-xs text-gray-600 max-w-xs truncate">{c.productTitle}</td>
                  <td className="px-4 py-3.5 text-center font-semibold"><PriceDisplay amount={c.dailyBudget} /></td>
                  <td className="px-4 py-3.5 text-center font-semibold"><PriceDisplay amount={c.cpcBid} /></td>
                  <td className="px-4 py-3.5 text-center font-bold text-gray-900">{c.clicks}</td>
                  <td className="px-4 py-3.5 text-center font-bold text-rose-600"><PriceDisplay amount={c.spend} /></td>
                  <td className="px-4 py-3.5"><Badge variant="success">{c.status}</Badge></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
`);

console.log('Massive LOC generator completed.');
`);
