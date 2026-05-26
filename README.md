# TelemetryPulse Monorepo

TelemetryPulse é um monorepo com um processador Java Spring Boot e um dashboard Angular para monitoramento em tempo real de veículos elétricos.

## Arquitetura

```text
apps/
  fleet-dashboard/          Angular 17+ com dashboard reativo
  telemetry-processor/      Java 21 + Spring Boot
libs/
  shared-ui/                Componentes Angular reutilizáveis
  shared-contracts/         Tipos TypeScript e OpenAPI
docker-compose.yml          PostgreSQL, backend e frontend
```

O backend recebe leituras de telemetria, persiste o histórico, atualiza o snapshot do veículo, processa regras de alerta e publica eventos via SSE. O frontend consome `/api/stream` com `EventSource`, mantém estado com RxJS e usa componentes `OnPush`.

## Regras de Alerta

- Velocidade acima de `120 km/h`: `SPEED_LIMIT_EXCEEDED`.
- Bateria abaixo de `15%`: `BATTERY_CRITICAL`.

## Rodando com Docker

Pré-requisitos: Docker e Docker Compose.

```bash
docker compose up --build
```

Serviços:

- Dashboard: `http://localhost:4200`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Para simular telemetrias no Windows PowerShell:

```powershell
.\tools\send-telemetry.ps1 -Count 50
```

## Rodando Localmente

Pré-requisitos:

- Java 21.
- Node 18+.
- Docker apenas para PostgreSQL, caso não tenha um banco local.

Subir o banco:

```bash
docker compose up postgres
```

Backend:

```bash
cd apps/telemetry-processor
./mvnw spring-boot:run
```

Frontend:

```bash
npm install
npm run start:frontend
```

## API Principal

Ingestão:

```http
POST /api/telemetry
```

Payload:

```json
{
  "vehicleId": "EV-001",
  "batteryLevel": 12,
  "speed": 132,
  "motorTemperature": 72.5,
  "latitude": -23.5505,
  "longitude": -46.6333,
  "occurredAt": "2026-01-01T10:00:00Z"
}
```

Stream em tempo real:

```http
GET /api/stream
```

Consultas:

- `GET /api/vehicles`
- `GET /api/alerts?vehicleId=EV-001&type=BATTERY_CRITICAL`

## Banco de Dados

As migrations ficam em `apps/telemetry-processor/src/main/resources/db/migration`.

Índices criados:

- `telemetry_readings(vehicle_id, occurred_at DESC)` para histórico por veículo e período.
- `alerts(vehicle_id, created_at DESC)` para alertas por veículo.
- `alerts(type, created_at DESC)` para filtro por tipo.
- índice parcial em `alerts(resolved_at)` para alertas ativos.

## Testes

Backend:

```bash
cd apps/telemetry-processor
./mvnw test
```

Frontend:

```bash
npm run test:frontend
```

## Checklist Do Desafio

- [x] Monorepo com `apps` e `libs`.
- [x] Aplicação Angular `fleet-dashboard`.
- [x] Serviço Java Spring Boot `telemetry-processor`.
- [x] Biblioteca Angular `shared-ui`.
- [x] Contratos compartilhados em `shared-contracts`.
- [x] Ingestão de telemetria.
- [x] Regras de alerta exigidas.
- [x] Stream em tempo real com SSE.
- [x] Lista de veículos ativos.
- [x] Painel de alertas recentes.
- [x] Filtro por tipo de alerta e ID do veículo.
- [x] Modelagem e índices para consultas históricas.
- [x] Docker Compose para ambiente local.
- [x] Testes unitários backend.
