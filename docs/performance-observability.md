# Performance e Observabilidade

## Objetivo

Validar o comportamento da arquitetura de microsserviços sob carga e stress, além de fornecer observabilidade dos serviços durante a execução.

## Arquitetura observada

k6 -> Booking Service -> Payment Service
                    -> PostgreSQL

Booking Service e Payment Service expõem métricas por meio de Spring Boot Actuator e Micrometer.

As métricas são coletadas pelo Prometheus e visualizadas no Grafana.

## Teste de carga

Cenário executado com ramp-up progressivo até 50 usuários virtuais.

Resultados obtidos:

- Requisições: 13.270
- Throughput: aproximadamente 221 req/s
- Taxa de erro: 0,00%
- Checks aprovados: 39.810 de 39.810
- Latência média: 104,16 ms
- p95: 217,96 ms
- p99: 310 ms
- Tempo máximo: 965,47 ms

Quality Gates:

- taxa de erro < 1%
- p95 < 1.500 ms
- p99 < 2.500 ms

Todos os thresholds foram atendidos.

## Teste de stress

O cenário de stress aumentou progressivamente a carga até 300 usuários virtuais.

Resultados obtidos:

- Requisições: 28.102
- Throughput: aproximadamente 432 req/s
- Taxa de erro: 0,00%
- Checks aprovados: 84.306 de 84.306
- Latência média: 316,83 ms
- p95: 763,31 ms
- p99: 1,01 s
- Tempo máximo: 1,79 s

Quality Gates:

- taxa de erro < 5%
- p95 < 2.000 ms
- p99 < 3.000 ms

Todos os thresholds foram atendidos.

Os resultados representam testes controlados em ambiente local e não devem ser interpretados como capacidade de produção.

## Observabilidade

Os microsserviços utilizam:

- Spring Boot Actuator
- Micrometer
- Prometheus
- Grafana
- X-Correlation-ID

Endpoints de métricas:

- Booking Service: /actuator/prometheus
- Payment Service: /actuator/prometheus

O Prometheus realiza scraping dos dois serviços em intervalos de 5 segundos.

## Dashboard

O dashboard Quality Engineering - Microservices apresenta:

- throughput em requests/s
- latência média
- erros HTTP 5xx
- utilização de CPU
- utilização de memória JVM Heap

Durante os testes de carga foi possível correlacionar o aumento de tráfego com latência, CPU e consumo de memória dos microsserviços.

## Estratégia

O teste de carga valida comportamento esperado dentro de thresholds definidos.

O teste de stress aumenta progressivamente a concorrência para observar degradação e estabilidade.

Os testes de performance complementam os testes funcionais e de integração, permitindo avaliar não apenas se o sistema funciona, mas também como ele se comporta sob aumento de demanda.
