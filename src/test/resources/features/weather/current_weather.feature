@weather @current
Feature: Текущая погода — позитивный сценарий

  Сценарий проверяет, что клиент корректно собирает запрос к /v1/current.json,
  десериализует ответ в типизированные DTO (Location/Current/Condition)
  и читает температуру, влажность и текстовое описание погоды.
  Все обращения идут к WireMock, внешний API не вызывается.

  Scenario: Получение текущей погоды для четырёх городов и сравнение значений
    Given mock-сервис погоды содержит корректные ответы для городов
      | city   | tempC | humidity | condition |
      | Moscow | 17.5  | 66       | Clear     |
      | Berlin | 21.1  | 58       | Sunny     |
      | Paris  | 19.3  | 71       | Overcast  |
      | Tokyo  | 24.0  | 74       | Mist      |
    When я запрашиваю текущую погоду для городов
      | Moscow |
      | Berlin |
      | Paris  |
      | Tokyo  |
    Then значения ответа совпадают с ожидаемыми
      | city   | tempC | humidity | condition |
      | Moscow | 17.5  | 66       | Clear     |
      | Berlin | 21.1  | 58       | Sunny     |
      | Paris  | 19.3  | 71       | Overcast  |
      | Tokyo  | 24.0  | 74       | Mist      |

  Scenario: Расхождения значений фиксируются в логе
    Given mock-сервис погоды содержит корректные ответы для городов
      | city   | tempC | humidity | condition |
      | Moscow | 17.5  | 66       | Clear     |
    When я запрашиваю текущую погоду для городов
      | Moscow |
    Then расхождения с ожидаемыми значениями зафиксированы в логе
      | city   | tempC | humidity | condition |
      | Moscow | 20.0  | 70       | Cloudy    |
