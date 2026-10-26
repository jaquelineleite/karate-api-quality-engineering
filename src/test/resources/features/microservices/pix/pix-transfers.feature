@microservices @pix
Feature: Testes funcionais de transferencias PIX simuladas

  Background:
    * url 'http://localhost:8083'
    * def uuid = function(){ return java.util.UUID.randomUUID() + '' }
    * def validRequest = { payerAccount: '12345-6', receiverPixKey: 'recebedor@exemplo.com', amount: 150.00, description: 'Teste PIX Karate' }

  Scenario: PIX-001 - Criar transferencia valida
    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And header X-Correlation-ID = uuid()
    And request validRequest
    When method post
    Then status 201
    And match response.transactionId == '#uuid'
    And match response.status == 'PENDING'
    And match response.amount == 150

  Scenario: PIX-002 - Consultar transferencia criada
    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request validRequest
    When method post
    Then status 201
    * def transactionId = response.transactionId

    Given path 'pix', 'transfers', transactionId
    When method get
    Then status 200
    And match response.transactionId == transactionId
    And match response.status == 'PENDING'

  Scenario: PIX-003 - Rejeitar valor zero
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.amount = 0

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

  Scenario: PIX-004 - Rejeitar valor negativo
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.amount = -10

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

  Scenario: PIX-005 - Rejeitar conta pagadora vazia
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.payerAccount = ''

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

  Scenario: PIX-006 - Rejeitar chave PIX vazia
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.receiverPixKey = ''

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

  Scenario: PIX-007 - Preservar transacao em retentativa idempotente
    * def key = uuid()

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And header X-Correlation-ID = 'original'
    And request validRequest
    When method post
    Then status 201
    * def originalId = response.transactionId

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And header X-Correlation-ID = 'retentativa'
    And request validRequest
    When method post
    Then status 201
    And match response.transactionId == originalId
    And match response.correlationId == 'original'

  Scenario: PIX-008 - Rejeitar reutilizacao de chave com dados diferentes
    * def key = uuid()

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request validRequest
    When method post
    Then status 201

    * def changedRequest = JSON.parse(JSON.stringify(validRequest))
    * set changedRequest.amount = 250

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request changedRequest
    When method post
    Then status 409

  Scenario: PIX-009 - Rejeitar transferencia sem chave de idempotencia
    Given path 'pix', 'transfers'
    And request validRequest
    When method post
    Then status 400

  Scenario: PIX-010 - Rejeitar valor com mais de duas casas decimais
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.amount = 150.999

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

  Scenario: PIX-011 - Consultar transferencia inexistente
    Given path 'pix', 'transfers', '00000000-0000-0000-0000-000000000000'
    When method get
    Then status 404
    And match response.status == 404
    And match response.error == 'Not Found'

  Scenario: PIX-012 - Rejeitar transferencia sem valor informado
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * remove payload.amount

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400
    And match response.status == 400
    And match response.error == 'Bad Request'

  Scenario: PIX-013 - Rejeitar mesma chave de idempotencia com destinatario diferente
    * def key = uuid()

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request validRequest
    When method post
    Then status 201
    * def originalId = response.transactionId
    * def originalReceiver = response.receiverPixKey

    * def changedRequest = JSON.parse(JSON.stringify(validRequest))
    * set changedRequest.receiverPixKey = 'outro@exemplo.com'

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request changedRequest
    When method post
    Then status 409
    And match response.status == 409
    And match response.error == 'Conflict'

    Given path 'pix', 'transfers', originalId
    When method get
    Then status 200
    And match response.transactionId == originalId
    And match response.receiverPixKey == originalReceiver
    And match response.status == 'PENDING'


  Scenario Outline: PIX-017 - Rejeitar campos contendo apenas espacos
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.<campo> = '   '

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

    Examples:
      | campo          |
      | payerAccount   |
      | receiverPixKey |


  Scenario Outline: PIX-018 - Rejeitar campos obrigatorios ausentes
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * remove payload.<campo>

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = uuid()
    And request payload
    When method post
    Then status 400

    Examples:
      | campo          |
      | payerAccount   |
      | receiverPixKey |


  Scenario: PIX-019 - Rejeitar retentativa com descricao diferente
    * def key = uuid()

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request validRequest
    When method post
    Then status 201
    * def originalId = response.transactionId

    * def changedRequest = JSON.parse(JSON.stringify(validRequest))
    * set changedRequest.description = 'Descricao alterada'

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request changedRequest
    When method post
    Then status 409

    Given path 'pix', 'transfers'
    And path originalId
    When method get
    Then status 200
    And match response.transactionId == originalId
    And match response.status == 'PENDING'

    * def dbHelper = Java.type('support.PixDatabaseHelper')
    * def persisted = dbHelper.findByIdempotencyKey(key)
    And match persisted.count == 1
    And match persisted.transactionId == originalId
    And match persisted.description == 'Teste PIX Karate'
    And match persisted.status == 'PENDING'


  Scenario: PIX-020 - Criar transferencia sem descricao
    * def key = uuid()
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * remove payload.description

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request payload
    When method post
    Then status 201
    And match response.transactionId == '#uuid'
    And match response.status == 'PENDING'

    * def dbHelper = Java.type('support.PixDatabaseHelper')
    * def persisted = dbHelper.findByIdempotencyKey(key)
    And match persisted.count == 1
    And match persisted.transactionId == response.transactionId
    And match persisted.description == null
    And match persisted.status == 'PENDING'


  Scenario: PIX-021 - Criar transferencia com descricao nula
    * def key = uuid()
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.description = null

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request payload
    When method post
    Then status 201
    And match response.transactionId == '#uuid'
    And match response.status == 'PENDING'
    * def originalId = response.transactionId

    * def dbHelper = Java.type('support.PixDatabaseHelper')
    * def persisted = dbHelper.findByIdempotencyKey(key)
    And match persisted.count == 1
    And match persisted.transactionId == originalId
    And match persisted.description == null
    And match persisted.status == 'PENDING'


  Scenario: PIX-022 - Criar transferencia com descricao vazia
    * def key = uuid()
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.description = ''

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request payload
    When method post
    Then status 201
    And match response.transactionId == '#uuid'
    And match response.status == 'PENDING'
    * def originalId = response.transactionId

    * def dbHelper = Java.type('support.PixDatabaseHelper')
    * def persisted = dbHelper.findByIdempotencyKey(key)
    And match persisted.count == 1
    And match persisted.transactionId == originalId
    And match persisted.description == ''
    And match persisted.status == 'PENDING'


  Scenario: PIX-023 - Aceitar valor minimo positivo de 0.01
    * def key = uuid()
    * def payload = JSON.parse(JSON.stringify(validRequest))
    * set payload.amount = 0.01

    Given path 'pix', 'transfers'
    And header X-Idempotency-Key = key
    And request payload
    When method post
    Then status 201
    And match response.transactionId == '#uuid'
    And match response.status == 'PENDING'
    * def originalId = response.transactionId

    * def dbHelper = Java.type('support.PixDatabaseHelper')
    * def persisted = dbHelper.findByIdempotencyKey(key)
    And match persisted.count == 1
    And match persisted.transactionId == originalId
    And match persisted.amount.toString() == '0.01'
    And match persisted.status == 'PENDING'
