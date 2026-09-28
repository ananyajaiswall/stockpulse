# Architectural Decision Records (ADRs)

## ADR-001: Commerce Logic Boundary

### Context
We need to separate the commerce decision-making logic from the REST API controllers and event handlers to ensure consistency and maintainability.

### Options
1. Embed business logic directly in controllers
2. Create a dedicated service layer
3. Implement a strategy pattern with advisors

### Decision
We chose option 3: Implement a strategy pattern with advisors. This provides:
- Clear separation of concerns
- Easy strategy switching (rule-based vs AI)
- Consistent interface for both HTTP and event-driven paths
- Extensibility for future strategies

### Tradeoffs
- More complex initial setup
- Additional abstraction layer
+ Clean architecture boundaries
+ Easy testing and mocking
+ Strategy extensibility

## ADR-002: Unified vs Separate AI Calls

### Context
When generating pricing and reorder recommendations, we need to decide whether to make one combined AI call or separate calls.

### Options
1. Single combined AI call for both recommendations
2. Separate pricing and reorder AI calls

### Decision
We chose option 2: Separate AI calls. This provides:
- Better modularity and error isolation
- Independent confidence scores
- Easier prompt engineering and tuning
- Independent fallback behavior

### Tradeoffs
- Potentially more API calls
- Slightly more latency
+ Better error handling
+ More granular control
+ Independent optimization opportunities

## ADR-003: Runtime Strategy Switching

### Options
1. Hardcoded strategy selection
2. Configuration-based strategy with restart required
3. Runtime strategy switching via Spring bean registry

### Decision
We chose option 3: Runtime strategy switching via Spring bean registry. This provides:
- No application restart required
- Clean strategy registration
- Easy addition of new strategies
- Configuration-driven selection

### Tradeoffs
- Slightly more complex setup
+ Maximum flexibility
+ No downtime for strategy changes
+ Easy experimentation

## ADR-004: LLM Failure Handling

### Context
LLM calls can fail due to network issues, timeouts, quota limits, or produce invalid responses.

### Options
1. Fail the entire operation when LLM fails
2. Silently ignore LLM failures
3. Explicit fallback to rule-based strategy with logging

### Decision
We chose option 3: Explicit fallback with comprehensive validation and logging. This ensures:
- Resilient system operation
- Clear failure visibility
- Graceful degradation
- Transactional integrity

### Tradeoffs
+ System reliability
+ Operational visibility
- Additional validation complexity
+ User experience preservation

## ADR-005: Agentic Loop Implementation

### Context
The system needs to automatically generate suggestions when inventory or demand conditions change.

### Options
1. Scheduled polling approach
2. Event-driven asynchronous processing
3. Direct synchronous processing

### Decision
We chose option 2: Event-driven asynchronous processing. This provides:
- Immediate trigger response
- Non-blocking operations
- Natural human-in-the-loop integration
- Built-in idempotency protection
- Scalable architecture

### Tradeoffs
+ Responsiveness
+ User experience
+ Separation of concerns
- Complexity of async handling
+ Robustness and scalability

## ADR-006: Extensibility Points

### Context
Future enhancements may include competitor pricing, margin floors, supplier catalogs, etc.

### Decision
We designed clear extension points:
- CommerceAdvisor interface for strategy expansion
- TriggerReason enum for new trigger conditions
- Suggestion entities for new recommendation types
- Event system for new business events
- Configuration properties for tunable parameters

### Examples
- CompetitorAwareCommerceAdvisor
- MarginConstraintCommerceAdvisor
- SupplierCatalogCommerceAdvisor
- SeasonalAdjustmentTrigger

## ADR-007: Deliberate Exclusions

### Context
Scope prioritization for the hackathon timeframe.

### Exclusions and Reasons
1. **Storefront/Payment Integration**: Out of scope - focus on inventory/pricing intelligence
2. **Competitor Scraping**: Deferred - concentrate on core commerce logic
3. **Automated Purchase Orders**: Not required - human approval is core requirement
4. **Supplier Integrations**: External complexity - mock suppliers suffice
5. **Authentication/Authorization**: Not scored - basic API access assumed
6. **Real-time Analytics Dashboard**: Polishing - core functionality first
7. **Multi-tenancy**: Enterprise feature - single merchant focus

### Priority Justification
All exclusions prioritize the core scoring requirements:
- Agentic workflow
- Human approval checkpoint
- AI/resilience
- Commerce strategy abstraction