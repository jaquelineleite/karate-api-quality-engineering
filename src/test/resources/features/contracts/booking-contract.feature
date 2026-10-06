@contract
Feature: Validação de contrato da API de reservas

  Background:
    * url baseUrl
    * def bookingSchema = read('classpath:schemas/booking-schema.json')

  Scenario: Validar contrato de uma reserva criada

    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
      """
      {
        "firstname": "Jaqueline",
        "lastname": "QA",
        "totalprice": 950,
        "depositpaid": true,
        "bookingdates": {
          "checkin": "2026-12-01",
          "checkout": "2026-12-05"
        },
        "additionalneeds": "Contract Testing"
      }
      """
    When method post
    Then status 200
    And match response.bookingid == '#number'
    And match response.booking == bookingSchema

    * def bookingId = response.bookingid

    Given path 'booking', bookingId
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response == bookingSchema
