@regression
Feature: Fluxo completo de gerenciamento de reservas

  Background:
    * url baseUrl
    * def auth = callonce read('classpath:features/common/get-token.feature')
    * def token = auth.token

  Scenario: Criar, consultar, atualizar e excluir uma reserva

    # CREATE
    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
      """
      {
        "firstname": "Jaqueline",
        "lastname": "QA",
        "totalprice": 900,
        "depositpaid": true,
        "bookingdates": {
          "checkin": "2026-11-10",
          "checkout": "2026-11-15"
        },
        "additionalneeds": "Quality Engineering"
      }
      """
    When method post
    Then status 200
    And match response.bookingid == '#number'
    * def bookingId = response.bookingid

    # READ
    Given path 'booking', bookingId
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response.firstname == 'Jaqueline'
    And match response.lastname == 'QA'

    # UPDATE
    Given path 'booking', bookingId
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And header Cookie = 'token=' + token
    And request
      """
      {
        "firstname": "Jaqueline",
        "lastname": "Senior QA",
        "totalprice": 1000,
        "depositpaid": true,
        "bookingdates": {
          "checkin": "2026-11-10",
          "checkout": "2026-11-16"
        },
        "additionalneeds": "API Automation"
      }
      """
    When method put
    Then status 200
    And match response.lastname == 'Senior QA'
    And match response.totalprice == 1000

    # DELETE
    Given path 'booking', bookingId
    And header Cookie = 'token=' + token
    When method delete
    Then status 201

    # Confirmação da exclusão
    Given path 'booking', bookingId
    When method get
    Then status 404
