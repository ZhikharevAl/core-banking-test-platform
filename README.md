# core-banking-test-platform

[![CI](https://github.com/ZhikharevAl/core-banking-test-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/ZhikharevAl/core-banking-test-platform/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-blue)](https://ZhikharevAl.github.io/core-banking-test-platform/)

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.7-02303A?logo=gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber-7-23D96C?logo=cucumber&logoColor=white)
![WireMock](https://img.shields.io/badge/WireMock-3-FF6B35?logo=wiremock&logoColor=white)
![AssertJ](https://img.shields.io/badge/AssertJ-3.26-DD0031)
![Allure](https://img.shields.io/badge/Allure-2.42-FF7B00?logo=qameta&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Checkstyle](https://img.shields.io/badge/Checkstyle-10.17-purple)
![License](https://img.shields.io/badge/license-MIT-green)

Тестовая платформа из двух модулей:

- **Банковские продукты** (`src/main/java/org/example/banking/`) - карты, вклады, валидация, наследование. Покрыто unit-тестами JUnit 5.
- **Weather API tests** (`src/test/java/org/example/weather/`) - BDD-тесты сервиса погоды на Cucumber + WireMock + AssertJ. Внешний `api.weatherapi.com` нигде не вызывается, всё идёт через локально поднятые стабы.

## Оглавление

- [Запуск](#запуск)
    - [По модулям (нужен только JDK 21)](#по-модулям-нужен-только-jdk-21)
    - [Через Makefile](#через-makefile)
    - [В Docker](#в-docker)
- [Стек](#стек)
- [Архитектура](#архитектура)
    - [Task 1: Банковские продукты](#task-1--банковские-продукты)
    - [Task 2: Weather API tests](#task-2--weather-api-tests)
- [Allure](#allure)
- [Тесты](#тесты)
- [CI](#ci)
- [pre-commit](#pre-commit)
- [Troubleshooting](#troubleshooting)

## Запуск

### По модулям (нужен только JDK 21)

Самый быстрый путь - Docker не нужен, Gradle Wrapper сам подтянет всё необходимое.

```bash
./gradlew bankingTest    # только банковские unit-тесты
./gradlew weatherTest    # только Weather API (Cucumber + WireMock)
./gradlew test           # всё вместе
```

### Через Makefile

```bash
make help            # список всех команд
make test            # все тесты
make test-banking    # только банковские unit-тесты
make test-weather    # только BDD Weather API
make check           # только Checkstyle
make coverage        # тесты + HTML-отчёт JaCoCo
make open-coverage   # то же + открыть отчёт в браузере
make ci              # checkstyle + tests + coverage (= что делает CI)
make clean           # очистить build/
```

HTML-отчёт покрытия после прогона: `build/jacocoHtml/index.html`.

![Coverage](./attachments/coverage.png)

### В Docker

Изолированный прогон без локального JDK/Gradle. Docker **опционален** - если его нет, используй варианты выше.

```bash
make docker-test     # checkstyle + test + jacoco в контейнере, артефакты в ./build
make allure-serve    # Allure UI на http://localhost:5050
make docker-allure   # обе команды разом
make docker-down     # погасить allure-сервис
make docker-clean    # снести образы и volumes
```

Отчёт: `http://localhost:5050/allure-docker-service/latest-report`.

> **Важно:** не запускай `make docker-test` под `sudo` иначе артефакты в `./build`
> создадутся от root, и потом локальный Gradle/pre-commit не сможет их удалить.
> Если docker требует sudo, добавь себя в группу docker
> (см. [Troubleshooting](#troubleshooting)).

![Allure overview](./attachments/allure.png)
![Allure](./attachments/allure1.png)

## Стек

- Java 21 (Temurin) + Gradle 8.7 (Kotlin DSL)
- JUnit 5 + JUnit Platform Suite
- Cucumber 7 (`java`, `junit-platform-engine`, `picocontainer` для DI между шагами)
- WireMock JRE8 standalone - стабы внешнего HTTP API
- Jackson Databind - JSON ⇄ records
- AssertJ - soft assertions с понятными диффами
- SLF4J Simple - логирование расхождений в stdout
- Allure 2.42 - отчёты + публикация в GitHub Pages
- Checkstyle 10.17 (custom config, в т.ч. правила именования тестовых классов/методов)
- JaCoCo 0.8.12 - HTML-отчёт покрытия
- Docker + Docker Compose - изолированный прогон и локальный Allure UI
- pre-commit - локальные хуки перед коммитом
- GitHub Actions - CI и публикация Allure-отчёта на gh-pages

## Архитектура

### Task 1 - Банковские продукты

Расположение: `src/main/java/org/example/banking/`.

```
common/
  BankingProduct          интерфейс: name, currency, balance
  AbstractBankingProduct  общая реализация + валидация
  CurrencyCode            enum поддерживаемых валют

card/
  CardProduct             интерфейс карточных операций
  AbstractCardProduct     базовая реализация
  DebitCard
  ForeignCurrencyDebitCard  специализация: запрет RUB
  CreditCard              + interestRate, currentDebt

deposit/
  DepositProduct          интерфейс депозитных операций
  Deposit                 + флаг закрытия, терминальное состояние
```

**Принципы:**

- **Контракты в интерфейсах.** Полиморфизм через `BankingProduct` / `CardProduct` / `DepositProduct` - клиентский код не зависит от конкретных классов.
- **Общая реализация в абстрактном базовом классе.** Валидация полей и мутации баланса - в `AbstractBankingProduct`, наследники не дублируют.
- **Расширяемость без правок.** Новый тип карты - наследник `AbstractCardProduct`. Новый тип вклада - реализация `DepositProduct`. Новая категория продукта (кредит наличными, инвестпродукт) - новый интерфейс рядом, наследующий `BankingProduct`.
- **Бизнес-правила в специализациях, а не в базе.** Запрет RUB у валютной карты живёт в `ForeignCurrencyDebitCard`. Логика долга - только в `CreditCard`.

### Task 2 - Weather API tests

Расположение: `src/test/java/org/example/weather/`. Слоёная архитектура - каждый слой делает одну вещь, ни один не знает деталей соседнего:

```
api/                              транспорт + контракт
  WeatherApiClient                тонкий HTTP-клиент над JDK HttpClient
  WeatherEndpoint                 enum путей API
  CurrentWeatherRequest           record-DTO запроса
  ApiCallResult                   обёртка над HttpResponse + URL
  WeatherResponseParser           изолированный Jackson-парсер (snake_case)
  WeatherApiClientException       доменное исключение

model/                            типизированные DTO ответа
  Location, Current, Condition, CurrentWeatherResponse, ApiError

wiremock/                         тестовая инфраструктура
  WireMockServerHolder            scenario-scoped экземпляр WireMockServer
  WeatherStubs                    декларативные стаб-билдеры

fixtures/                         наборы тестовых данных
  CurrentWeatherFixture           вход для стабов и ожидания позитива
  ApiErrorFixture                 вход для стабов негатива
  ErrorRequestCase                пара (город, код) для When-шага негатива
  ExpectedApiError                ожидание HTTP-ответа в Then-шаге негатива

hooks/
  WireMockHooks                   @Before/@After lifecycle

support/
  AllureAttachments               прицепляет request/response к Allure-шагам
  DataTableTypes                  конвертеры Cucumber DataTable → record-фикстуры
  Mismatches                      stdout-лог расхождений «ожидаемое vs фактическое»
  WeatherTestContext              состояние сценария (LinkedHashMap)

steps/                            тонкие Cucumber step definitions
  CurrentWeatherSteps             happy path + сценарий расхождений
  WeatherErrorSteps               400/401/403

config/
  WeatherTestConfig               константы (API_KEY, timeout)

CucumberTestRunnerTests           JUnit Platform suite
```

**Особенности архитектуры:**

- **Степы не знают про HTTP, WireMock и JSON.** В `CurrentWeatherSteps`/`WeatherErrorSteps` нет ни одного `import java.net.http.*` или `ObjectMapper`. Только оркестрация: фикстура → стаб → клиент → DTO → AssertJ.
- **DI между шагами через `cucumber-picocontainer`.** `WireMockServerHolder`, `WeatherApiClient`, `WeatherStubs`, `WeatherTestContext`, `DataTableTypes` создаются один раз на сценарий и шарятся между хуками и степами без статиков.
- **Парсинг DataTable идиоматичный.** `DataTableTypes` через `@DataTableType` превращает строки таблиц в типизированные records. Степы получают `List<Fixture>` напрямую, без `Map<String, String>`.
- **Типизированные DTO вместо `JsonNode`.** snake_case JSON маппится через `PropertyNamingStrategies.SNAKE_CASE` в одной точке (`WeatherResponseParser`).
- **Stubы декларативны.** Шаги передают фикстуру, `WeatherStubs` знает, как из неё собрать WireMock-mapping. Формат стаба меняется в одном месте.
- **Soft assertions через AssertJ.** Все проверки в Then-шагах в одной `SoftAssertions.assertSoftly(...)` - видно все фейлы сразу, а не первый.
- **Расхождения летят в stdout.** `Mismatches.report(key, field, expected, actual)` пишет `WARN [Moscow] tempC mismatch: expected=20.0, actual=17.5` - требование ТЗ продублировано рядом с Allure-проверками. Отдельный сценарий «Расхождения значений фиксируются в логе» демонстрирует это.
- **Allure-аттачменты автоматические.** `WeatherApiClient` дёргает `AllureAttachments` в одну точку в отчёте виден реальный URL и тело ответа.

## Allure

Локально:

```bash
make docker-test            # генерирует build/allure-results
make allure-serve           # http://localhost:5050
```

Поскольку `CHECK_RESULTS_EVERY_SECONDS: "3"`, после каждого нового прогона достаточно обновить страницу.

**Что попадает в отчёт:**

- **Categories** - кастомные группы из `src/test/resources/allure/categories.json` (Infrastructure / API / Cucumber / Assertion error). Соглашение: `failed` - реальный баг, `broken` - тест сломался не из-за прода (NPE, инфра).
- **Environment** - версии Java, Gradle, Cucumber, WireMock, AssertJ, Allure, Jackson + ОС. Генерируется gradle-таской `writeAllureEnvironment` из реальной среды.
- **Steps + Attachments** - у каждого HTTP-вызова виден URL запроса и pretty-printed JSON ответа.

**Публикация в CI:** job `publish-allure-report` (только из `main`, при `success() || failure()`) ставит Allure CLI, мержит историю трендов из `gh-pages` и публикует через `peaceiris/actions-gh-pages`. Один раз настроить в GitHub: Settings → Pages → ветка `gh-pages` root `/`; Settings → Actions → Workflow permissions → **Read and write**. Отчёт: `https://ZhikharevAl.github.io/core-banking-test-platform/`.

## Тесты

```
src/test/java/org/example/banking/
  BankingProductArchitectureTests.java          архитектурные проверки полиморфизма
  card/    DebitCardTests, ForeignCurrencyDebitCardTests, CreditCardTests
  common/  AbstractBankingProductValidationTests
  deposit/ DepositTests

src/test/java/org/example/weather/
  CucumberTestRunnerTests.java                  JUnit Platform suite
  api/ model/ wiremock/ hooks/ steps/ support/ fixtures/ config/

src/test/resources/
  allure/categories.json                        кастомные группы для отчёта
  features/weather/
    current_weather.feature                     позитив (4 города) + расхождения
    weather_error.feature                       400/401/403 — все 8 кодов из swagger
```

**Покрытие Weather API:**

| Status | Code | Что проверяется                                |
|--------|------|------------------------------------------------|
| 200    | —    | success: location + current                    |
| 400    | 1003 | Parameter 'q' not provided                     |
| 400    | 1005 | API request url is invalid                     |
| 400    | 1006 | No location found matching parameter 'q'       |
| 401    | 1002 | API key not provided                           |
| 401    | 2006 | API key provided is invalid                    |
| 403    | 2007 | API key has exceeded calls per month quota     |
| 403    | 2008 | API key has been disabled                      |
| 403    | 2009 | API key does not have access to the resource   |

## CI

`.github/workflows/ci.yml`:

- Гоняет Checkstyle и тесты на каждом push + по ручному запуску (`workflow_dispatch`).
- Кэширует Gradle через `gradle/actions/setup-gradle` — feature-ветки на read-only кэше.
- `concurrency` отменяет устаревшие запуски при пуше в ту же ветку.
- Складывает JaCoCo HTML и `allure-results` как артефакты; на фейле - отчёты Checkstyle и тестов.
- Из `main` публикует Allure-отчёт с трендами на GitHub Pages.

## pre-commit

```bash
make precommit-install
make precommit-run
```

**Хуки:** trailing whitespace, EOF newline, YAML/XML/JSON sanity-check, mixed line endings, check-added-large-files (1 MB), check-merge-conflict, detect-secrets, `gradle checkstyleMain checkstyleTest`, `gradle test`.

> pre-commit проверяет **застейдженные** файлы. После правки делай `git add`, потом `git commit` - иначе хук гоняет старую версию из индекса.

## Troubleshooting

**`docker` требует `sudo`.**
Пользователь не в группе `docker`. Запускать сборку под sudo нельзя - `HOST_UID` станет 0, и `./build/` заполнится root-овыми файлами. Добавь себя в группу и перелогинься (в WSL - `wsl --shutdown`, иначе группа не подхватится):

```bash
sudo usermod -aG docker $USER
newgrp docker        # или полный релогин; в WSL - wsl --shutdown из PowerShell
docker ps            # должно работать без sudo
```

**После `sudo`-прогона сломались локальные команды (`make test`, `make test-banking`, pre-commit) - `Permission denied`.**
Под sudo Gradle/Docker создали в `build/` и `.gradle/` файлы от root, обычный пользователь их не перезапишет. Снеси обе папки и пересоздай обычным прогоном:

```bash
sudo rm -rf build .gradle
make test-banking      # пересоздаст всё под твоим пользователем
```

**Docker: `java.io.IOException: Permission denied` в `GradleUserHomeScopeFileTimeStampInspector` / `~/.gradle`.**
Named volume `gradle-cache` был создан под другим UID:GID, чем тот, под которым сейчас бежит контейнер. Частая причина - твой **GID не 1000, а 1001** (проверь `id -g`), а в старом образе/волюме зашит `1000:1000`. Лечится сносом volume и пересборкой с актуальными ARG:

```bash
docker compose down -v          # -v обязателен: сносит gradle-cache
sudo rm -rf build .gradle
docker compose build --no-cache tests
make docker-test
```

`Dockerfile` принимает `ARG HOST_UID/HOST_GID`, а `docker-compose.yaml` пробрасывает их и в `build.args`, и в `user:` - поэтому владелец файлов в образе, runtime-пользователь и volume совпадают по UID:GID.

**Docker: `Failed to create parent directory '/workspace/build/classes'`.**
Папка `./build` отсутствовала на хосте (например, после `sudo rm -rf build`), docker создал точку bind-mount `./build` от root, и контейнер под `1000:100x` не может в неё писать. Цель `docker-test` в `Makefile` предсоздаёт папку под твоим пользователем до запуска:

```makefile
docker-test:
	@mkdir -p build
	HOST_UID=$(shell id -u) HOST_GID=$(shell id -g) $(COMPOSE) run --rm --build tests
```

Если ошибка уже случилась: `sudo rm -rf build` → `make docker-test` (создаст `build/` под тобой). После прогона `ls -la build/` должен показывать твоего пользователя как владельца.

**Полный ритуал восстановления Docker (когда права запутались окончательно).**

```bash
docker compose down -v
sudo rm -rf build .gradle
docker compose build --no-cache tests
make docker-test
ls -la build/        # владелец твой пользователь, не root
```

**Allure UI на `:5050` пустой / `Unknown` / 0 test cases.**
Папка `build/allure-results` пустая или сервис поднялся раньше прогона. Порядок: сначала `make docker-test`, потом `make allure-serve`. Если allure уже висит `docker compose restart allure`.

**`make docker-test` не подхватывает свежий код.**
`docker compose run` использует закешированный образ. Цель `docker-test` уже содержит `--build`, но при сомнениях - `docker compose build tests` вручную.

**`gradle test` показывает `UP-TO-DATE` и не перезапускается.**
Кэш Gradle. Цели `test`/`bankingTest`/`weatherTest` сбрасывают его через `outputs.upToDateWhen { false }`; разово можно `./gradlew test --rerun-tasks`.

**`SLF4J: No providers were found` в начале прогона.**
Информационное предупреждение от первого сценария до инициализации `slf4j-simple`. На результат и на WARN-логи расхождений не влияет.

**`workflow_dispatch` не показывает кнопку «Run workflow».**
Кнопка появляется, только когда `ci.yml` есть в default-ветке (`main`). Влей ветку или запушь yml в main.
