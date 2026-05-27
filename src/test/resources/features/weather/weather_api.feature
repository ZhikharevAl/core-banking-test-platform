Feature: Weather API with WireMock stubs

  Scenario: Positive - get current weather for four cities and compare values
    Given mock weather service has valid responses for cities
      | city   | tempC | humidity | condition |
      | Moscow | 17.5  | 66       | Clear     |
      | Berlin | 21.1  | 58       | Sunny     |
      | Paris  | 19.3  | 71       | Overcast  |
      | Tokyo  | 24.0  | 74       | Mist      |
    When I request current weather for cities
      | Moscow |
      | Berlin |
      | Paris  |
      | Tokyo  |
    Then weather response matches expected values
      | city   | tempC | humidity | condition |
      | Moscow | 17.5  | 66       | Clear     |
      | Berlin | 21.1  | 58       | Sunny     |
      | Paris  | 19.3  | 71       | Overcast  |
      | Tokyo  | 24.0  | 74       | Mist      |

  Scenario: Negative - get four error types and compare messages
    Given mock weather service has error responses
      | city     | status | code | message                                    |
      | bad-city | 400    | 1006 | No location found matching parameter 'q'   |
      |          | 400    | 1003 | Parameter 'q' not provided.                |
      | !!!      | 400    | 1005 | API request url is invalid.                |
      | null     | 401    | 2006 | API key provided is invalid                |
    When I request weather with invalid input cities
      | city     | code |
      | bad-city | 1006 |
      |          | 1003 |
      | !!!      | 1005 |
      | null     | 2006 |
    Then error response matches expected api errors
      | status | code | message                                    |
      | 400    | 1006 | No location found matching parameter 'q'   |
      | 400    | 1003 | Parameter 'q' not provided.                |
      | 400    | 1005 | API request url is invalid.                |
      | 401    | 2006 | API key provided is invalid                |
