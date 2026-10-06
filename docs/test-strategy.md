# Estratégia de Testes

## Objetivo

Validar os principais riscos funcionais e não funcionais da API utilizada no laboratório.

## Priorização

### P0

- disponibilidade;
- autenticação;
- criação de reserva;
- operações protegidas;
- fluxo CRUD principal.

### P1

- contratos;
- cenários negativos;
- diferentes massas;
- validações adicionais de regras.

## Abordagem

### Smoke

Conjunto pequeno de testes críticos para fornecer feedback rápido.

### Regressão

Cobertura mais ampla das funcionalidades automatizadas.

### Contrato

Validação da estrutura e dos tipos retornados pela API.

### Cenários negativos

Validação de recursos inexistentes e operações sem autorização.

### Performance

Uso de thresholds objetivos para avaliar taxa de erro e tempo de resposta.

## Quality Gate

A execução deve:

- executar testes reais e evitar falso BUILD SUCCESS com zero testes;
- possuir zero falhas funcionais para aprovação;
- respeitar os thresholds configurados no k6;
- gerar evidências para investigação.

## Investig
Cobertura mais ampla das funcionalidades automatizadas.

### Contrato

Validação da estrutura e dos tipos retornados pela API.

### Cenários negativos

Validação de recursos inexistentes e operações sem autorização.

### Performance

Uso de thresholds objetivos para avaliar taxa de erro e tempo de resposta.

## Quality Gate

A execução deve:

- executar testes reais e evitar falso BUILD SUCCESS com zero testes;
- possuir zero falhas funcionais para aprovação;
- respeitar os thresholds configurados no k6;
- gerar evidências para investigação.

## Investigação de falhas

Antes de corrigir uma falha, identificar sua origem:

- aplicação;
- automação;
- dados;
- ambiente;
- dependência;
- infraestrutura de CI/CD.

Retry não deve ser utilizado como primeira solução para mascarar instabilidade.
