# PIX-014 — Persistencia e idempotencia apos reinicializacao

## Objetivo

Validar que uma transferencia PIX simulada permanece persistida
e que a idempotencia continua funcionando apos reiniciar o pix-service.

## Pre-condicoes

- pix-service disponivel na porta 8083.
- PostgreSQL disponivel.
- Ambiente Docker Compose inicializado.

## Procedimento

1. Criar transferencia com chave de idempotencia unica.
2. Registrar transactionId e correlationId.
3. Reiniciar somente o container karate-qe-pix.
4. Aguardar o endpoint /actuator/health retornar UP.
5. Consultar a transferencia pelo transactionId.
6. Repetir o POST com a mesma chave e os mesmos dados.
7. Confirmar que transactionId e correlationId originais foram preservados.
8. Consultar o PostgreSQL e confirmar exatamente um registro.

## Resultados esperados

- Criacao inicial: HTTP 201.
- Consulta apos reinicializacao: HTTP 200.
- Retentativa idempotente: HTTP 201 conforme contrato atual.
- Mesmo transactionId.
- Mesmo correlationId original.
- Apenas um registro no PostgreSQL.

## Resultado da execucao manual

Data: 2026-10-26

Transaction ID:
87720ccb-6f43-4a0b-b31c-efdb4aa674de

Idempotency Key:
pix-restart-014

Valor:
275.50

Status:
PENDING

Resultado:
APROVADO

## Observacoes

Este teste utiliza um PIX simulado e nao realiza transferencias reais.

O teste comprova persistencia e idempotencia neste ambiente
e nesta execucao. Nao comprova tolerancia a perda do banco de dados,
falhas de rede distribuidas ou recuperacao de transacoes liquidadas.

O procedimento de reinicializacao deve ser executado em uma
suite de resiliencia isolada, nao na regressao funcional paralela.
