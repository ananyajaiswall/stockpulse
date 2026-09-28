package com.stockpulse.reorder;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.reorder.mapper.ReorderSuggestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ReorderSuggestionService {
    
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final ReorderSuggestionMapper reorderSuggestionMapper;
    
    public ReorderSuggestionService(ReorderSuggestionRepository reorderSuggestionRepository,
                                  ReorderSuggestionMapper reorderSuggestionMapper) {
        this.reorderSuggestionRepository = reorderSuggestionRepository;
        this.reorderSuggestionMapper = reorderSuggestionMapper;
    }
    
    public ReorderSuggestion createSuggestion(Product product, Integer currentStock,
                                            Integer recommendedQuantity, Double confidence,
                                            String reasoning, TriggerReason triggerReason) {
        String id = UUID.randomUUID().toString();
        ReorderSuggestion suggestion = new ReorderSuggestion(id, product, currentStock,
                                                           recommendedQuantity, confidence,
                                                           reasoning, triggerReason);
        return reorderSuggestionRepository.save(suggestion);
    }
    
    @Transactional(readOnly = true)
    public List<ReorderSuggestion> getPendingSuggestions() {
        return reorderSuggestionRepository.findByStatus(SuggestionStatus.PENDING);
    }
    
    @Transactional(readOnly = true)
    public List<ReorderSuggestion> getSuggestionsByProduct(String productId) {
        return reorderSuggestionRepository.findByProductId(productId);
    }
    
    @Transactional(readOnly = true)
    public Optional<ReorderSuggestion> getSuggestionById(String id) {
        return reorderSuggestionRepository.findById(id);
    }
    
    public Optional<ReorderSuggestion> findPendingByProductAndTrigger(Product product, TriggerReason triggerReason) {
        return reorderSuggestionRepository.findPendingByProductAndTriggerReason(product, SuggestionStatus.PENDING, triggerReason)
            .stream().findFirst();
    }
    
    public ReorderSuggestion updateSuggestionStatus(String id, SuggestionStatus status) {
        ReorderSuggestion suggestion = reorderSuggestionRepository.findById(id)
            .orElseThrow(() -> new ReorderSuggestionNotFoundException("Reorder suggestion not found with id: " + id));
        
        if (status == SuggestionStatus.ACCEPTED) {
            suggestion.accept();
        } else if (status == SuggestionStatus.REJECTED) {
            suggestion.reject();
        } else {
            suggestion.setStatus(status);
        }
        
        return reorderSuggestionRepository.save(suggestion);
    }
}