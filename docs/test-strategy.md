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
3. `k6 Performance Smoke`.

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
