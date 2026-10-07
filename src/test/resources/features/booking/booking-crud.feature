@regression
Feature: Fluxo completo de gerenciamento de reservas

  Background:
    * url baseUrl
    * def auth = callonce read('classpath:features/common/get-token.feature')
    * def token = auth.token
    * def bookingPayload = read('classpath:data/booking-payload.json')

  Scenario: Criar, consultar, atualizar e excluir uma reserva

    # CREATE
    * set bookingPayload.firstname = 'Jaqueline'
    * set bookingPayload.lastname = 'QA'
    * set bookingPayload.totalprice = 900
    * set bookingPayload.bookingdates.checkin = '2026-11-10'
    * set bookingPayload.bookingdates.checkout = '2026-11-15'

    Given path 'booking'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request bookingPayload
    When method post
    Then status 200
    And match response.bookingid == '#number'
    And match response.booking == bookingPayload
    * def bookingId = response.bookingid

    # READ
    Given path 'booking', bookingId
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response == bookingPayload

    # UPDATE
    * def updatedBooking = read('classpath:data/booking-payload.json')
    * set updatedBooking.firstname = 'Jaqueline'
    * set updatedBooking.lastname = 'Senior QA'
    * set updatedBooking.totalprice = 1000
    * set updatedBooking.bookingdates.checkin = '2026-11-10'
    * set updatedBooking.bookingdates.checkout = '2026-11-16'
    * set updatedBooking.additionalneeds = 'API Automation'

    Given path 'booking', bookingId
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And header Cookie = 'token=' + token
    And request updatedBooking
    When method put
    Then status 200
    And match response == updatedBooking

    # READ AFTER UPDATE
    Given path 'booking', bookingId
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response == updatedBooking

    # DELETE
    Given path 'booking', bookingId
    And header Cookie = 'token=' + token
    When method delete
    Then status 201

    # CONFIRM DELETE
    Given path 'booking', bookingId
    When method get
    Then status 404
