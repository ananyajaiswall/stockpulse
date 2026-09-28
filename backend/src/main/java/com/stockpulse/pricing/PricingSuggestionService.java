package com.stockpulse.pricing;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import com.stockpulse.pricing.mapper.PricingSuggestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PricingSuggestionService {
    
    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final PricingSuggestionMapper pricingSuggestionMapper;
    
    public PricingSuggestionService(PricingSuggestionRepository pricingSuggestionRepository, 
                                  PricingSuggestionMapper pricingSuggestionMapper) {
        this.pricingSuggestionRepository = pricingSuggestionRepository;
        this.pricingSuggestionMapper = pricingSuggestionMapper;
    }
    
    public PricingSuggestion createSuggestion(Product product, BigDecimal currentPrice, 
                                            BigDecimal recommendedPrice, PriceDirection direction, 
                                            Double confidence, String reasoning, TriggerReason triggerReason) {
        String id = UUID.randomUUID().toString();
        PricingSuggestion suggestion = new PricingSuggestion(id, product, currentPrice, 
                                                           recommendedPrice, direction, 
                                                           confidence, reasoning, triggerReason);
        return pricingSuggestionRepository.save(suggestion);
    }
    
    @Transactional(readOnly = true)
    public List<PricingSuggestion> getPendingSuggestions() {
        return pricingSuggestionRepository.findByStatus(SuggestionStatus.PENDING);
    }
    
    @Transactional(readOnly = true)
    public List<PricingSuggestion> getSuggestionsByProduct(String productId) {
        return pricingSuggestionRepository.findByProductId(productId);
    }
    
    @Transactional(readOnly = true)
    public Optional<PricingSuggestion> getSuggestionById(String id) {
        return pricingSuggestionRepository.findById(id);
    }
    
    public Optional<PricingSuggestion> findPendingByProductAndTrigger(Product product, TriggerReason triggerReason) {
        return pricingSuggestionRepository.findPendingByProductAndTriggerReason(product, SuggestionStatus.PENDING, triggerReason)
            .stream().findFirst();
    }
    
    public PricingSuggestion updateSuggestionStatus(String id, SuggestionStatus status) {
        PricingSuggestion suggestion = pricingSuggestionRepository.findById(id)
            .orElseThrow(() -> new PricingSuggestionNotFoundException("Pricing suggestion not found with id: " + id));
        
        if (status == SuggestionStatus.ACCEPTED) {
            suggestion.accept();
        } else if (status == SuggestionStatus.REJECTED) {
            suggestion.reject();
        } else {
            suggestion.setStatus(status);
        }
        
        return pricingSuggestionRepository.save(suggestion);
    }
}