@booking
Feature: Gerenciamento de reservas

  Background:
    * url baseUrl

  @smoke
  Scenario: Criar uma nova reserva
    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
      """
      {
        "firstname": "Jaqueline",
        "lastname": "QA",
        "totalprice": 850,
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
    And match response.booking.firstname == 'Jaqueline'
    And match response.booking.lastname == 'QA'
    And match response.booking.totalprice == 850
    And match response.booking.depositpaid == true
    And match response.booking.bookingdates.checkin == '2026-11-10'
    And match response.booking.bookingdates.checkout == '2026-11-15'
