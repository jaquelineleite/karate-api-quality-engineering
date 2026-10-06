Feature: Obter token de autenticação

  Scenario: Gerar token para operações protegidas
    Given url baseUrl
    And path 'auth'
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
    * def token = response.token
