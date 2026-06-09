# Space Report — Roadmap

Check each step off as it compiles and runs correctly.

```
[x] Step 1  — Maven project skeleton (pom.xml + package tree)
[x] Step 2  — Enums (EventType, RiskLevel, AlertStatus, AssetType)
[x] Step 3  — Domain entities (Organization, SpaceAsset, SpaceEvent, RiskAnalysis, Alert, ActionHistory)
[x] Step 4  — Output ports / repository interfaces (5 interfaces in domain/port/out/)
[x] Step 5  — Input ports / use-case interfaces (4 interfaces in domain/port/in/)
[x] Step 6  — Domain services (SpaceEventService, RiskAnalysisService, AlertService — zero infra imports)
[x] Step 7  — BST: SpaceEventBST indexed by riskScore — O(log n) lookup (RF-002)
[x] Step 8  — H2 JPA adapters (Spring Data JPA + H2 implements all output ports)
[x] Step 9  — NASA DONKI HTTP adapter (CME + SEP ingestion — RF-001)
[x] Step 10 — Console adapter: main() wires everything, runs end-to-end demo
[x] Step 11 — Webhook notifier (HTTP POST JSON on CRITICAL alert — RF-005)
[x] Step 12 — Unit tests for BR-001 critical alert logic (JUnit 5 + Mockito) ← ESSENTIAL
[x] Step 13 — Inheritance: abstract DomainEntity base class; all 6 entities extend it (OOP — Herança)
[ ] Step 14 — Complete CRUD: update() + delete() on all 5 port interfaces, adapters, and demo in runner
```

## How to run

```bash
# Compile
mvn compile

# Run unit tests
mvn test

# Run the full demo (fetches from NASA DONKI API)
mvn spring-boot:run
```

## Architecture Rules (enforced throughout)
- Domain classes: ZERO imports from `infrastructure.*`, `javax.persistence.*`, `org.springframework.*`
- Output ports: interfaces in `domain/port/out/` — adapters implement them in `infrastructure/`
- Input ports: interfaces in `domain/port/in/` — domain services implement them
- All dependencies injected via constructor — never `new` inside a service
