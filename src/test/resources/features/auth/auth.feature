@auth
Feature: Autenticação na API Restful Booker

  Background:
    * url baseUrl

  @smoke
  Scenario: Gerar token com credenciais válidas
    Given path 'auth'
    And request
      """
      {
        "username": "admin",
        "password": "password123"
      }
      """
    When method post
    Then status 200
    And match response.token == '#string'
    And match response.token != ''
