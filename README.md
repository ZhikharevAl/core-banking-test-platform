# core-banking-test-platform

[![CI](https://github.com/ZhikharevAl/core-banking-test-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/ZhikharevAl/core-banking-test-platform/actions/workflows/ci.yml)

## Стек

- Java 21 (Temurin)
- Gradle 8.7 (Kotlin DSL)
- JUnit 5
- Checkstyle 10.17 (custom config, в т.ч. правила именования тестовых классов/методов)
- JaCoCo 0.8.12 - HTML-отчёт покрытия
- pre-commit - локальные хуки перед коммитом
- GitHub Actions - CI

## Архитектура

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

Принципы:

- **Контракты в интерфейсах.** Полиморфизм через `BankingProduct` / `CardProduct` / `DepositProduct` - клиентский код не зависит от конкретных классов.
- **Общая реализация в абстрактном базовом классе.** Валидация полей и мутации баланса - в `AbstractBankingProduct`, наследники не дублируют.
- **Расширяемость без правок.** Новый тип карты - наследник `AbstractCardProduct`. Новый тип вклада - реализация `DepositProduct`. Новая категория продукта (кредит наличными, инвестпродукт) - новый интерфейс рядом, наследующий `BankingProduct`.
- **Бизнес-правила в специализациях, а не в базе.** Запрет RUB у валютной карты живёт в `ForeignCurrencyDebitCard`. Логика долга - только в `CreditCard`.

## Запуск

Через Makefile:

```bash
make help            # все доступные команды
make ci              # checkstyle + tests + coverage (= что делает CI)
make test            # только тесты
make check           # только checkstyle
make coverage        # тесты + HTML-отчёт JaCoCo
make open-coverage   # то же + открыть отчёт в браузере
make clean           # очистить build/
```

Напрямую через Gradle:

```bash
./gradlew test
./gradlew checkstyleMain checkstyleTest
./gradlew test jacocoTestReport
```

HTML-отчёт покрытия после прогона: `build/jacocoHtml/index.html`.

![Coverage](./attachments/coverage.png)

## Тесты

Раскладка:

```
src/test/java/org/example/banking/
  BankingProductArchitectureTests.java   архитектурные проверки полиморфизма
  card/
    DebitCardTests.java
    ForeignCurrencyDebitCardTests.java
    CreditCardTests.java
  common/
    AbstractBankingProductValidationTests.java   валидация конструктора и операций базы
  deposit/
    DepositTests.java
```

## pre-commit

```bash
make precommit-install
make precommit-run
```

Хуки: trailing whitespace, EOF newline, YAML/XML/JSON sanity-check, detect-secrets, gradle checkstyle, gradle test.

## CI

`.github/workflows/ci.yml`:

- Гоняет Checkstyle и тесты на каждом push/PR.
- Кэширует Gradle через `gradle/actions/setup-gradle` - feature-ветки на read-only кэше, чтобы не размывать общий.
- `concurrency` отменяет устаревшие запуски при пуше в ту же ветку.
- Складывает HTML-отчёт JaCoCo как артефакт `jacoco-html` (доступен на вкладке Actions у запуска).
- На фейле дополнительно сохраняет отчёты Checkstyle и тестов.
