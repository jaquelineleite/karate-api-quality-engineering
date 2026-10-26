@microservices @payment @idempotency
Feature: Validar idempotência do Payment Service

  Background:
    * url paymentServiceUrl
    * def correlationId = 'karate-' + java.util.UUID.randomUUID()
    * def idempotencyKey = 'payment-' + java.util.UUID.randomUUID()
    * def payload = { bookingId: 1001, amount: 850.00 }

  Scenario: Repetir pagamento com a mesma chave não gera nova transação
    Given path 'payments'
    And header X-Correlation-ID = correlationId
    And header X-Idempotency-Key = idempotencyKey
    And request payload
    When method post
    Then status 200
    And match response.status == 'APPROVED'
    And match response.paymentId == '#string'
    * def originalPaymentId = response.paymentId

    Given path 'payments'
    And header X-Correlation-ID = correlationId
    And header X-Idempotency-Key = idempotencyKey
    And request payload
    When method post
    Then status 200
    And match response.paymentId == originalPaymentId

  Scenario: Reutilizar chave com valor diferente retorna conflito
    Given path 'payments'
    And header X-Idempotency-Key = idempotencyKey
    And request payload
    When method post
    Then status 200

    Given path 'payments'
    And header X-Idempotency-Key = idempotencyKey
    And request { bookingId: 1001, amount: 950.00 }
    When method post
    Then status 409

  Scenario: Pagamento sem chave de idempotência é rejeitado
    Given path 'payments'
    And request payload
    When method post
    Then status 400

  Scenario: Pagamento com valor negativo é rejeitado
    Given path 'payments'
    And header X-Idempotency-Key = idempotencyKey
    And request { bookingId: 1001, amount: -10 }
    When method post
    Then status 400
