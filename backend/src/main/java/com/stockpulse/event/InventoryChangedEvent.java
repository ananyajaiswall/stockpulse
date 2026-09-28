package com.stockpulse.event;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import org.springframework.context.ApplicationEvent;

public class InventoryChangedEvent extends ApplicationEvent {
    
    private final Product product;
    private final TriggerReason triggerReason;
    
    public InventoryChangedEvent(Object source, Product product, TriggerReason triggerReason) {
        super(source);
        this.product = product;
        this.triggerReason = triggerReason;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public TriggerReason getTriggerReason() {
        return triggerReason;
    }
}