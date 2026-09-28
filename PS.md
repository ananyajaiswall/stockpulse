StockPulse · Hackathon · Solo
AI Inventory &
Dynamic Pricing
5 hours
Solo · Individual
Spring Boot 3.x
React 18 or Angular 17
118 pts total
Brief
Tasks
ADR Guide
Extensions
Design Depth
Scoring & Timeline
Tech Spec
The scenario
ShopStream runs an online store with hundreds of SKUs across electronics, apparel, and home goods. Prices are set manually and reviewed weekly. Inventory levels update in real time as orders flow in — but pricing and replenishment decisions lag behind. When a product sells faster than expected, stock runs low while the price stays flat. When demand suddenly spikes on a trending item, the merchandising team hears about it from a Slack message, opens a spreadsheet, and debates a price change over email.

When stock drops critically low, someone manually decides whether to raise the price to protect remaining inventory, run a clearance discount to move units, or trigger a reorder. When a viral product spikes, someone guesses at a price increase. It works — but it's slow, inconsistent, and fails silently when no one is watching the dashboard.

That's the problem you're solving. When inventory crosses a threshold or demand velocity spikes, your system detects it automatically, uses AI to recommend a price adjustment and a reorder quantity, and puts both recommendations in front of merchandising for approval — without anyone having to ask for it.
Scope for this sprint
You're building a reactive commerce advisor — not a full storefront, cart, or payment system. Product catalog CRUD, competitor scraping, and automated purchase orders are sprint 2 and 3. Your job right now is to nail the inventory-signal → AI recommendation → human approval loop for both pricing and replenishment, and build the foundation that makes those extensions straightforward to add.
What you're building
A domain model — Product, InventorySnapshot, PricingSuggestion, ReorderSuggestion with explicit state machines and extension points for sprint 2 fields
A commerce engine — pluggable strategy interface with rule-based and AI-powered implementations for pricing (and a lightweight rule-based reorder baseline), switchable at runtime without a restart
An AI commerce advisor — LLM examines product context, stock level, demand velocity, and trigger situation; returns recommended price, reorder quantity, confidence scores, and plain-English reasoning merchandising can act on
An agentic recommendation loop — when stock drops below threshold or demand spikes, the system queues pricing and reorder suggestions async for approval — no button click required
A merchandising console — React or Angular UI showing pending suggestions with AI reasoning, accept/reject controls, and badges distinguishing auto-triggered vs manual requests
A few things worth knowing
This is a solo effort — every line of code and every architectural call is yours alone.
AI coding tools — Cursor, Copilot, Claude — are fair game and encouraged. Understand what you've shipped; we'll walk through it together afterwards.
The walkthrough is a conversation, not an exam. Trace an order reducing stock → low inventory trigger → suggestions appearing → accept updating price.
Submit: public GitHub repo, 5-minute demo video, and ADR.md. The ADR matters as much as the code.
Two addenda at the end — seed script and LLM gateway utility — save setup time on things that don't carry evaluation signal.
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}
Keep API key out of repo
Use @Value("${llm.api-key}") from environment variable. Public repos with committed keys get revoked within minutes.




T-1
Domain model & API
Backend
20 pts
~50 min
Start here — state machines before field lists; the transitions matter more than the column names.

Product — SKU, name, category (ELECTRONICS, APPAREL, HOME), current price, stock level, reorder threshold, demand velocity (orders in last 24h), lifecycle: ACTIVE → PRICE_REVIEW_PENDING → ACTIVE (and OUT_OF_STOCK when stock = 0)

PricingSuggestion — product reference, current price, recommended price, change direction (INCREASE, DECREASE, HOLD), confidence (0.0–1.0), reasoning, status (PENDING → ACCEPTED | REJECTED), triggerReason (INITIAL, INVENTORY_LOW, DEMAND_SPIKE, MANUAL)

