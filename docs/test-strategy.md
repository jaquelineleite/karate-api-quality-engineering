# Estratégia de Testes

## Objetivo

Definir uma estratégia de qualidade para validar os principais riscos funcionais e não funcionais da API utilizada no laboratório.

A abordagem prioriza feedback rápido, independência dos testes, cobertura dos fluxos críticos e geração de evidências para investigação de falhas.

## Priorização por risco

### P0 — Crítico

Fluxos cuja falha compromete as principais operações da API:

- disponibilidade da aplicação;
- autenticação;
- criação de reservas;
- operações protegidas;
- fluxo CRUD principal;
- consulta e confirmação do estado dos dados.

### P1 — Importante

Cenários que ampliam a cobertura e aumentam a confiança na solução:

- validação de contratos;
- cenários negativos;
- diferentes massas de dados;
- validações adicionais de resposta;
- comportamentos de autorização.

## Estratégia de execução

### Smoke

Conjunto reduzido de cenários críticos utilizado para fornecer feedback rápido sobre a disponibilidade e as operações essenciais da aplicação.

Os cenários são identificados pela tag `@smoke`.

### Regressão

Conjunto de cenários relevantes para validar se alterações introduziram impactos em funcionalidades já existentes.

Os cenários de regressão são identificados pela tag `@regression` e podem ser executados pelo `RegressionRunner`.

### Suíte completa

A execução completa utiliza os runners funcionais do projeto para validar todas as categorias automatizadas.

Essa execução é utilizada na branch principal como uma camada adicional de segurança antes de considerar o pipeline aprovado.

### Contrato

Valida a estrutura e os tipos dos dados retornados pela API.

Os schemas reutilizáveis são mantidos separadamente dos cenários para reduzir duplicação e facilitar manutenção.

### Cenários negativos

Validam comportamentos como:

- consulta de recurso inexistente;
- atualização sem autenticação;
- exclusão sem autenticação.

Sempre que possível, os próprios cenários criam os dados necessários antes da validação, evitando dependência de registros previamente existentes no ambiente.

### Data-driven

Cenários orientados a dados permitem validar o mesmo comportamento com diferentes massas sem duplicar a lógica do teste.

### Performance

Os testes não funcionais são executados com k6, mantendo separação entre automação funcional e avaliação de performance.

São utilizados thresholds para avaliar critérios como taxa de erro e tempo de resposta.

Testes agressivos de carga, stress, spike ou endurance não devem ser executados contra infraestrutura pública de terceiros sem autorização.

## Gerenciamento de dados de teste

Os testes devem evitar dependência de estado externo sempre que possível.

A estratégia adotada inclui:

- criação dos próprios dados necessários ao cenário;
- captura dinâmica de identificadores retornados pela API;
- reutilização controlada de payloads;
- redução de IDs fixos;
- independência entre cenários.

Essa abordagem reduz falhas causadas por massa inexistente, alterada ou compartilhada entre execuções.

## Estratégia de paralelismo e isolamento de dados

### Risco tratado

A execução concorrente pode apresentar interferência quando cenários
compartilham registros, identificadores ou estado mutável.

O objetivo é validar a independência de três cenários de integração entre
Booking Service e Payment Service.

### Implementação

A feature `features/microservices/integration/booking-payment.feature`
utiliza um `Scenario Outline` com três exemplos de clientes e valores distintos.

Cada cenário:

- gera um `X-Correlation-ID` com UUID;
- cria sua própria reserva;
- captura o identificador retornado pela API;
- consulta a reserva utilizando esse identificador;
- valida contrato, dados, correlação e status `CONFIRMED`.

O `MicroservicesParallelRunner` seleciona a tag `@microservices`,
executa com `.parallel(3)` e verifica:

- exatamente três cenários executados;
- zero falhas reportadas pelo Karate.

O `MicroservicesRunner` mantém a execução funcional independente.

### Investigação realizada

Durante a implementação, a API retornou HTTP 400 devido à construção
incorreta do payload parametrizado.

A correção utilizou uma expressão JavaScript para construir o objeto
e `Number(valor)` para garantir que o preço fosse enviado como número.

