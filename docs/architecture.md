# Arquitetura de Testes

## Objetivo

A arquitetura separa responsabilidades de testes funcionais, contratos, cenários negativos e testes não funcionais.

## Camadas

```text
                    CI/CD
                      |
                Quality Gate
                /           \
           Karate             k6
              |                |
        API / Contrato      Performance
              |
      Restful Booker API

Karate
Responsável por:
- testes funcionais;
- autenticação;
- CRUD;
- contratos;
- cenários negativos;
- data-driven;
- BDD.
k6
Responsável por:
- performance smoke;
- validação de latência;
- taxa de erros;
- thresholds;
- cenários controlados de carga.
CI/CD
O GitHub Actions orquestra a execução automática e transforma os testes em gates de qualidade.
Decisões arquiteturais
Separação de responsabilidades
Karate não foi utilizado como ferramenta de carga.
k6 foi escolhido especificamente para os testes não funcionais.
Reutilização
Autenticação e schemas são reutilizados para reduzir duplicação.
Independência
Sempre que possível, os cenários criam ou controlam seus próprios dados.
Isso reduz dependência de ordem e facilita execução paralela.
Testes E2E
O fluxo CRUD representa uma jornada integrada intencional.
Isso não significa que toda a suíte deva depender de testes anteriores.
Banco de dados
A API pública não fornece acesso à camada de persistência.
Adicionar um banco local sem relação com o sistema testado criaria uma validação artificial e aumentaria a complexidade sem elevar a cobertura real.
Performance
Testes agressivos não são executados contra infraestrutura pública de terceiros.
Stress, spike e endurance devem utilizar ambientes autorizados e controlados.