ReorderSuggestion — product reference, current stock, recommended quantity, suggested lead time days, confidence, reasoning, status (PENDING → ACCEPTED | REJECTED), same triggerReason enum — threads through the agentic loop and UI badges

Endpoints to wire up:
POST /products — create product with initial stock and price
GET /products?status=&category= — filterable catalog list
PATCH /products/{id}/stock — update stock level; fires agentic loop if below reorder threshold
POST /products/{id}/orders — simulate a sale (decrements stock, bumps demand velocity); may fire loop on spike or low stock
POST /products/{id}/suggest-pricing — on-demand pricing suggestion
POST /products/{id}/suggest-reorder — on-demand reorder suggestion
PATCH /pricing-suggestions/{id} — accept/reject; accept updates Product.currentPrice
PATCH /reorder-suggestions/{id} — accept/reject; accept updates stock (simulated inbound shipment)
Designing for what's coming
Sprint 2 brings competitor prices, margin floors, and supplier catalogs. A nullable costPrice or supplierId placeholder now costs nothing. Your ADR should touch on at least one extension field.
Seed data
Addendum A has 8 products — one seeded near reorder threshold for the demo path.
T-2
Pluggable commerce engine
Backend · Design Pattern
25 pts
~55 min
Two contracts — or one unified CommerceAdvisor returning both pricing and reorder recommendations. Your ADR should justify the choice.

Rule-based pricing strategy — if stock < reorder threshold, recommend 10% price increase; if demand velocity > 2× category average, recommend 5% increase; otherwise HOLD. Deterministic, no external deps, solid fallback.

Rule-based reorder strategy — recommend quantity = (reorder threshold × 3) − current stock, minimum 1. Simple baseline for AI to improve on.

AI strategy — T-3; design interfaces with combined pricing + reorder output in mind.

Active strategy switchable via config at runtime — no code change, no restart.

Wire on-demand endpoints to run active strategy and persist suggestions.
Two callers, one interface
HTTP on-demand endpoints and async event handlers in the agentic loop must use the same contracts without modification.
Sprint 2 heads up
CompetitorAwareStrategy should mean implementing the contract and registering — nothing more.
T-3
AI commerce advisor
AI Integration
25 pts
~55 min
AI implementation of your commerce contracts. Give the LLM structured context:
Product name, category, current price, stock level, reorder threshold
Demand velocity vs category peers (include category average in prompt)
Trigger context — inventory low vs demand spike vs manual request
Expect back for pricing: recommended price, change direction, confidence, reasoning.
Expect back for reorder: recommended quantity, confidence, reasoning.

A few things worth getting right:
Validate recommended price is positive and within sane bounds (e.g. not 10× current without flagging)
Validate reorder quantity is a positive integer
Handle timeouts, quota errors, unparseable JSON — fall back to rule-based strategies
Keep AI calls off the critical request path where possible
Two prompts, not one
Inventory-low and demand-spike are genuinely different merchandising decisions. Low stock might mean raise price to protect inventory or clearance to move units — the model needs trigger context to reason well. Spike might mean capitalize with a modest increase. If both prompts are the same document with one field added, the model isn't getting what it needs.
Addendum B
Working callLLM(String prompt) for Gemini, Groq, and Ollama. Prompt construction, parsing, validation, and fallback are yours.
Bonus · +5 pts
POST /products/{id}/suggest-pricing/stream — SSE token stream of AI reasoning before the suggestion lands.
T-4
Agentic recommendation loop
Agentic
15 pts
~45 min
The heart of the system — inventory signals drive recommendations automatically.

Trigger A — inventory low: when PATCH /products/{id}/stock or POST /products/{id}/orders leaves stock < reorder threshold, async run creates PricingSuggestion and ReorderSuggestion with triggerReason = INVENTORY_LOW.

Trigger B — demand spike: when a single order or velocity update pushes demand velocity above a configurable threshold (e.g. 3× category average), async run creates suggestions with triggerReason = DEMAND_SPIKE.

