@weather @errors
Feature: Текущая погода — ответы с ошибками

  Покрывает коды ошибок для /v1/current.json:
  - 400: 1003 (нет q), 1005 (битый URL), 1006 (город не найден)
  - 401: 1002 (нет API key), 2006 (неверный API key)
  - 403: 2007 (квота), 2008 (ключ отключён), 2009 (нет доступа)

  Scenario: Клиент корректно разбирает задокументированные ответы с ошибками
    Given mock-сервис погоды возвращает ответы с ошибками
      | city         | status | code | message                                       |
      | bad-city     | 400    | 1006 | No location found matching parameter 'q'      |
      | empty-q      | 400    | 1003 | Parameter 'q' not provided.                   |
      | bad-url      | 400    | 1005 | API request url is invalid.                   |
      | no-key       | 401    | 1002 | API key not provided                          |
      | invalid-key  | 401    | 2006 | API key provided is invalid                   |
      | quota-out    | 403    | 2007 | API key has exceeded calls per month quota.   |
      | key-disabled | 403    | 2008 | API key has been disabled.                    |
      | no-access    | 403    | 2009 | API key does not have access to the resource. |
    When я запрашиваю погоду для некорректных входных данных
      | city         | code |
      | bad-city     | 1006 |
      | empty-q      | 1003 |
      | bad-url      | 1005 |
      | no-key       | 1002 |
      | invalid-key  | 2006 |
      | quota-out    | 2007 |
      | key-disabled | 2008 |
      | no-access    | 2009 |
    Then ответ с ошибкой совпадает с ожидаемым
      | status | code | message                                       |
      | 400    | 1006 | No location found matching parameter 'q'      |
      | 400    | 1003 | Parameter 'q' not provided.                   |
      | 400    | 1005 | API request url is invalid.                   |
      | 401    | 1002 | API key not provided                          |
      | 401    | 2006 | API key provided is invalid                   |
      | 403    | 2007 | API key has exceeded calls per month quota.   |
      | 403    | 2008 | API key has been disabled.                    |
      | 403    | 2009 | API key does not have access to the resource. |
