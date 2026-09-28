package com.stockpulse.reorder;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, String> {
    
    List<ReorderSuggestion> findByStatus(com.stockpulse.pricing.SuggestionStatus status);
    
    List<ReorderSuggestion> findByProductId(String productId);
    
    List<ReorderSuggestion> findByProductIdAndStatus(String productId, com.stockpulse.pricing.SuggestionStatus status);
    
    @Query("SELECT rs FROM ReorderSuggestion rs WHERE rs.product = :product AND rs.status = :status AND rs.triggerReason = :triggerReason")
    List<ReorderSuggestion> findPendingByProductAndTriggerReason(@Param("product") Product product, 
                                                                   @Param("status") com.stockpulse.pricing.SuggestionStatus status,
                                                                   @Param("triggerReason") TriggerReason triggerReason);
}