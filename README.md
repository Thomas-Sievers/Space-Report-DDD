# 🛰️ Space Report

**Sistema Inteligente de Monitoramento Climático Espacial e Infraestrutura de Alertas em Tempo Real**

FIAP — Engenharia de Software • Global Solution 2026.1 — *Domain-Driven Design (DDD)*

---

## 👥 Integrantes (Squad)

| Nome | RM |
|---|---|
| Áurea Sardinha Carminato | 563837 |
| Eduarda de Castro Coutinho dos Santos | 562184 |
| Mariana Souza França | 562353 |
| Thomas Soares Sievers | 563566 |

---

## 📖 Sobre o Projeto

O **Space Report** é uma infraestrutura analítica de missão crítica que monitora o **clima espacial (Space Weather)** e dispara alertas automáticos para operadores de satélites e tripulações em ambientes extraveiculares.

O sistema ingere telemetria solar em tempo real da **API pública DONKI da NASA**, calcula o índice de risco de cada anomalia (Ejeções de Massa Coronal — CME, e Eventos de Partículas Solares — SEP) e orquestra o disparo de alertas quando os limiares de segurança são violados para o tipo de órbita de cada ativo.

### Problema que resolve

A economia espacial deixou de ser exclusivamente governamental e tornou-se um mercado comercial multibilionário, com projeção de **100.000 satélites ativos até 2030** (ESA). Erupções solares, CMEs e SEPs ameaçam:

- **Hardwares orbitais** — queima irreversível de barramentos de comunicação e degradação de componentes;
- **Tripulações** — exposição à radiação durante Atividades Extraveiculares (EVAs).

Hoje, a triagem desses eventos depende de processos manuais lentos. O Space Report **automatiza** a correlação dos índices de risco e o disparo de salvaguardas em escala de segundos.

### Objetivos

- Ingerir telemetria espacial (CME + SEP) da NASA DONKI de forma automatizada;
- Calcular o índice de risco de cada evento e indexá-lo em uma **Árvore Binária de Busca (BST)** para consultas otimizadas em `O(log n)`;
- Persistir todos os dados com integridade referencial (PK/FK);
- Disparar **webhooks** automáticos para os clientes ao detectar violação de limiares;
- Demonstrar **DDD**, **Arquitetura Hexagonal** e **POO** sobre um domínio de negócio real.

---

## 🏛️ Arquitetura — Hexagonal (Ports & Adapters)

O domínio (regras de negócio) **nunca** depende de infraestrutura (banco de dados, HTTP, frameworks). A infraestrutura implementa as interfaces (ports) definidas pelo domínio.

```
                    ┌──────────────────────────────────────┐
                    │              DOMÍNIO (core)            │
   Input Adapters   │  ┌────────────┐      ┌─────────────┐  │   Output Adapters
   ───────────────► │  │ Input Ports│─────►│  Services   │  │ ◄───────────────
   SpaceReportRunner│  │ (use cases)│      │ (regras de  │  │  H2/JPA Adapters
   (Console/Runner) │  └────────────┘      │  negócio)   │  │  NasaDonkiClient
                    │                       └──────┬──────┘  │  WebhookNotifier
                    │                  ┌────────────▼──────┐ │
                    │                  │   Output Ports    │ │
                    │                  │ (repos, notifiers)│ │
                    │                  └───────────────────┘ │
                    └──────────────────────────────────────┘
        Sem imports de Spring, JPA ou HTTP dentro do domínio ✅
```

**Regras de ouro (verificadas):**

1. Classes de domínio têm **zero imports** de `infrastructure.*`, `jakarta.persistence.*` ou `org.springframework.*`;
2. Output ports são interfaces em `domain/port/out/` — adapters as implementam em `infrastructure/`;
3. Input ports são interfaces em `domain/port/in/` — os serviços de domínio as implementam;
4. Trocar o banco (ex.: H2 → Oracle) exige mudar **apenas** o adapter — o domínio permanece intacto.

---

## 🧩 Modelagem do Domínio

### Entidades (todas herdam de `DomainEntity`)

| Classe Java | Tabela | Descrição |
|---|---|---|
| `Organization` | `Organizacao` | Cliente — operador de satélites, agência espacial |
| `SpaceAsset` | `AtivoEspacial` | Ativo de uma organização — satélite, estação, base lunar |
| `SpaceEvent` | `EventoEspacial` | Evento solar — CME, SEP, Solar Flare |
| `RiskAnalysis` | `AnaliseRisco` | Avaliação de risco de um evento sobre um ativo |
| `Alert` | `Alerta` | Alerta gerado a partir de uma análise de risco |
| `ActionHistory` | `HistoricoAcao` | Ações tomadas em resposta a um alerta |

