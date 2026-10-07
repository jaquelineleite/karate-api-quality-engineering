# Karate API Quality Engineering

Projeto prático de **Quality Engineering para APIs REST**, utilizando Karate Framework, Java, Maven, JUnit 5, k6 e GitHub Actions.

O objetivo não é apenas automatizar endpoints, mas demonstrar decisões de engenharia relacionadas a **risco, independência dos testes, reutilização, contratos, dados de teste, performance e Quality Gates no CI/CD**.

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

| Tipo | Objetivo |
|---|---|
| Health Check | Verificar disponibilidade básica da API |
| Autenticação | Validar geração de token |
| Funcional | Validar criação e comportamento das reservas |
| CRUD | Validar Create, Read, Update e Delete |
| Contrato | Validar estrutura e tipos das respostas |
| Negativo | Validar recursos inexistentes e operações não autorizadas |
| Data-driven | Executar o mesmo comportamento com diferentes massas |
| Smoke | Fornecer feedback rápido sobre fluxos críticos |
| Regressão | Validar cenários relevantes antes da integração |
| Performance Smoke | Validar taxa de erro e tempo de resposta |

## Estrutura

```text
karate-api-quality-engineering/
├── .github/
│   └── workflows/
│       └── quality-gate.yml
├── docs/
│   ├── architecture.md
│   └── test-strategy.md
├── performance/
│   └── k6/
│       ├── smoke.js
│       └── load.js
├── src/
│   └── test/
│       ├── java/
│       │   └── runners/
│       │       ├── RegressionRunner.java
│       │       └── SmokeRunner.java
│       └── resources/
│           ├── data/
│           │   └── booking-payload.json
│           ├── features/
│           │   ├── auth/
│           │   ├── booking/
│           │   ├── common/
│           │   ├── contracts/
│           │   ├── datadriven/
│           │   ├── health/
│           │   └── negative/
│           ├── schemas/
│           │   └── booking-schema.json
│           └── karate-config.js
├── pom.xml
└── README.md
```

## Decisões de arquitetura

A suíte foi estruturada para evitar dependências desnecessárias entre testes.

Sempre que possível, os cenários:

- criam os próprios dados;
- capturam IDs dinamicamente;
- evitam registros previamente existentes;
- reutilizam payloads e schemas;
- compartilham autenticação de forma controlada.

Um payload base de reserva está externalizado em:

```text
src/test/resources/data/booking-payload.json
```

Os cenários alteram somente os campos necessários para seu contexto.

Isso reduz duplicação sem introduzir abstrações desnecessárias ao Karate.

## Fluxo CRUD

O cenário CRUD representa uma jornada integrada:

```text
CREATE
  ↓
READ
  ↓
UPDATE
  ↓
READ AFTER UPDATE
  ↓
DELETE
  ↓
CONFIRM 404
```

O `bookingId` é capturado dinamicamente durante a execução.

Assim, o teste não depende de um registro previamente existente no ambiente.

## Autenticação e configuração

A obtenção do token foi isolada em uma feature reutilizável:

```gherkin
* def auth = callonce read('classpath:features/common/get-token.feature')
* def token = auth.token
```

O `karate-config.js` centraliza configurações utilizadas pela suíte.

A URL base e as credenciais podem ser fornecidas por propriedades ou variáveis de ambiente, permitindo alterar o contexto de execução sem modificar os cenários.

## Testes de contrato

Os contratos são externalizados em schemas reutilizáveis.

Exemplo:

```gherkin
* def bookingSchema = read('classpath:schemas/booking-schema.json')
And match response == bookingSchema
```

Essa abordagem permite identificar alterações estruturais ou de tipos que possam impactar consumidores da API.

## Execução

### Pré-requisitos

- Java 21
- Maven
- k6

### Suíte completa

```bash
mvn clean test
```

### Smoke

```bash
mvn test -Dtest=SmokeRunner
```

### Regressão

```bash
mvn test -Dtest=RegressionRunner
```

### Contrato

```bash
mvn test -Dtest=ContractTest
```

### Cenários negativos

```bash
mvn test -Dtest=NegativeTest
```

## Performance com k6

### Smoke

```bash
k6 run performance/k6/smoke.js
```

### Carga controlada

```bash
k6 run performance/k6/load.js
```

Os testes utilizam thresholds objetivos para avaliar comportamento não funcional.

Testes agressivos de stress, spike ou endurance não são executados contra a infraestrutura pública utilizada pelo laboratório.

Esses cenários devem ser executados somente em ambientes autorizados e controlados.

## Quality Gate

O GitHub Actions executa diferentes níveis de validação conforme o contexto.

### Pull Request

```text
Pull Request
    |
Karate Regression Gate
```

O objetivo é fornecer feedback direcionado antes da integração das alterações.

### Push na main

```text
Push main
    |
    ├── Karate Regression Gate
    ├── Karate Full API Suite
    └── k6 Performance Smoke
```

Uma alteração somente é considerada aprovada quando os jobs obrigatórios são concluídos sem falhas.

Os relatórios Karate são publicados como artefatos para auxiliar investigação e diagnóstico.

## Troubleshooting real do pipeline

Durante a evolução do projeto, uma execução do GitHub Actions apresentou:

```text
Error: spawn k6 ENOENT
```

Os testes Karate haviam passado.

A investigação mostrou que a falha não estava na aplicação nem na automação funcional: o runner não possuía o executável do k6 disponível.

O pipeline foi corrigido para instalar o k6 antes da execução.

Esse caso demonstra a importância de classificar corretamente falhas entre:

- aplicação;
- automação;
- dados;
- ambiente;
- dependências;
- infraestrutura de CI/CD.

Retry não deve ser utilizado como primeira resposta para mascarar instabilidade. A causa raiz deve ser investigada.

## Banco de dados

A Restful Booker é uma API pública e não disponibiliza acesso à camada de persistência.

Adicionar um banco local sem relação com o sistema testado criaria uma validação artificial e aumentaria a complexidade sem elevar a cobertura real.

Por isso, este laboratório valida as interfaces efetivamente disponibilizadas pelo sistema.

## Princípios aplicados

- priorização baseada em risco;
- independência dos testes;
- criação e controle da própria massa;
- redução de IDs fixos;
- reutilização sem abstração excessiva;
- validações funcionais e de contrato;
- cenários positivos e negativos;
- testes data-driven;
- separação entre funcional e performance;
- Quality Gates no CI/CD;
- evidências para investigação;
- prevenção de falsos positivos;
- investigação de causa raiz;
- uso responsável de testes não funcionais.

## Documentação

A estratégia e as decisões arquiteturais estão detalhadas em:

- `docs/test-strategy.md`
- `docs/architecture.md`

## Evoluções possíveis

- mocks para dependências externas;
- paralelismo com isolamento de massa;
- ampliação dos contratos;
- relatórios consolidados;
- observabilidade e correlation IDs;
- execução programada de regressão.

## Sistema utilizado

O projeto utiliza a **Restful Booker API** como sistema público para estudo e demonstração de práticas de Quality Engineering.