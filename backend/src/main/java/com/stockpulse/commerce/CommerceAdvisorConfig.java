package com.stockpulse.commerce;

import com.stockpulse.commerce.rule.RuleBasedCommerceAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

@Configuration
public class CommerceAdvisorConfig {
    
    @Value("${commerce.strategy:rule}")
    private String activeStrategy;
    
    @Bean
    public Map<String, CommerceAdvisor> advisorMap(RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor) {
        return Map.of(
            "rule", ruleBasedCommerceAdvisor
            // Add more strategies here when implemented
        );
    }
    
    @Bean
    @Primary
    public CommerceAdvisor activeAdvisor(Map<String, CommerceAdvisor> advisorMap) {
        return advisorMap.get(activeStrategy);
    }
}