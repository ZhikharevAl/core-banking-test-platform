GRADLE := ./gradlew
GRADLE_FLAGS := --no-daemon --stacktrace

.PHONY: help build test check coverage open-coverage clean ci precommit-install precommit-run

help: ## Показать список команд
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-22s\033[0m %s\n", $$1, $$2}'

build: ## Скомпилировать проект (без тестов)
	$(GRADLE) build -x test $(GRADLE_FLAGS)

test: ## Прогнать unit-тесты
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

precommit-install: ## Установить pre-commit хуки
	pre-commit install

precommit-run: ## Прогнать pre-commit по всем файлам
	pre-commit run --all-files
