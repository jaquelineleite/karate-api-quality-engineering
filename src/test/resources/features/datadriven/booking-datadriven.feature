@datadriven
Feature: Criação de reservas orientada a dados

  Background:
    * url baseUrl

  Scenario Outline: Criar reservas com diferentes massas
    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
      """
      {
        "firstname": "<firstname>",
        "lastname": "<lastname>",
        "totalprice": <totalprice>,
        "depositpaid": <depositpaid>,
        "bookingdates": {
          "checkin": "2026-12-10",
          "checkout": "2026-12-15"
        },
        "additionalneeds": "<additionalneeds>"
      }
      """
    When method post
    Then status 200
    And match response.bookingid == '#number'
    And match response.booking.firstname == '<firstname>'
    And match response.booking.lastname == '<lastname>'
    And match response.booking.totalprice == <totalprice>
    And match response.booking.depositpaid == <depositpaid>

    Examples:
      | firstname | lastname | totalprice | depositpaid | additionalneeds |
      | Ana       | Silva    | 500        | true        | Breakfast       |
      | Bruno     | Souza    | 750        | false       | Parking         |
      | Carla     | Santos   | 1200       | true        | Late checkout   |
