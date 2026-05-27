@weather @current
Feature: Current weather endpoint - happy path

  Сценарий проверяет, что клиент корректно собирает запрос к /v1/current.json,
  десериализует ответ в типизированные DTO (Location/Current/Condition)
  и читает температуру, влажность и текстовое описание погоды.
  Все обращения идут к WireMock, внешний API не вызывается.

  Scenario: Get current weather for four cities and compare values
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