### Relacionamentos

```
Organization 1──N SpaceAsset
SpaceEvent   1──N RiskAnalysis     N──1 SpaceAsset
RiskAnalysis 1──N Alert            1──N ActionHistory
```

### Enums (estados de domínio, nunca Strings cruas)

| Enum | Valores |
|---|---|
| `EventType` | `CME`, `SEP`, `SOLAR_FLARE` |
| `RiskLevel` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `AlertStatus` | `PENDING`, `SENT`, `ACKNOWLEDGED`, `RESOLVED` |
| `AssetType` | `SATELLITE`, `SPACE_STATION`, `SPACECRAFT`, `LUNAR_BASE` |

### Regra de Negócio (RN) — Alerta Crítico

Um alerta é classificado como **`CRITICAL`** quando a intensidade do evento (velocidade de plasma de um CME **ou** densidade de fluxo de prótons de um SEP) ultrapassa o **limiar de tolerância definido para o tipo de órbita** do ativo (LEO, GEO ou Lunar). Órbitas mais sensíveis (ex.: base lunar) possuem limiares menores.

---

## 📂 Estrutura de Pastas

```
src/main/java/br/com/spacereport/
├── SpaceReportApplication.java          # Entrypoint Spring Boot
├── domain/
│   ├── exception/
│   │   └── DonkiApiException.java
│   ├── model/                           # Entidades + DomainEntity + Enums
│   │   ├── DomainEntity.java            # Classe base abstrata (Herança)
│   │   ├── Organization.java  SpaceAsset.java  SpaceEvent.java
│   │   ├── RiskAnalysis.java  Alert.java  ActionHistory.java
│   │   └── EventType.java  RiskLevel.java  AlertStatus.java  AssetType.java
│   ├── port/
│   │   ├── in/                          # Casos de uso (interfaces)
│   │   │   ├── IngestSpaceEventUseCase.java   AnalyzeRiskUseCase.java
│   │   │   └── GenerateAlertUseCase.java      QueryAlertUseCase.java
│   │   └── out/                         # Contratos de saída (interfaces)
│   │       ├── OrganizationRepository.java    SpaceAssetRepository.java
│   │       ├── SpaceEventRepository.java       RiskAnalysisRepository.java
│   │       └── AlertRepository.java            AlertNotifier.java
│   └── service/                         # Serviços de domínio (zero infra)
│       ├── SpaceEventService.java   RiskAnalysisService.java
│       └── AlertService.java        SpaceEventBST.java
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   └── SpaceReportRunner.java   # Orquestra o fluxo end-to-end
    │   └── out/
    │       ├── NasaDonkiClient.java     # Cliente HTTP da NASA DONKI
    │       ├── WebhookNotifier.java     # Disparo de webhook JSON
    │       ├── *RepositoryAdapter.java  # 5 adapters JPA (implementam os ports)
    │       └── persistence/
    │           ├── entity/              # Entidades JPA (@Entity)
    │           └── repository/          # Spring Data JPA repositories
    └── config/
        └── AppConfig.java               # Wiring dos serviços de domínio
```

---

## 🛠️ Tecnologias

| Categoria | Ferramenta |
|---|---|
| Linguagem | Java 17 |
| Build | Maven |
| Framework | Spring Boot 3.2.5 |
| Persistência | H2 (in-memory) + Spring Data JPA / Hibernate |
| Cliente HTTP | `java.net.http.HttpClient` (nativo) |
| JSON | Jackson (`ObjectMapper`) |
| Testes | JUnit 5 + Mockito |
| Logging | SLF4J + Logback |

---

## ▶️ Como Executar

### Pré-requisitos

- **Java 17+**
- **Maven** (`brew install maven` no macOS)

### Comandos

```bash
# Compilar
mvn compile

# Rodar os testes unitários
mvn test

# Executar a aplicação (fluxo completo end-to-end)
mvn spring-boot:run
```

Ao iniciar, a aplicação:
1. Popula organizações e ativos de demonstração (seed);
2. Busca eventos CME + SEP da NASA DONKI (últimos 7 dias);
3. Calcula risco, indexa em BST e gera alertas;
4. Demonstra operações de **CRUD** (update + delete);
5. Mantém o servidor ativo para inspeção do banco via H2 Console.

> ℹ️ A `DEMO_KEY` da NASA tem limite de 30 requisições/hora. Se o limite for atingido (HTTP 429), a aplicação registra um aviso e continua sem travar.

