Feature: Obter token de autenticação

  Scenario: Gerar token para operações protegidas
    Given url baseUrl
    And path 'auth'
    And header Content-Type = 'application/json'
    And request
      """
      {
        "username": "#(authUsername)",
        "password": "#(authPassword)"
      }
      """
    When method post
    Then status 200
    And match response.token == '#string'
    * def token = response.token
