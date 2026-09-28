import { Product, PricingSuggestion, ReorderSuggestion } from '../types/api.types';

const API_BASE_URL = '';

export const api = {
  // Products
  getProducts: async (status?: string, category?: string): Promise<Product[]> => {
    let url = `${API_BASE_URL}/products`;
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (category) params.append('category', category);
    if (params.toString()) url += `?${params.toString()}`;
    
    try {
      const response = await fetch(url);
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to fetch products: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error('Error fetching products:', error);
      throw error;
    }
  },

  updateStock: async (productId: string, stockLevel: number): Promise<Product> => {
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}/stock`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ stockLevel })
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update stock: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error updating stock for product ${productId}:`, error);
      throw error;
    }
  },

  simulateOrder: async (productId: string, quantity: number): Promise<Product> => {
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ quantity })
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to simulate order: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error simulating order for product ${productId}:`, error);
      throw error;
    }
  },

  createPricingSuggestion: async (productId: string): Promise<PricingSuggestion> => {
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}/suggest-pricing`, {
        method: 'POST'
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to create pricing suggestion: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error creating pricing suggestion for product ${productId}:`, error);
      throw error;
    }
  },

  createReorderSuggestion: async (productId: string): Promise<ReorderSuggestion> => {
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}/suggest-reorder`, {
        method: 'POST'
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to create reorder suggestion: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error creating reorder suggestion for product ${productId}:`, error);
      throw error;
    }
  },

  // Pricing Suggestions
  getPricingSuggestions: async (): Promise<PricingSuggestion[]> => {
    try {
      const response = await fetch(`${API_BASE_URL}/pricing-suggestions`);
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to fetch pricing suggestions: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error('Error fetching pricing suggestions:', error);
      throw error;
    }
  },

  updatePricingSuggestionStatus: async (id: string, status: 'ACCEPTED' | 'REJECTED'): Promise<PricingSuggestion> => {
    try {
      const response = await fetch(`${API_BASE_URL}/pricing-suggestions/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update pricing suggestion status: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error updating pricing suggestion ${id}:`, error);
      throw error;
    }
  },

  // Reorder Suggestions
  getReorderSuggestions: async (): Promise<ReorderSuggestion[]> => {
    try {
      const response = await fetch(`${API_BASE_URL}/reorder-suggestions`);
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to fetch reorder suggestions: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error('Error fetching reorder suggestions:', error);
      throw error;
    }
  },

  updateReorderSuggestionStatus: async (id: string, status: 'ACCEPTED' | 'REJECTED'): Promise<ReorderSuggestion> => {
    try {
      const response = await fetch(`${API_BASE_URL}/reorder-suggestions/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update reorder suggestion status: ${response.status} ${response.statusText} - ${errorText}`);
      }
      return response.json();
    } catch (error) {
      console.error(`Error updating reorder suggestion ${id}:`, error);
      throw error;
    }
  }
};