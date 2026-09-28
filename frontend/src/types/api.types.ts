export interface Product {
  id: string;
  sku: string;
  name: string;
  category: 'ELECTRONICS' | 'APPAREL' | 'HOME';
  currentPrice: number;
  stockLevel: number;
  reorderThreshold: number;
  demandVelocity: number;
  status: 'ACTIVE' | 'PRICE_REVIEW_PENDING' | 'OUT_OF_STOCK';
  costPrice?: number;
  supplierId?: string;
}

export interface PricingSuggestion {
  id: string;
  productId: string;
  productName: string;
  currentPrice: number;
  recommendedPrice: number;
  direction: 'INCREASE' | 'DECREASE' | 'HOLD';
  confidence: number;
  reasoning: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  triggerReason: 'INITIAL' | 'INVENTORY_LOW' | 'DEMAND_SPIKE' | 'MANUAL';
  createdAt: string;
}

export interface ReorderSuggestion {
  id: string;
  productId: string;
  productName: string;
  currentStock: number;
  recommendedQuantity: number;
  suggestedLeadTimeDays?: number;
  confidence: number;
  reasoning: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  triggerReason: 'INITIAL' | 'INVENTORY_LOW' | 'DEMAND_SPIKE' | 'MANUAL';
  createdAt: string;
}