Get these right:
Stock/order endpoints return immediately — recommendation generation is fully async
Suggestions surface in UI on next poll
Skip duplicate PENDING suggestions for same product + triggerReason + suggestion type
AI failure during async path → rule-based fallback for both suggestion types — silent drop is worse than a rule-based recommendation
Accepting pricing suggestion updates currentPrice; accepting reorder suggestion increments stockLevel (simulated inbound)
The loop in plain terms
Observe (stock/velocity signal) → reason (what should price and replenishment be?) → act (queue suggestions) → checkpoint (merchandising approves). The system proposes; humans publish prices and place orders.
Wiring suggestion
Spring @EventListener + @Async, or ApplicationEventPublisher, are clean decoupling options. A scheduled poller isn't quite right — the loop should fire because something changed, not because a timer ticked.
T-5
Merchandising console
React 18 or Angular 17
12 pts floor · +8 pts ceiling
~40 min
Build the interface a merchandiser opens when inventory alerts fire. Doesn't need to be beautiful — needs to show the agentic loop end-to-end.

Floor:
Products in PRICE_REVIEW_PENDING or with pending suggestions — inline pricing and reorder suggestions with confidence and AI reasoning
Accept/Reject for both suggestion types
Badges for INVENTORY_LOW vs DEMAND_SPIKE vs manual — makes the loop visible
Product list showing stock level, current price, demand velocity, status
Simulate sale button or stock PATCH for demo without curl
Polling or refresh; loading and error states
Ceiling: full catalog board, margin display, price history chart, category filters, stock heatmap.
On priorities
Backend and systems design carry more weight than UI polish. A clean functional floor that shows auto-triggered badges beats an elaborate ceiling that cost you the agentic loop or ADR.
T-6
ADR + live walkthrough
Architecture ownership
20 pts
Four solid entries: Context → Options → Decision → Tradeoffs.

Cover: commerce logic placement, strategy switchability, LLM failure handling, agentic loop decoupling.

High-weight entries:
Unified vs split contracts — one AI call returning pricing + reorder vs two separate calls
Extensibility — point to where competitor pricing or margin floors plug in
Deliberate exclusions — frame as priority decisions, not time excuses
Walkthrough: trace order → stock drop → async suggestions → accept price change.
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}

The ADR records thinking that shaped your code — captured entry by entry as you make each call. Format: Context → Options → Decision → Tradeoffs. Write each entry right after the decision, not at the end when you're tired.
The format
Context — What situation requires a decision? What constraints apply?

Options — At least two approaches considered.

Decision — What you chose and why — connect to testability, extensibility, sprint 2 roadmap.

Tradeoffs — What you gave up. Every decision has a cost.
The five decisions to cover
1 Where does commerce logic live?
Service layer, domain model, or dedicated advisor component? What boundary prevents a service class accumulating pricing, reorder, events, and persistence?
2 Unified AI call vs separate pricing/reorder calls?
One prompt returning both recommendations is cheaper and faster; separate calls allow independent fallbacks and clearer contracts. Defend your choice.
3 How does runtime strategy switching work?
Bean map, factory, registry — works from HTTP and async paths; sprint 2's third strategy plugs in without touching existing code.
4 LLM failure handling
Timeouts, malformed JSON, absurd prices (e.g. $0 or $999,999). Async path must still produce rule-based suggestions.
5 Agentic loop trigger and decoupling
How stock updates and order events fire async recommendation without blocking the response. Idempotency on repeated triggers.
6 Extensibility and exclusions
Point to a sprint 2 seam in actual code. Name what you deferred and why.
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}

