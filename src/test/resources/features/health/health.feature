@smoke
Feature: Health check da API Restful Booker

  Background:
    * url baseUrl

  Scenario: Validar disponibilidade da API
    Given path 'ping'
    When method get
    Then status 201
