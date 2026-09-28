package com.stockpulse.pricing;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, String> {
    
    List<PricingSuggestion> findByStatus(SuggestionStatus status);
    
    List<PricingSuggestion> findByProductId(String productId);
    
    List<PricingSuggestion> findByProductIdAndStatus(String productId, SuggestionStatus status);
    
    @Query("SELECT ps FROM PricingSuggestion ps WHERE ps.product = :product AND ps.status = :status AND ps.triggerReason = :triggerReason")
    List<PricingSuggestion> findPendingByProductAndTriggerReason(@Param("product") Product product, 
                                                                   @Param("status") SuggestionStatus status,
                                                                   @Param("triggerReason") TriggerReason triggerReason);
}