Not building today — but worth having in your head when modeling entities and interfaces.
Sprint 2 — Competitors & Margins
costPrice and marginFloor on products — AI must not recommend below floor
CompetitorAwareStrategy — third pricing option using scraped competitor data
Supplier catalog — reorder suggestions name preferred supplier
Category-level pricing rules — electronics vs apparel caps
Price change cooldown — no second suggestion within N hours
Sprint 3 — Automation & Storefront
Auto-apply high-confidence pricing when within guardrails
Auto PO generation on reorder accept — integrate supplier API
Customer-facing price display API for storefront
AI uses competitor API as tool before recommending
Bundle pricing across related SKUs
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}

AGT-1 What makes this agentic rather than automated?
Trigger is a specific inventory or demand signal change, not a weekly cron. Action is queuing suggestions, not publishing prices. Checkpoint is merchandising approval before price goes live.
AGT-2 Low stock — raise price or clearance?
Genuinely ambiguous merchandising decision. AI should explain tradeoffs in reasoning, not just output a number. We'll discuss your prompt design in the walkthrough.
AGT-3 Two triggers, shared or separate handlers?
INVENTORY_LOW and DEMAND_SPIKE may both fire for the same product. Idempotency and whether one handler or two — worth an ADR entry.
AGT-4 Accept side effects
Accepting pricing updates live price; accepting reorder simulates inbound stock. Should product return to ACTIVE? What if both suggestions pending — accept one, reject other?
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}

20
Domain & API
25
Commerce engine
25
AI integration
15
Agentic loop
12
UI floor
20
ADR + walkthrough
+8+5
UI ceiling · SSE
Area	What we're looking for	Pts
Entity design	Clean state machines, pricing + reorder suggestions linked to triggers, extension placeholders visible	8
API correctness	Stock updates, simulated orders, accept updates price/stock atomically	7
Persistence	JPA correct, app runs from README in under 5 minutes	5
Commerce contract	Interface before implementations; HTTP + async callers share contracts	10
Runtime switchability	Strategy changes via config without restart	8
Pattern justification	ADR defends unified vs split contracts and strategy pattern	7
Inventory-low prompt	Trigger context, stock vs threshold, merchandising tradeoffs in prompt	8
Demand-spike prompt	Genuinely different from low-stock; velocity context included	8
AI resilience	Bounds validation, fallbacks, async path never silent-drops	9
Agentic loop	Event-driven, idempotent, both suggestion types on trigger	7
Human checkpoint	Prices don't change without accept; named in ADR	8
UI floor	Both suggestion types, badges, simulate sale, accept/reject wired	12
ADR quality	Four+ entries with tradeoffs; extensibility points to code	10
Walkthrough	Trace order → low stock → suggestions → accept price	10
UI ceiling	Catalog board, margin, history — proportional	+8
SSE bonus	Token stream end-to-end	+5
Suggested timeline
Phase 1
~15–20 min
Read, sketch, decide
Entities, unified vs split AI calls, trigger thresholds. ADR before code.
Phase 2
~60–75 min
Domain, API, rule strategies
Products, suggestions, stock/order endpoints, rule-based pricing + reorder, switchability.
Phase 3
~55–65 min
AI advisor + prompts
Two prompts in plain text first. Implement, validate bounds, test broken LLM.
Phase 4
~45–55 min
Agentic loop
Stock low + demand spike triggers. Test simulate order on seeded product.
Phase 5
~40–50 min
Merchandising console
Pending suggestions, badges, simulate sale, accept updates price.
Phase 6
~30–40 min
ADR, demo, submit
Demo shows auto-triggered path after simulated sale drops stock below threshold.
If running behind
Drop SSE bonus first. Then UI ceiling. Protect agentic loop and ADR — highest evaluation signal for commerce systems design.
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}

Backend
Java 17+ · Spring Boot 3.x · Maven/Gradle · JPA · Validation · H2/Postgres · CORS localhost:5173 / 4200
LLM — choose one
Gemini 1.5 Flash · Groq + Llama 3.1 · Ollama local

Pricing output:
{"recommendedPrice":29.99,"direction":"INCREASE","confidence":0.82,"reasoning":"..."}

