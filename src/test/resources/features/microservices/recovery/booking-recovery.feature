@microservices @recovery
Feature: Recuperacao de pagamentos com resultado desconhecido

  Background:
    * url bookingServiceUrl

  Scenario: Reserva inexistente nao pode ser recuperada
    Given path 'bookings', -999999, 'retry-payment'
    When method post
    Then status 404

  Scenario: Reserva confirmada nao pode ser processada novamente
    * def correlationId = 'recovery-' + java.util.UUID.randomUUID()

    Given path 'bookings'
    And header X-Correlation-ID = correlationId
    And request { customerName: 'Teste Recuperacao', totalPrice: 150.00 }
    When method post
    Then status 201
    And match response.status == 'CONFIRMED'
    * def bookingId = response.id

    Given path 'bookings', bookingId, 'retry-payment'
    When method post
    Then status 409

    Given path 'bookings', bookingId
    When method get
    Then status 200
    And match response.status == 'CONFIRMED'
