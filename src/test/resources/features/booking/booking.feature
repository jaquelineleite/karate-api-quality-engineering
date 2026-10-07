@booking @regression
Feature: Gerenciamento de reservas

  Background:
    * url baseUrl
    * def bookingPayload = read('classpath:data/booking-payload.json')

  @smoke
  Scenario: Criar uma nova reserva
    * set bookingPayload.firstname = 'Jaqueline'
    * set bookingPayload.lastname = 'QA'
    * set bookingPayload.totalprice = 850
    * set bookingPayload.bookingdates.checkin = '2026-11-10'
    * set bookingPayload.bookingdates.checkout = '2026-11-15'

    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request bookingPayload
    When method post
    Then status 200
    And match response.bookingid == '#number'
    And match response.booking.firstname == bookingPayload.firstname
    And match response.booking.lastname == bookingPayload.lastname
    And match response.booking.totalprice == bookingPayload.totalprice
    And match response.booking.depositpaid == bookingPayload.depositpaid
    And match response.booking.bookingdates == bookingPayload.bookingdates