Reorder output:
{"recommendedQuantity":150,"confidence":0.78,"reasoning":"..."}
Frontend
React 18 (Vite) or Angular 17 — document in ADR
Submission
Public GitHub · /backend + /frontend · README.md under 5 min · ADR.md required · 5-min demo showing inventory-low auto path
Reference · Always available
Addendum A — Seed Script
SQL · H2 and Postgres compatible
-- StockPulse · Seed Data
-- Adapt table/column names to your schema

INSERT INTO products (id, sku, name, category, current_price, stock_level, reorder_threshold, demand_velocity, status) VALUES
  ('PRD-001', 'SKU-ELEC-001', 'Wireless Earbuds Pro',     'ELECTRONICS', 79.99,  45,  20, 3,  'ACTIVE'),
  ('PRD-002', 'SKU-ELEC-002', 'USB-C Hub 7-Port',           'ELECTRONICS', 34.99,  120, 30, 1,  'ACTIVE'),
  ('PRD-003', 'SKU-APP-001',  'Organic Cotton T-Shirt',     'APPAREL',     24.99,  8,   15, 12, 'PRICE_REVIEW_PENDING'),
  ('PRD-004', 'SKU-APP-002',  'Running Shorts — Navy',      'APPAREL',     39.99,  55,  20, 2,  'ACTIVE'),
  ('PRD-005', 'SKU-HOME-001', 'Ceramic Pour-Over Set',      'HOME',        49.99,  22,  10, 4,  'ACTIVE'),
  ('PRD-006', 'SKU-HOME-002', 'LED Desk Lamp — Dimmable',   'HOME',        59.99,  0,   15, 0,  'OUT_OF_STOCK'),
  ('PRD-007', 'SKU-ELEC-003', 'Portable Charger 20K',       'ELECTRONICS', 44.99,  18,  25, 8,  'ACTIVE'),
  ('PRD-008', 'SKU-APP-003',  'Hoodie — Heather Grey',      'APPAREL',     54.99,  11,  12, 15, 'ACTIVE');

-- PRD-003 (T-Shirt): stock 8, threshold 15 — already low, good for immediate demo
-- PRD-008 (Hoodie): stock 11, threshold 12, velocity 15 — demand spike demo:
--   POST /products/PRD-008/orders simulates viral sale
Demo paths
Inventory low: POST order on PRD-003 until stock < 15 → auto pricing + reorder suggestions.
Demand spike: POST multiple orders on PRD-008 → velocity crosses threshold → spike-triggered suggestions.
Addendum B — LLM Gateway Utility
Java · Gemini · Groq · Ollama
package com.stockpulse.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

/**
 * Provider-specific HTTP for Gemini, Groq, Ollama.
 * Returns raw text — parsing, validation, fallback are yours.
 *
 * Configure in application.properties:
 *   llm.provider  = gemini
 *   llm.api-key   = ${LLM_API_KEY}
 *   llm.model     = gemini-1.5-flash
 *   llm.base-url  = https://generativelanguage.googleapis.com
 */
@Component
public class LLMGateway {

    @Value("${llm.provider}") private String provider;
    @Value("${llm.api-key:}") private String apiKey;
    @Value("${llm.model}") private String model;
    @Value("${llm.base-url}") private String baseUrl;
    private final RestClient http = RestClient.create();

    public String callLLM(String prompt) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> callGemini(prompt);
            case "groq"   -> callOpenAICompatible(prompt, baseUrl + "/openai/v1/chat/completions");
            case "ollama" -> callOpenAICompatible(prompt, baseUrl + "/v1/chat/completions");
            default      -> throw new IllegalStateException("Unknown provider: " + provider);
        };
    }

    private String callGemini(String prompt) { /* same as FleetMind */ return ""; }
    private String callOpenAICompatible(String prompt, String url) { return ""; }
}