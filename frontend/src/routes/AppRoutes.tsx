import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { StorefrontLayout } from '@/layouts/StorefrontLayout';
import { SellerLayout } from '@/layouts/SellerLayout';
import { AdminLayout } from '@/layouts/AdminLayout';

import { HomePage } from '@/features/home/HomePage';
import { ProductsPage } from '@/features/products/ProductsPage';
import { ProductDetailPage } from '@/features/products/ProductDetailPage';
import { CartPage } from '@/features/cart/CartPage';
import { CheckoutPage } from '@/features/checkout/CheckoutPage';
import { LoginPage } from '@/features/auth/LoginPage';
import { RegisterPage } from '@/features/auth/RegisterPage';
import { CustomerOrdersPage } from '@/features/account/CustomerOrdersPage';
import { WishlistPage } from '@/features/account/WishlistPage';
import { CustomerProfilePage } from '@/features/account/CustomerProfilePage';
import { SellerDashboardPage } from '@/features/seller/SellerDashboardPage';
import { SellerInventoryPage } from '@/features/seller/SellerInventoryPage';
import { SellerPayoutsPage } from '@/features/seller/SellerPayoutsPage';
import { SellerProductCreatePage } from '@/features/seller/SellerProductCreatePage';
import { AdminDashboardPage } from '@/features/admin/AdminDashboardPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Customer Storefront Routes */}
      <Route element={<StorefrontLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/products/:slug" element={<ProductDetailPage />} />
        <Route path="/search" element={<ProductsPage />} />
        <Route path="/categories/:slug" element={<ProductsPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Customer Account & Order Routes */}
        <Route path="/account/orders" element={<CustomerOrdersPage />} />
        <Route path="/account/wishlist" element={<WishlistPage />} />
        <Route path="/account/profile" element={<CustomerProfilePage />} />
        <Route path="/account/addresses" element={<CustomerProfilePage />} />
      </Route>

      {/* Seller Dashboard Routes */}
      <Route path="/seller" element={<SellerLayout />}>
        <Route index element={<Navigate to="/seller/dashboard" replace />} />
        <Route path="dashboard" element={<SellerDashboardPage />} />
        <Route path="products" element={<SellerDashboardPage />} />
        <Route path="products/new" element={<SellerProductCreatePage />} />
        <Route path="inventory" element={<SellerInventoryPage />} />
        <Route path="orders" element={<SellerDashboardPage />} />
        <Route path="returns" element={<SellerDashboardPage />} />
        <Route path="reviews" element={<SellerDashboardPage />} />
        <Route path="coupons" element={<SellerDashboardPage />} />
        <Route path="payouts" element={<SellerPayoutsPage />} />
        <Route path="analytics" element={<SellerDashboardPage />} />
        <Route path="settings" element={<SellerDashboardPage />} />
      </Route>

      {/* Admin Operations Routes */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<Navigate to="/admin/dashboard" replace />} />
        <Route path="dashboard" element={<AdminDashboardPage />} />
        <Route path="sellers" element={<AdminDashboardPage />} />
        <Route path="customers" element={<AdminDashboardPage />} />
        <Route path="products" element={<AdminDashboardPage />} />
        <Route path="orders" element={<AdminDashboardPage />} />
        <Route path="payouts" element={<AdminDashboardPage />} />
        <Route path="disputes" element={<AdminDashboardPage />} />
        <Route path="audit-logs" element={<AdminDashboardPage />} />
        <Route path="settings" element={<AdminDashboardPage />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
