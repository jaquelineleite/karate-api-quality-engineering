@negative @regression
Feature: Cenários negativos da API de reservas

  Background:
    * url baseUrl
    * def bookingPayload =
      """
      {
        "firstname": "Usuario",
        "lastname": "Teste Negativo",
        "totalprice": 100,
        "depositpaid": false,
        "bookingdates": {
          "checkin": "2026-12-01",
          "checkout": "2026-12-02"
        },
        "additionalneeds": "None"
      }
      """

  Scenario: Consultar uma reserva inexistente
    Given path 'booking', 999999999
    When method get
    Then status 404

  Scenario: Impedir atualização de reserva sem autenticação
    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request bookingPayload
    When method post
    Then status 200
    And match response.bookingid == '#number'
    * def bookingId = response.bookingid

    Given path 'booking', bookingId
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request bookingPayload
    When method put
    Then status 403

  Scenario: Impedir exclusão de reserva sem autenticação
    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request bookingPayload
    When method post
    Then status 200
    And match response.bookingid == '#number'
    * def bookingId = response.bookingid

    Given path 'booking', bookingId
    When method delete
    Then status 403