---

## 🗄️ Evidência de Persistência — H2 Console

Com a aplicação rodando (`mvn spring-boot:run`), acesse no navegador:

```
http://localhost:8080/h2-console
```

Preencha os campos de conexão:

| Campo | Valor |
|---|---|
| **JDBC URL** | `jdbc:h2:mem:spacereportdb` |
| **User Name** | `sa` |
| **Password** | *(vazio)* |

Após conectar, é possível visualizar e consultar as tabelas (`ORGANIZACAO`, `ATIVO_ESPACIAL`, `EVENTO_ESPACIAL`, `ANALISE_RISCO`, `ALERTA`) com os dados persistidos — comprovando o requisito de persistência e CRUD.

---

## 🔄 Fluxo da Aplicação

```
Início
  └─► SpaceReportRunner popula ativos de demonstração
       └─► NasaDonkiClient busca eventos (CME + SEP) da NASA
            └─► Para cada evento:  SpaceEventService.ingest()
                 ├─► salva o evento
                 ├─► RiskAnalysisService.analyze()  → calcula score e nível de risco
                 └─► AlertService.generate()        → gera alerta (ignora LOW)
                      └─► WebhookNotifier dispara JSON ao endpoint do cliente
       └─► SpaceEventBST ordena/filtra eventos por intensidade  (O(log n))
       └─► Demonstração de CRUD (update + delete)
       └─► Impressão de todos os alertas e dos alertas CRÍTICOS
```

---

## 🎯 Conceitos de DDD Aplicados

| Conceito | Onde |
|---|---|
| **Linguagem Ubíqua** | Nomenclatura do domínio (`SpaceEvent`, `RiskAnalysis`, `Alert`) reflete o negócio |
| **Entidades** | 6 entidades em `domain/model/` |
| **Serviços de Domínio** | `SpaceEventService`, `RiskAnalysisService`, `AlertService` |
| **Repositórios** | 5 interfaces em `domain/port/out/` + adapters JPA |
| **Casos de Uso** | 4 interfaces em `domain/port/in/` |
| **Separação de Camadas** | Domínio isolado de infraestrutura (Arquitetura Hexagonal) |

## 🧱 Conceitos de POO Aplicados

| Pilar | Onde |
|---|---|
| **Abstração** | Interfaces de ports e casos de uso |
| **Encapsulamento** | Campos `private final`, injeção via construtor |
| **Herança** | As 6 entidades estendem `DomainEntity` (campo `id` comum) |
| **Polimorfismo** | Serviços implementam interfaces de caso de uso; adapters implementam ports |

---

## 🧪 Testes

```bash
mvn test
```

**16 testes** (JUnit 5 + Mockito), com foco na regra de negócio de alertas críticos:

- `RiskAnalysisServiceTest` — cálculo de risco e limiares por órbita (6 testes)
- `AlertServiceTest` — geração e consulta de alertas (5 testes)
- `SpaceEventBSTTest` — indexação e busca em BST (5 testes)

Os serviços de domínio são testados com **mocks dos output ports** — sem necessidade de banco de dados.

---

## ✅ Cobertura de Requisitos

| Requisito | Status |
|---|---|
| Conceitos de DDD (Linguagem Ubíqua, Entidades, Serviços, Repositórios, Casos de Uso, Camadas) | ✅ |
| POO (Abstração, Encapsulamento, Herança, Polimorfismo) | ✅ |
| Arquitetura Hexagonal (Domínio, Ports, Adapters) | ✅ |
| Persistência com H2 | ✅ |
| Operações CRUD completas | ✅ |
| Testes unitários *(diferencial)* | ✅ |
| Interface gráfica — H2 Console *(diferencial)* | ✅ |
| Clean Code *(diferencial)* | ✅ |

---

## 🌍 Objetivos de Desenvolvimento Sustentável (ODS)

- **ODS 9 — Indústria, Inovação e Infraestrutura:** protege a infraestrutura de comunicação global e os sistemas de geolocalização (GPS) contra eventos solares severos.
- **ODS 11 — Cidades e Comunidades Sustentáveis:** salvaguarda sistemas de distribuição de energia elétrica vulneráveis a correntes geomagneticamente induzidas.

---

## 📡 API NASA DONKI

- Base URL: `https://api.nasa.gov/DONKI`
- CME: `GET /CME?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&api_key=DEMO_KEY`
- SEP: `GET /SEP?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&api_key=DEMO_KEY`

> Para uso além de testes, substitua `DEMO_KEY` por uma chave real obtida em [api.nasa.gov](https://api.nasa.gov).
