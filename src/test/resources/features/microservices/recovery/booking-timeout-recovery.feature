@microservices @recovery @timeout
Feature: Recuperacao de pagamento apos timeout

  Background:
    * url bookingServiceUrl

  Scenario: Pagamento aprovado com resposta atrasada pode ser recuperado
    * def correlationId = 'timeout-' + java.util.UUID.randomUUID()

    # Criar reserva com atraso de 6 segundos no pagamento
    Given path 'bookings'
    And header X-Correlation-ID = correlationId
    And header X-Test-Delay-Ms = 6000
    And request { customerName: 'Teste Timeout Karate', totalPrice: 150.00 }
    When method post
    Then status 201
    And match response.status == 'PAYMENT_UNKNOWN'
    * def bookingId = response.id

    # Recuperar pagamento usando a mesma chave de idempotencia
    Given path 'bookings', bookingId, 'retry-payment'
    When method post
    Then status 200
    And match response.status == 'CONFIRMED'

    # Impedir nova retentativa em reserva confirmada
    Given path 'bookings', bookingId, 'retry-payment'
    When method post
    Then status 409

    # Confirmar persistencia do estado final
    Given path 'bookings', bookingId
    When method get
    Then status 200
    And match response.status == 'CONFIRMED'
