package com.stockpulse.pricing.controller;

import com.stockpulse.pricing.PricingSuggestion;
import com.stockpulse.pricing.PricingSuggestionService;
import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.pricing.dto.PricingSuggestionDTO;
import com.stockpulse.pricing.mapper.PricingSuggestionMapper;
import com.stockpulse.commerce.CommerceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pricing-suggestions")
public class PricingSuggestionController {
    
    private final PricingSuggestionService pricingSuggestionService;
    private final CommerceService commerceService;
    private final PricingSuggestionMapper pricingSuggestionMapper;
    
    public PricingSuggestionController(PricingSuggestionService pricingSuggestionService,
                                     CommerceService commerceService,
                                     PricingSuggestionMapper pricingSuggestionMapper) {
        this.pricingSuggestionService = pricingSuggestionService;
        this.commerceService = commerceService;
        this.pricingSuggestionMapper = pricingSuggestionMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<PricingSuggestionDTO>> getPendingSuggestions() {
        List<PricingSuggestion> suggestions = pricingSuggestionService.getPendingSuggestions();
        List<PricingSuggestionDTO> suggestionDTOs = suggestions.stream()
            .map(pricingSuggestionMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(suggestionDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PricingSuggestionDTO> getSuggestion(@PathVariable String id) {
        return pricingSuggestionService.getSuggestionById(id)
            .map(suggestion -> ResponseEntity.ok(pricingSuggestionMapper.toDTO(suggestion)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<PricingSuggestionDTO> updateSuggestionStatus(@PathVariable String id, 
                                                                      @Valid @RequestBody UpdateSuggestionStatusDTO statusDTO) {
        PricingSuggestion updatedSuggestion;
        if (statusDTO.status() == SuggestionStatus.ACCEPTED) {
            updatedSuggestion = commerceService.acceptPricingSuggestion(id);
        } else if (statusDTO.status() == SuggestionStatus.REJECTED) {
            updatedSuggestion = commerceService.rejectPricingSuggestion(id);
        } else {
            updatedSuggestion = pricingSuggestionService.updateSuggestionStatus(id, statusDTO.status());
        }
        
        return ResponseEntity.ok(pricingSuggestionMapper.toDTO(updatedSuggestion));
    }
    
    public record UpdateSuggestionStatusDTO(@NotNull SuggestionStatus status) {}
}