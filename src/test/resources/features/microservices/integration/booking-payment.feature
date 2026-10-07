@microservices @integration
Feature: Integração entre Booking Service e Payment Service

  Background:
    * url bookingServiceUrl
    * def bookingSchema = read('classpath:schemas/microservices/booking-response.json')
    * def correlationId = 'karate-' + java.util.UUID.randomUUID()
    * def requestPayload =
    """
    {
      "customerName": "Karate Integration",
      "totalPrice": 850.00
    }
    """

  Scenario: Confirmar reserva após pagamento aprovado

    Given path 'bookings'
    And header Content-Type = 'application/json'
    And header X-Correlation-ID = correlationId
    And request requestPayload
    When method post
    Then status 201
    And match response == bookingSchema

    And match header X-Correlation-ID == correlationId
    And match response.id == '#number'
    And match response.customerName == 'Karate Integration'
    And match response.totalPrice == 850.00
    And match response.status == 'CONFIRMED'
    And match response.correlationId == correlationId

    * def bookingId = response.id

    Given path 'bookings', bookingId
    And header X-Correlation-ID = correlationId
    When method get
    Then status 200
    And match response == bookingSchema

    And match header X-Correlation-ID == correlationId
    And match response.id == bookingId
    And match response.status == 'CONFIRMED'
    And match response.correlationId == correlationId
    And match response.createdAt == '#string'
