GRADLE := ./gradlew
GRADLE_FLAGS := --no-daemon --stacktrace
COMPOSE := docker compose

.PHONY: help build test check coverage open-coverage clean ci precommit-install precommit-run \
        allure-serve allure-clean docker-test docker-allure docker-down docker-clean

help: ## Показать список команд
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-22s\033[0m %s\n", $$1, $$2}'

build: ## Скомпилировать проект (без тестов)
	$(GRADLE) build -x test $(GRADLE_FLAGS)

test: ## Прогнать тесты
	$(GRADLE) test $(GRADLE_FLAGS)

check: ## Запустить Checkstyle
	$(GRADLE) checkstyleMain checkstyleTest $(GRADLE_FLAGS)

coverage: ## Тесты + HTML-отчёт покрытия
	$(GRADLE) test jacocoTestReport $(GRADLE_FLAGS)
	@echo ""
	@echo "HTML отчёт: build/jacocoHtml/index.html"

open-coverage: coverage ## Собрать отчёт и открыть в браузере (Linux/macOS/WSL)
	@if command -v xdg-open > /dev/null; then xdg-open build/jacocoHtml/index.html; \
	elif command -v open > /dev/null; then open build/jacocoHtml/index.html; \
	else echo "Открой вручную: build/jacocoHtml/index.html"; fi

ci: check test coverage ## Полный набор проверок (как в CI)

clean: ## Очистить артефакты сборки
	$(GRADLE) clean $(GRADLE_FLAGS)


allure-serve: ## Поднять Allure UI локально на http://localhost:5050
	$(COMPOSE) up -d allure
	@echo "Allure UI: http://localhost:5050"

allure-clean: ## Очистить allure-results
	rm -rf build/allure-results


docker-test: ## Прогнать CI-набор в Docker (checkstyle + test + jacoco)
	HOST_UID=$(shell id -u) HOST_GID=$(shell id -g) $(COMPOSE) run --rm --build tests

docker-allure: docker-test allure-serve ## Тесты в Docker + Allure UI

docker-down: ## Погасить compose-стек
	$(COMPOSE) down

docker-clean: ## Снести образы, volumes и сетевые ресурсы compose-стека
	$(COMPOSE) down -v --rmi local

precommit-install: ## Установить pre-commit хуки
	pre-commit install

precommit-run: ## Прогнать pre-commit по всем файлам
	pre-commit run --all-files
