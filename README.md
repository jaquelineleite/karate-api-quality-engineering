# Karate API Quality Engineering

Projeto prático de Quality Engineering focado em automação de APIs REST com Karate Framework, Java, Maven, BDD, testes de contrato, testes não funcionais com k6 e Quality Gate no GitHub Actions.

O objetivo não é apenas demonstrar execução de testes automatizados, mas aplicar uma estratégia de qualidade com diferentes tipos de validação, reutilização, critérios objetivos e integração contínua.

## Stack

- Java 21
- Karate Framework
- Maven
- JUnit 5
- Gherkin / BDD
- k6
- Git
- GitHub Actions
- Restful Booker API

## Estratégia de testes

A suíte foi organizada para cobrir diferentes riscos da API:

| Tipo | Objetivo |
|---|---|
| Health Check | Verificar disponibilidade básica |
| Autenticação | Validar geração de token |
| Funcional | Validar criação e regras da reserva |
| CRUD | Validar fluxo Create, Read, Update e Delete |
| Contrato | Validar estrutura e tipos do payload |
| Negativo | Validar erros e operações não autorizadas |
| Data-Driven | Validar diferentes massas sem duplicação |
| Performance Smoke | Validar disponibilidade, erros e latência |

## Estrutura

```text
karate-api-quality-engineering/
├── .github/
│   └── workflows/
│       └── quality-gate.yml
├── performance/
│   └── k6/
│       ├── smoke.js
│       └── load.js
├── src/
│   └── test/
│       ├── java/
│       │   └── runners/
│       └── resources/
│           ├── features/
│           │   ├── auth/
│           │   ├── booking/
│           │   ├── common/
│           │   ├── contracts/
│           │   ├── datadriven/
│           │   ├── health/
│           │   └── negative/
│           ├── schemas/
│           └── karate-config.js
├── pom.xml
└── README.md
Execução
Pré-requisitos
- Java 21
- Maven
- k6
Executar testes Karate
mvn clean test
Executar apenas o smoke
mvn -Dtest=SmokeRunner test

Executar teste de contrato
mvn -Dtest=ContractTest test

Executar cenários negativos
mvn -Dtest=NegativeTest test

Performance com k6
Smoke:
k6 run performance/k6/smoke.js

Carga controlada:
k6 run performance/k6/load.js

O smoke utiliza thresholds objetivos:
http_req_failed: ['rate<0.01'],
http_req_duration: ['p(95)<1000']

Em uma execução local durante o desenvolvimento:
http_req_failed = 0.00%
p(95) = 191.82 ms
checks = 100%

Os resultados representam uma execução específica e podem variar conforme rede, ambiente e disponibilidade da API.
Testes de contrato
Os contratos são externalizados em arquivos reutilizáveis.
Exemplo:
* def bookingSchema = read('classpath:schemas/booking-schema.json')
And match response == bookingSchema

Isso permite identificar alterações estruturais ou de tipos que podem impactar consumidores da API.
Reutilização
A autenticação foi isolada em uma feature reutilizável:
* def auth = callonce read('classpath:features/common/get-token.feature')
* def token = auth.token

O token pode então ser utilizado nas operações protegidas.
CI/CD
O GitHub Actions executa um Quality Gate composto por:
Quality Gate
│
├── Karate API Tests
│
└── k6 Performance Smoke

Falhas funcionais ou violações dos thresholds de performance provocam falha no respectivo job.
Os relatórios do Karate são publicados como artifacts para auxiliar análise e investigação.
Troubleshooting real do pipeline
Durante a primeira execução no GitHub Actions, os testes Karate passaram, porém o job de performance falhou com:
Error: spawn k6 ENOENT

A análise mostrou que o problema não estava no teste nem na API. O runner não possuía o executável do k6 disponível.
A configuração do pipeline foi corrigida para instalar o k6 antes da execução.
Após a correção:
Karate API Tests       PASS
k6 Performance Smoke   PASS
Quality Gate           PASS

Esse caso demonstra a importância de diferenciar falhas da aplicação, automação e infraestrutura de execução.
Performance e uso responsável
A API utilizada é pública.
Por esse motivo, os testes executados contra o ambiente público utilizam carga reduzida.
Testes agressivos de stress, spike ou endurance devem ser executados somente em ambientes controlados e autorizados.
Princípios aplicados
- testes orientados a risco;
- reutilização sem dependência desnecessária;
- validação funcional e de contrato;
- cenários positivos e negativos;
- massa orientada a dados;
- BDD;
- thresholds objetivos;
- feedback automatizado no CI/CD;
- evidências para investigação;
- prevenção de falsos positivos;
- uso responsável de testes não funcionais.
Evoluções possíveis
- mocks para dependências externas;
- execução paralela com isolamento de massa;
- novos contratos;
- autenticação parametrizada por ambiente;
- relatórios consolidados;
- observabilidade e correlation IDs;
- execução programada de regressão.
Observação
O projeto utiliza a Restful Booker como sistema público para fins de estudo e demonstração.
Por se tratar de uma API externa, validações internas de banco de dados não foram adicionadas artificialmente ao projeto. Testes de persistência devem ser implementados quando houver acesso legítimo à camada de dados e quando fizerem sentido para a arquitetura avaliada.
