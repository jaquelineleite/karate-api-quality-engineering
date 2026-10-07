## Objetivo

A arquitetura separa responsabilidades entre testes funcionais, contratos, cenários negativos e testes não funcionais, priorizando independência, reutilização e feedback rápido no CI/CD.

## Visão arquitetural

```text
                     GitHub Actions
                           |
                     Quality Gates
              _____________|_____________
             |             |             |
      Regression Gate   Full API     Performance
             |             |             |
    RegressionRunner     Karate           k6
             |             |             |
             +-------------+-------------+
                           |
                  Restful Booker API

      Karate
Responsável por:
- testes funcionais de API;
- autenticação;
- fluxo CRUD;
- contratos;
- cenários negativos;
- testes data-driven;
- seleção por tags;
- geração de relatórios.
k6
Responsável pelos testes não funcionais:
- performance smoke;
- latência;
- taxa de erros;
- thresholds;
- carga controlada.
Karate não é utilizado como ferramenta de carga. O k6 mantém a responsabilidade de performance separada da automação funcional.
Organização
src/test
├── java/runners
│   ├── runners específicos
│   ├── SmokeRunner
│   └── RegressionRunner
│
└── resources
    ├── data
    │   └── booking-payload.json
    ├── features
    │   ├── auth
    │   ├── booking
    │   ├── common
    │   ├── contracts
    │   ├── datadriven
    │   ├── health
    │   └── negative
    ├── schemas
    │   └── booking-schema.json
    └── karate-config.js

performance/k6
├── smoke.js
└── load.js

Reutilização
O projeto reutiliza componentes quando existe ganho real de manutenção:
- autenticação por fluxo comum;
- payload base de reserva;
- schemas de contrato;
- configurações centralizadas.
A estratégia evita criar camadas ou abstrações sem necessidade apenas para aumentar a complexidade do framework.
Independência dos testes
Sempre que possível, os cenários criam e controlam seus próprios dados.
CREATE
  |
bookingId
  |
validação

Isso reduz dependência de IDs fixos, ordem de execução e estado previamente existente no ambiente.
Fluxo CRUD
O fluxo CRUD representa uma jornada integrada intencional:
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

O identificador da reserva é obtido dinamicamente durante a execução.
Configuração e autenticação
O karate-config.js centraliza configurações da execução.
A URL base e as credenciais podem ser obtidas por propriedades ou variáveis de ambiente, permitindo alterar o contexto de execução sem modificar os cenários.
O token é obtido por um fluxo reutilizável para operações protegidas.
Estratégia de execução
As tags permitem selecionar diferentes conjuntos de testes, como @smoke, @regression, @contract, @negative e @datadriven.
O RegressionRunner executa os cenários classificados com @regression.
No CI/CD:
- Pull Request executa o Karate Regression Gate;
- push na main executa regressão, suíte completa e k6 Performance Smoke;
- relatórios Karate são publicados como artefatos para investigação.
Banco de dados
A Restful Booker não disponibiliza acesso à camada de persistência.
Adicionar um banco local sem relação com o sistema testado produziria uma validação artificial. Por isso, o projeto valida somente interfaces efetivamente disponibilizadas pelo sistema.
Performance
Testes agressivos de stress, spike ou endurance não são executados contra infraestrutura pública de terceiros.
Esses cenários devem ser executados apenas em ambientes autorizados e controlados.
Decisões arquiteturais
- separar testes funcionais e não funcionais;
- reduzir dependência de dados externos;
- capturar identificadores dinamicamente;
- reutilizar componentes sem abstração excessiva;
- utilizar tags para diferentes níveis de execução;
- diferenciar feedback de Pull Request e main;
- preservar evidências para investigação de falhas.