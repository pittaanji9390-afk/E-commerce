export type Role =
  | 'ROLE_SUPER_ADMIN'
  | 'ROLE_ADMIN'
  | 'ROLE_CATALOG_ADMIN'
  | 'ROLE_FINANCE_ADMIN'
  | 'ROLE_SUPPORT_AGENT'
  | 'ROLE_MODERATOR'
  | 'ROLE_SELLER'
  | 'ROLE_SELLER_MANAGER'
  | 'ROLE_CUSTOMER';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: Role[];
  avatarUrl?: string;
  emailVerified: boolean;
  status: 'PENDING_VERIFICATION' | 'ACTIVE' | 'SUSPENDED' | 'DEACTIVATED';
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  iconUrl?: string;
  imageUrl?: string;
  path: string;
  level: number;
}

export interface Brand {
  id: number;
  name: string;
  slug: string;
  logoUrl?: string;
}

export interface ProductVariant {
  id: string;
  sku: string;
  title: string;
  priceAdjustment: number;
  attributes: Record<string, string>;
  availableStock: number;
}

export interface Product {
  id: string;
  sellerId: string;
  sellerName: string;
  category: Category;
  brand?: Brand;
  title: string;
  slug: string;
  sku: string;
  shortDescription: string;
  description: string;
  basePrice: number;
  compareAtPrice?: number;
  currency: string;
  status: 'DRAFT' | 'PENDING_REVIEW' | 'ACTIVE' | 'INACTIVE' | 'REJECTED' | 'ARCHIVED';
  images: { id: string; url: string; isPrimary: boolean; altText?: string }[];
  variants: ProductVariant[];
  ratingAverage: number;
  ratingCount: number;
  totalSales: number;
}

export interface CartItem {
  id: string;
  variantId: string;
  productId: string;
  sellerId: string;
  sellerName: string;
  productTitle: string;
  variantTitle: string;
  sku: string;
  unitPrice: number;
  quantity: number;
  imageUrl?: string;
}

export interface Cart {
  items: CartItem[];
  subtotal: number;
  itemCount: number;
}

export interface OrderItem {
  id: string;
  productId: string;
  variantId: string;
  productTitle: string;
  variantTitle: string;
  sku: string;
  unitPrice: number;
  quantity: number;
  taxAmount: number;
  discountAmount: number;
  totalPrice: number;
}

export interface SellerOrder {
  id: string;
  sellerOrderNumber: string;
  sellerId: string;
  sellerName: string;
  subtotal: number;
  totalAmount: number;
  status: 'PENDING_PAYMENT' | 'PAID' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
  items: OrderItem[];
  trackingNumber?: string;
  carrier?: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  taxAmount: number;
  grandTotal: number;
  currency: string;
  paymentStatus: 'PENDING' | 'AUTHORIZED' | 'PAID' | 'FAILED' | 'REFUNDED';
  orderStatus: 'PENDING_PAYMENT' | 'PAID' | 'PROCESSING' | 'PARTIALLY_SHIPPED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  sellerOrders: SellerOrder[];
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: string;
}

export interface ApiError {
  timestamp: string;
  requestId: string;
  status: number;
  code: string;
  message: string;
  fieldErrors?: { field: string; message: string }[];
}
