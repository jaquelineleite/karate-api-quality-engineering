@negative
Feature: Cenários negativos da API de reservas

  Background:
    * url baseUrl

  Scenario: Consultar uma reserva inexistente
    Given path 'booking', 999999999
    When method get
    Then status 404

  Scenario: Atualizar reserva sem autenticação
    Given path 'booking', 1
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
      """
      {
        "firstname": "Usuario",
        "lastname": "Sem Autorizacao",
        "totalprice": 100,
        "depositpaid": false,
        "bookingdates": {
          "checkin": "2026-12-01",
          "checkout": "2026-12-02"
        },
        "additionalneeds": "None"
      }
      """
    When method put
    Then status 403

  Scenario: Excluir reserva sem autenticação
    Given path 'booking', 1
    When method delete
    Then status 403
