package com.stockpulse.reorder.controller;

import com.stockpulse.reorder.ReorderSuggestion;
import com.stockpulse.reorder.ReorderSuggestionService;
import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.reorder.dto.ReorderSuggestionDTO;
import com.stockpulse.reorder.mapper.ReorderSuggestionMapper;
import com.stockpulse.commerce.CommerceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reorder-suggestions")
public class ReorderSuggestionController {
    
    private final ReorderSuggestionService reorderSuggestionService;
    private final CommerceService commerceService;
    private final ReorderSuggestionMapper reorderSuggestionMapper;
    
    public ReorderSuggestionController(ReorderSuggestionService reorderSuggestionService,
                                     CommerceService commerceService,
                                     ReorderSuggestionMapper reorderSuggestionMapper) {
        this.reorderSuggestionService = reorderSuggestionService;
        this.commerceService = commerceService;
        this.reorderSuggestionMapper = reorderSuggestionMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<ReorderSuggestionDTO>> getPendingSuggestions() {
        List<ReorderSuggestion> suggestions = reorderSuggestionService.getPendingSuggestions();
        List<ReorderSuggestionDTO> suggestionDTOs = suggestions.stream()
            .map(reorderSuggestionMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(suggestionDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReorderSuggestionDTO> getSuggestion(@PathVariable String id) {
        return reorderSuggestionService.getSuggestionById(id)
            .map(suggestion -> ResponseEntity.ok(reorderSuggestionMapper.toDTO(suggestion)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ReorderSuggestionDTO> updateSuggestionStatus(@PathVariable String id,
                                                                      @Valid @RequestBody UpdateSuggestionStatusDTO statusDTO) {
        ReorderSuggestion updatedSuggestion;
        if (statusDTO.status() == SuggestionStatus.ACCEPTED) {
            updatedSuggestion = commerceService.acceptReorderSuggestion(id);
        } else if (statusDTO.status() == SuggestionStatus.REJECTED) {
            updatedSuggestion = commerceService.rejectReorderSuggestion(id);
        } else {
            updatedSuggestion = reorderSuggestionService.updateSuggestionStatus(id, statusDTO.status());
        }
        
        return ResponseEntity.ok(reorderSuggestionMapper.toDTO(updatedSuggestion));
    }
    
    public record UpdateSuggestionStatusDTO(@NotNull SuggestionStatus status) {}
}