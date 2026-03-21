# Rick and Morty Character Explorer

Android-приложение для просмотра и управления персонажами из вселенной Рика и Морти. Проект демонстрирует современные практики разработки под Android, включая Clean Architecture, паттерн MVVM, офлайн-режим и реактивное программирование.

## 📱 Возможности

- **Список персонажей**: Просмотр персонажей с бесконечной прокруткой (пагинация)
- **Детальная информация**: Просмотр подробной информации о каждом персонаже
- **Избранное**: Добавление персонажей в избранное и управление списком
- **CRUD операции**: Добавление собственных персонажей и удаление существующих
- **Офлайн-режим**: Кэширование данных в локальной базе Room для работы без интернета
- **Material Design**: Чистый интерфейс, следующий гайдлайнам Material Design

##  Архитектура

Проект следует принципам **Clean Architecture** с тремя слоями:

### 1. Data Layer (Слой данных)
- **Remote**: Retrofit + Moshi для API-запросов к [Rick and Morty API](https://rickandmortyapi.com/)
- **Local**: Room база данных для постоянного хранения с TypeConverters
- **Repository**: Реализация паттерна Repository с приоритетом локальных данных

### 2. Domain Layer (Доменный слой)
- Бизнес-логика и use cases
- Доменные модели (Character и др.)
- Интерфейсы репозиториев

### 3. Presentation Layer (Слой представления)
- **MVVM** с ViewModel
- Activities и Adapters
- LiveData для реактивного обновления UI

##  Технологический стек

- **Язык**: Kotlin
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Архитектура**: Clean Architecture + MVVM
- **Сеть**: Retrofit2 с Moshi
- **База данных**: Room с TypeConverters
- **Загрузка изображений**: Glide
- **Внедрение зависимостей**: Service Locator
- **Асинхронность**: Kotlin Coroutines
- **UI**: Material Design Components

##  Требования

- JDK 8 или выше
- Android Studio Hedgehog | 2023.1.1 или новее
- Android SDK с API level 21+
- Gradle 8.0+

##  Запуск проекта

### Клонирование репозитория
```bash
git clone https://github.com/yourusername/rick-and-morty-explorer.git
cd rick-and-morty-explorer
