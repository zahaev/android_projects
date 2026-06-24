com.example.myapplication/
│
├── 📦 di/                          (Dependency Injection)
│   └── ServiceLocator.kt           — ручной DI контейнер
│
├── 📦 model/
│   ├── 📦 data/                    (Data Layer)
│   │   ├── 📦 local/               (Локальные данные - Room)
│   │   │   ├── AppDatabase.kt          — база данных Room
│   │   │   ├── RoomConverters.kt       — конвертеры типов
│   │   │   ├── ApiLocation.kt          — модель локации
│   │   │   ├── MetadataEntity.kt       — Entity метаданных
│   │   │   ├── CharacterLocalDataSource.kt — DAO wrapper
│   │   │   └── 📦 dao/
│   │   │       └── CharacterDao.kt     — DAO интерфейс
│   │   │
│   │   ├── 📦 remote/              (Удалённые данные - Retrofit)
│   │   │   ├── RetrofitClient.kt       — Retrofit клиент
│   │   │   ├── RickMortyApi.kt         — API интерфейс
│   │   │   ├── CharacterDto.kt         — DTO персонажа
│   │   │   ├── CharacterResponce.kt    — DTO ответа API
│   │   │   ├── LocationDto.kt          — DTO локации
│   │   │   └── CharacterRemoteDataSource.kt — Remote wrapper
│   │   │
│   │   ├── 📦 mapper/              (Мапперы)
│   │   │   └── CharacterMapper.kt      — Entity ↔ Domain
│   │   │
│   │   └── 📦 repository/
│   │       └── CharacterRepositoryImpl.kt — реализация репозитория
│   │
│   └── 📦 domain/                  (Domain Layer)
│       ├── 📦 model/
│       │   └── Character.kt            — доменная модель
│       └── 📦 repository/
│           └── CharacterRepository.kt  — интерфейс репозитория
│
├── 📦 view/                        (Presentation - View)
│   ├── MainActivity.kt                 — главная Activity (Compose)
│   ├── CharacterDetailActivity.kt      — ⚠️ СТАРАЯ (XML, отключена)
│   └── CharacterAdapter.kt             — ⚠️ СТАРЫЙ (XML, отключён)
│
├── 📦 ui/                          (Presentation - Compose UI)
│   ├── 📦 theme/
│   │   └── Theme.kt                    — тема Material3
│   ├── 📦 navigation/
│   │   └── AppNavigation.kt           — NavController
│   └── 📦 screens/
│       ├── CharacterListScreen.kt     — экран списка
│       ├── CharacterDetailScreen.kt   — экран деталей
│       └── Dialogs.kt                 — диалоги
│
└── 📦 viewmodel/                   (Presentation - ViewModel)
    ├── MainViewModel.kt               — VM для списка
    ├── MainViewModelFactory.kt        — Factory
    ├── CharacterDetailViewModel.kt    — VM для деталей
    └── CharacterDetailViewModelFactory.kt



    📊 Описание слоёв
1. Data Layer — работа с данными
Компонент
Назначение
Room (local)
Хранение персонажей в SQLite БД
Retrofit (remote)
Загрузка данных с rickandmortyapi.com
Repository
Объединяет local + remote, реализует стратегию "сначала БД, потом сеть"
Mapper
Преобразование Entity ↔ Domain модель
2. Domain Layer — бизнес-логика
Character Чистая доменная модель (без аннотаций Room/Retrofit)
CharacterRepository Абстрактный интерфейс для работы с данными
3. Presentation Layer — UI + ViewModel
UI:
  Compose : CharacterListScreen, CharacterDetailScreen, Dialogs
ViewModel:
  MainViewModel — управление списком, пагинация, избранное 
  CharacterDetailViewModel — загрузка деталей персонажа
4. DI Layer
ServiceLocator — ручная инъекция зависимостей (без Dagger/Hilt)

📦 Зависимости (ключевые библиотеки)
Retrofit + Moshi HTTP запросы + JSON парсинг
Room Локальная БД
Coil Загрузка изображений в Compose
Compose + Material3 Современный UI
Navigation Compose Навигация между экранами
ViewModel + LiveData Архитектура MVVM
Coroutines Асинхронность
🏗️ Архитектурные паттерны
MVVM — разделение UI и логики
Repository Pattern — единый источник данных
Offline-first — сначала БД, потом сеть
Manual DI — ServiceLocator вместо Hilt/Dagger
Pagination — подгрузка по 5 персонажей за раз
