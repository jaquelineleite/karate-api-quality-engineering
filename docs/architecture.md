# Arquitetura de Quality Engineering

## Objetivo

Documentar a arquitetura real do laboratório de testes de API, suas responsabilidades, decisões técnicas e mecanismos de validação.

O projeto possui dois contextos distintos:

1. Testes funcionais contra a API pública Restful Booker.
2. Testes de integração, paralelismo, performance e observabilidade em microserviços locais controlados.

## Componentes

### Automação funcional — Karate

O Karate é utilizado para:

- testes funcionais de API;
- autenticação e operações protegidas;
- fluxo CRUD de reservas;
- contratos JSON;
- cenários negativos;
- testes orientados a dados;
- integração entre microserviços;
- execução paralela de cenários.

Os testes são organizados em features, schemas e runners JUnit.

### Performance — k6

O k6 é responsável por executar cenários não funcionais com thresholds de desempenho.

O paralelismo funcional do Karate não substitui testes de carga, stress ou capacidade.

## Ambiente público — Restful Booker

Os testes funcionais validam as interfaces disponibilizadas pela Restful Booker.

Os cenários utilizam identificadores obtidos dinamicamente, evitando dependência de registros fixos.

Como não há acesso à persistência interna dessa API pública, não são realizadas validações diretas em seu banco de dados.

## Ambiente local — microserviços

A infraestrutura é definida em `infrastructure/docker-compose.yml`.

Componentes:

| Componente | Porta local | Responsabilidade |
|---|---|---|
| Booking Service | 8081 | Operações de reservas |
| Payment Service | 8082 | Serviço de pagamentos |
| PostgreSQL 17 | 5435 | Persistência das reservas |
| Prometheus | 9090 | Coleta de métricas |
| Grafana | 3000 | Visualização de métricas |

O Booking Service recebe a URL do Payment Service e os parâmetros de conexão com PostgreSQL por variáveis de ambiente.

O Docker Compose configura uma verificação de saúde para PostgreSQL. O Booking Service aguarda a saúde do banco, mas utiliza apenas a condição de inicialização para o Payment Service.

Essa diferença deve ser considerada na investigação de problemas de inicialização e disponibilidade.

## Integração entre Booking Service e Payment Service

A feature de integração está localizada em:

`src/test/resources/features/microservices/integration/booking-payment.feature`

Ela valida a criação e a consulta de reservas, incluindo:

- HTTP 201 na criação;
- HTTP 200 na consulta;
- contrato JSON da resposta;
- identificador da reserva;
- status `CONFIRMED`;
- propagação do `X-Correlation-ID`.

O identificador retornado pela criação é reutilizado na consulta da mesma reserva.

## Paralelismo e isolamento de dados

A execução paralela utiliza:

`src/test/java/runners/MicroservicesParallelRunner.java`

O runner seleciona os cenários com a tag `@microservices` e configura três threads.

A feature contém três exemplos com clientes e valores diferentes.

Cada cenário gera um `X-Correlation-ID` exclusivo e utiliza o identificador da reserva retornado pela API.

Essa estratégia reduz interferências decorrentes do compartilhamento de dados entre cenários.

A execução com três threads foi verificada localmente por meio da timeline do Karate. Isso demonstra concorrência entre os cenários avaliados, mas não representa um teste de alta carga.

## Runners de integração

- `MicroservicesRunner`: execução funcional dos cenários de integração.
- `MicroservicesParallelRunner`: execução paralela dos cenários de integração.

Ambos são executados no job de integração do GitHub Actions.

## Observabilidade

Booking Service e Payment Service expõem métricas utilizando Spring Boot Actuator e Micrometer.

O Prometheus coleta essas métricas e o Grafana apresenta os indicadores.

O laboratório contempla métricas HTTP, histogramas de latência e painéis de percentis P95/P99.

Os resultados e limites dos testes não funcionais são detalhados em `docs/performance-observability.md`.

## Quality Gates — GitHub Actions

O workflow está localizado em:

`.github/workflows/quality-gate.yml`

São executados quatro jobs:

1. Karate Regression Gate.
2. Karate Full API Suite.
3. k6 Performance Smoke.
4. Microservices Integration Gate.

O job de microserviços executa:

`mvn test -Dtest=MicroservicesRunner,MicroservicesParallelRunner`

Os relatórios são disponibilizados como artefatos para investigação.

### Evidência de execução

Commit: `57ccb6e`

GitHub Actions:

https://github.com/jaquelineleite/karate-api-quality-engineering/actions/runs/37800573140

Resultado observado:

- quatro jobs aprovados;
- ambos os runners de integração executados;
- quatro testes reportados pelo Maven;
- zero falhas, zero erros e zero testes ignorados.

O runner paralelo aparece como um teste JUnit, mas valida internamente três cenários Karate.

## Decisões arquiteturais

- Separar testes funcionais de testes de performance.
- Manter ambientes públicos e locais conceitualmente distintos.
- Utilizar identificadores dinâmicos e dados independentes.
- Centralizar configurações sem criar abstrações desnecessárias.
- Utilizar contratos reutilizáveis.
- Preservar evidências no CI/CD.
- Investigar causas de falhas antes de adicionar retries.
- Utilizar paralelismo funcional para avaliar independência de cenários, não para substituir testes de carga.

## Limitações

A validação paralela atual utiliza três cenários e três threads.

Os resultados de performance obtidos em ambiente local não representam capacidade de produção.

A aprovação do pipeline demonstra sucesso das verificações configuradas, mas não garante ausência de defeitos ou cobertura integral do sistema.