A validação posterior confirmou HTTP 201 na criação e HTTP 200 na consulta.

### Resultado esperado e obtido

| Critério | Esperado | Obtido |
|---|---|---|
| Quantidade | Três cenários Karate | Três cenários validados |
| Concorrência | Execuções sobrepostas | Confirmadas na timeline local do Karate |
| Dados | Reservas e correlações independentes | Confirmados na execução local |
| Integração | HTTP 201 e HTTP 200 | Confirmados |
| CI/CD | Ambos os runners executados | Confirmados no GitHub Actions |
| Falhas | Zero | Zero |

Na execução do CI/CD, o Maven reportou quatro testes JUnit:
um do runner paralelo e três do runner funcional.

O teste JUnit do runner paralelo executa internamente três cenários Karate.
Portanto, o total JUnit não deve ser confundido com a quantidade de cenários
executados pelo Karate.

Evidência do pipeline:

https://github.com/jaquelineleite/karate-api-quality-engineering/actions/runs/37800573140

### Limitações

A execução com três threads avalia independência funcional em concorrência
controlada. Não substitui testes de carga, stress ou capacidade.

A validação do status `CONFIRMED` não comprova isoladamente o comportamento
interno do Payment Service.

## Autenticação e configuração

Dados de configuração são centralizados no `karate-config.js`.

A URL base e as credenciais utilizadas pelo fluxo de autenticação podem ser fornecidas por propriedades ou variáveis de ambiente, permitindo execução em diferentes contextos sem alterar os cenários.

O token é obtido por um fluxo reutilizável e utilizado nas operações protegidas.

## Estratégia de tags

As tags permitem selecionar diferentes níveis de execução:

- `@smoke` — validações críticas de feedback rápido;
- `@regression` — cenários selecionados para regressão;
- `@contract` — validações de contrato;
- `@negative` — cenários negativos;
- `@datadriven` — cenários orientados a dados;
- `@auth` — autenticação;
- `@booking` — funcionalidades relacionadas a reservas.

## Quality Gate no CI/CD

O pipeline diferencia o nível de validação conforme o momento do desenvolvimento.

### Pull Request

O `Karate Regression Gate` executa os cenários classificados como regressão para fornecer feedback direcionado antes da integração das alterações.

### Branch main

Após um push na branch principal são executados:

1. `Karate Regression Gate`;
2. `Karate Full API Suite`;
3. `k6 Performance Smoke`;
4. `Microservices Integration Gate`.

O job de integração executa os runners `MicroservicesRunner` e
`MicroservicesParallelRunner`, validando os cenários funcionais e paralelos
contra os microserviços locais.

O pipeline somente deve ser considerado aprovado quando os jobs obrigatórios forem concluídos sem falhas.

Os relatórios Karate são publicados como artefatos mesmo quando ocorre falha, permitindo análise posterior.

## Critérios de qualidade

A execução deve:

- executar testes reais e evitar falso `BUILD SUCCESS` com zero testes;
- possuir zero falhas nos cenários funcionais obrigatórios;
- respeitar os thresholds definidos nos testes de performance;
- gerar evidências para investigação;
- manter os cenários independentes sempre que possível.

## Investigação de falhas

Antes de alterar um teste ou adicionar retry, a origem da falha deve ser investigada.

A falha pode estar relacionada a:

- aplicação;
- automação;
- dados;
- ambiente;
- dependência externa;
- infraestrutura de CI/CD.

A investigação deve considerar logs, resposta da API, status code, payload, dados utilizados e contexto da execução.

Retry não deve ser utilizado como primeira solução para mascarar instabilidade. Primeiro deve ser identificada a causa raiz.

## Princípios adotados

A estratégia segue os seguintes princípios:

- priorização baseada em risco;
- feedback rápido no pipeline;
- testes independentes;
- baixo acoplamento com dados externos;
- reutilização sem abstração desnecessária;
- separação entre testes funcionais e não funcionais;
- evidências para facilitar diagnóstico;
- manutenção sustentável da suíte.
