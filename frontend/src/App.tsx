import React, { useState, useEffect } from 'react';
import { api } from './services/api.service';
import { Product, PricingSuggestion, ReorderSuggestion } from './types/api.types';
import './App.css';

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [pricingSuggestions, setPricingSuggestions] = useState<PricingSuggestion[]>([]);
  const [reorderSuggestions, setReorderSuggestions] = useState<ReorderSuggestion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch all data
  const fetchData = async () => {
    try {
      console.log('Fetching data...');
      setLoading(true);
      
      // Test individual API calls first
      const productsData = await api.getProducts();
      console.log('Products loaded:', productsData.length);
      
      const pricingData = await api.getPricingSuggestions();
      console.log('Pricing suggestions loaded:', pricingData.length);
      
      const reorderData = await api.getReorderSuggestions();
      console.log('Reorder suggestions loaded:', reorderData.length);
      
      setProducts(productsData);
      setPricingSuggestions(pricingData);
      setReorderSuggestions(reorderData);
      setError(null);
      
      console.log('All data loaded successfully');
    } catch (err: any) {
      console.error('Error loading data:', err);
      const errorMessage = err.message || 'Failed to load data';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Run a mutation, then refresh so async-generated suggestions show up
  const runAction = async (action: () => Promise<unknown>) => {
    try {
      await action();
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Action failed');
    }
    await fetchData();
  };

  // Initial load and polling
  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000); // Poll every 5 seconds
    return () => clearInterval(interval);
  }, []);

  if (loading && products.length === 0) {
    return (
      <div className="App">
        <header className="App-header">
          <h1>StockPulse — AI Inventory & Dynamic Pricing Advisor</h1>
          <p>Loading dashboard...</p>
        </header>
      </div>
    );
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>StockPulse — AI Inventory & Dynamic Pricing Advisor</h1>
        <p>AI-powered inventory management and dynamic pricing</p>
      </header>

      {error && (
        <div className="error-banner">
          Error: {error}
        </div>
      )}

      <div className="dashboard-summary">
        <h2>Dashboard</h2>
        <p>Products: {products.length}</p>
        <p>Pending Pricing Suggestions: {pricingSuggestions.length}</p>
        <p>Pending Reorder Suggestions: {reorderSuggestions.length}</p>
      </div>

      <div className="section">
        <h2>Products</h2>
        {products.length === 0 ? (
          <p>No products found</p>
        ) : (
          products.map(product => (
            <div key={product.id} className="product-item">
              <h3>{product.name} ({product.sku})</h3>
              <p>Category: {product.category}</p>
              <p>Price: ${product.currentPrice.toFixed(2)}</p>
              <p>Stock: {product.stockLevel} (Threshold: {product.reorderThreshold})</p>
              <p>Demand Velocity: {product.demandVelocity}</p>
              <p>Status: {product.status}</p>
              <button
                className="btn-primary"
                disabled={product.stockLevel === 0}
                onClick={() => runAction(() => api.simulateOrder(product.id, 1))}
              >
                Simulate Sale
              </button>
              <button className="btn-secondary" onClick={() => runAction(() => api.createPricingSuggestion(product.id))}>
                Suggest Pricing
              </button>
              <button className="btn-secondary" onClick={() => runAction(() => api.createReorderSuggestion(product.id))}>
                Suggest Reorder
              </button>
            </div>
          ))
        )}
      </div>
      
      <div className="section">
        <h2>Pricing Suggestions</h2>
        {pricingSuggestions.length === 0 ? (
          <p>No pending pricing suggestions</p>
        ) : (
          pricingSuggestions.map(suggestion => (
            <div key={suggestion.id} className="suggestion-item">
              <h3>Product: {suggestion.productName}</h3>
              <p>Current Price: ${suggestion.currentPrice.toFixed(2)}</p>
              <p>Recommended Price: ${suggestion.recommendedPrice.toFixed(2)}</p>
              <p>Direction: {suggestion.direction}</p>
              <p>Confidence: {(suggestion.confidence * 100).toFixed(1)}%</p>
              <p>Reasoning: {suggestion.reasoning}</p>
              <p>Trigger: {suggestion.triggerReason}</p>
              <p>Status: {suggestion.status}</p>
              <button className="btn-primary" onClick={() => runAction(() => api.updatePricingSuggestionStatus(suggestion.id, 'ACCEPTED'))}>
                Accept
              </button>
              <button className="btn-secondary" onClick={() => runAction(() => api.updatePricingSuggestionStatus(suggestion.id, 'REJECTED'))}>
                Reject
              </button>
            </div>
          ))
        )}
      </div>
      
      <div className="section">
        <h2>Reorder Suggestions</h2>
        {reorderSuggestions.length === 0 ? (
          <p>No pending reorder suggestions</p>
        ) : (
          reorderSuggestions.map(suggestion => (
            <div key={suggestion.id} className="suggestion-item">
              <h3>Product: {suggestion.productName}</h3>
              <p>Current Stock: {suggestion.currentStock}</p>
              <p>Recommended Quantity: {suggestion.recommendedQuantity}</p>
              {suggestion.suggestedLeadTimeDays != null && (
                <p>Suggested Lead Time: {suggestion.suggestedLeadTimeDays} days</p>
              )}
              <p>Confidence: {(suggestion.confidence * 100).toFixed(1)}%</p>
              <p>Reasoning: {suggestion.reasoning}</p>
              <p>Trigger: {suggestion.triggerReason}</p>
              <p>Status: {suggestion.status}</p>
              <button className="btn-primary" onClick={() => runAction(() => api.updateReorderSuggestionStatus(suggestion.id, 'ACCEPTED'))}>
                Accept
              </button>
              <button className="btn-secondary" onClick={() => runAction(() => api.updateReorderSuggestionStatus(suggestion.id, 'REJECTED'))}>
                Reject
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default App;