# Contribution Guidelines

## Git Workflow

Этот проект использует Git-flow модель ветвления.

### Ветки

- `master` — стабильная релизная ветка. Только production-ready код.
- `develop` — основная ветка разработки. Сюда сливаются все feature-ветки.
- `feature/*` — новые функции (например, `feature/character-search`)
- `fix/*` — исправление багов (например, `fix/pagination-bug`)
- `refactor/*` — рефакторинг без изменения поведения
- `chore/*` — технические изменения: версии, зависимости, CI
- `test/*` — добавление или исправление тестов
- `docs/*` — документация
- `release/*` — подготовка релиза
- `hotfix/*` — срочные исправления для master

#Создание ветки

git checkout -b chore/document-git-flow

# Написание commit message

git commit -m "тип ветки:описание commit message"

пример: git commit -m "chore: document git workflow"


Слияние ветки задачи в основную

chore/document-git-flow -> develop
