package com.stockpulse.commerce.rule;

import com.stockpulse.commerce.CommerceAdvisor;
import com.stockpulse.commerce.PricingRecommendation;
import com.stockpulse.commerce.ReorderRecommendation;
import com.stockpulse.product.Product;
import com.stockpulse.pricing.PriceDirection;
import com.stockpulse.common.TriggerReason;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("rule")
public class RuleBasedCommerceAdvisor implements CommerceAdvisor {
    
    private static final BigDecimal LOW_STOCK_PRICE_MULTIPLIER = new BigDecimal("1.10");
    private static final BigDecimal DEMAND_SPIKE_PRICE_MULTIPLIER = new BigDecimal("1.05");
    private static final int MIN_REORDER_QUANTITY = 1;
    
    @Override
    public PricingRecommendation generatePricingRecommendation(Product product, TriggerReason triggerReason) {
        if (product.isLowStock()) {
            // Low stock - increase price to protect inventory
            BigDecimal recommendedPrice = product.getCurrentPrice()
                .multiply(LOW_STOCK_PRICE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
            
            return new PricingRecommendation(
                recommendedPrice,
                PriceDirection.INCREASE,
                0.9, // High confidence for rule-based
                String.format("Stock level (%d) is below reorder threshold (%d). Increasing price by 10%% to protect scarce inventory.",
                    product.getStockLevel(), product.getReorderThreshold())
            );
        } else if (triggerReason == TriggerReason.DEMAND_SPIKE) {
            // Demand spike - increase price to manage high demand
            BigDecimal recommendedPrice = product.getCurrentPrice()
                .multiply(DEMAND_SPIKE_PRICE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
            
            return new PricingRecommendation(
                recommendedPrice,
                PriceDirection.INCREASE,
                0.8, // Medium-high confidence
                String.format("Demand velocity (%d) indicates strong interest. Increasing price by 5%% to manage high demand.",
                    product.getDemandVelocity())
            );
        } else {
            // Hold current price
            return new PricingRecommendation(
                product.getCurrentPrice(),
                PriceDirection.HOLD,
                0.95, // High confidence
                "No significant inventory or demand signals detected. Maintaining current price."
            );
        }
    }
    
    @Override
    public ReorderRecommendation generateReorderRecommendation(Product product, TriggerReason triggerReason) {
        // Rule-based reorder calculation: (reorderThreshold * 3) - currentStock
        int recommendedQuantity = (product.getReorderThreshold() * 3) - product.getStockLevel();
        recommendedQuantity = Math.max(recommendedQuantity, MIN_REORDER_QUANTITY);
        
        String reasoning;
        if (triggerReason == TriggerReason.INVENTORY_LOW) {
            reasoning = String.format("Stock level (%d) is below reorder threshold (%d). Calculated reorder quantity: (threshold * 3) - current stock = (%d * 3) - %d = %d.",
                product.getStockLevel(), product.getReorderThreshold(),
                product.getReorderThreshold(), product.getStockLevel(), recommendedQuantity);
        } else if (triggerReason == TriggerReason.DEMAND_SPIKE) {
            reasoning = String.format("High demand velocity (%d) detected. Calculated reorder quantity: (threshold * 3) - current stock = (%d * 3) - %d = %d to prepare for continued demand.",
                product.getDemandVelocity(), product.getReorderThreshold(), product.getStockLevel(), recommendedQuantity);
        } else {
            reasoning = String.format("Calculated reorder quantity: (threshold * 3) - current stock = (%d * 3) - %d = %d.",
                product.getReorderThreshold(), product.getStockLevel(), recommendedQuantity);
        }
        
        return new ReorderRecommendation(
            recommendedQuantity,
            0.85, // Medium-high confidence
            reasoning
        );
    }
}