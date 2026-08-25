package com.example.myapplication

import com.example.myapplication.model.data.local.CharacterLocalDataSourceContract
import com.example.myapplication.model.data.local.entity.CharacterEntity
import com.example.myapplication.model.data.remote.CharacterRemoteDataSourceContract
import com.example.myapplication.model.data.remote.dto.CharacterDto
import com.example.myapplication.model.data.remote.dto.LocationDto
import com.example.myapplication.model.data.repository.CharacterRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterRepositoryTest {

    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setup() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()

        repository = CharacterRepositoryImpl(
            localDataSource = fakeLocalDataSource,
            remoteDataSource = fakeRemoteDataSource
        )
    }

    // ============================================================
    // 1. ДАННЫЕ УЖЕ ЕСТЬ В ROOM
    // ============================================================

    @Test
    fun `getCharactersPage saves remote data to local database`() =
        runTest {

            fakeRemoteDataSource.characters = listOf(
                createDto(
                    id = 1,
                    name = "Rick Sanchez"
                )
            )

            val result = repository.getCharactersPage(
                page = 1,
                pageSize = 20
            )

            assertEquals(1, result.size)

            assertEquals(
                "Rick Sanchez",
                result.first().name
            )

            assertEquals(
                1,
                fakeLocalDataSource.insertedCharacters.size
            )
        }


    // ============================================================
    // 2. ДАННЫХ В ROOM НЕТ
    // ============================================================

    @Test
    fun `getCharactersPage loads data from remote when local is empty`() =
        runTest {

            fakeLocalDataSource.characters = emptyList()

            fakeRemoteDataSource.characters = listOf(
                createDto(
                    id = 1,
                    name = "Rick Sanchez"
                ),
                createDto(
                    id = 2,
                    name = "Morty Smith"
                )
            )

            val result = repository.getCharactersPage(
                page = 1,
                pageSize = 20
            )

            assertEquals(2, result.size)

            assertEquals(
                "Rick Sanchez",
                result[0].name
            )

            assertEquals(
                "Morty Smith",
                result[1].name
            )

            assertEquals(
                2,
                fakeLocalDataSource.insertedCharacters.size
            )
        }


    // ============================================================
    // 3. ОШИБКА API
    // ============================================================

    @Test
    fun `getCharactersPage returns local data when API fails`() =
        runTest {

            fakeLocalDataSource.characters = listOf(
                createEntity(
                    id = 1,
                    name = "Cached Rick"
                )
            )

            fakeRemoteDataSource.exception =
                RuntimeException("Network error")

            val result = repository.getCharactersPage(
                page = 1,
                pageSize = 20
            )

            assertEquals(1, result.size)

            assertEquals(
                "Cached Rick",
                result.first().name
            )
        }


    // ============================================================
    // 4. ОШИБКА API ПРИ ПОИСКЕ
    // ============================================================

    @Test(expected = RuntimeException::class)
    fun `getCharactersPage throws API error when search is active`() =
        runTest {

            fakeLocalDataSource.characters = listOf(
                createEntity(
                    id = 1,
                    name = "Cached Rick"
                )
            )

            fakeRemoteDataSource.exception =
                RuntimeException("Network error")

            repository.getCharactersPage(
                page = 1,
                pageSize = 20,
                searchQuery = "Rick"
            )
        }


    // ============================================================
    // 5. ПАГИНАЦИЯ
    // ============================================================

    @Test
    fun `getCharactersPage uses correct page`() =
        runTest {

            fakeRemoteDataSource.characters = listOf(
                createDto(
                    id = 21,
                    name = "Character 21"
                )
            )

            fakeLocalDataSource.characters = listOf(
                createEntity(
                    id = 21,
                    name = "Character 21"
                )
            )

            val result = repository.getCharactersPage(
                page = 2,
                pageSize = 20
            )

            assertEquals(1, result.size)

            assertEquals(
                2,
                fakeRemoteDataSource.lastRequestedPage
            )
        }


    // ============================================================
    // 6. ПАГИНАЦИЯ + РАЗМЕР СТРАНИЦЫ
    // ============================================================

    @Test
    fun `getCharactersPage calculates correct local offset`() =
        runTest {

            fakeRemoteDataSource.characters = listOf(
                createDto(
                    id = 21,
                    name = "Character 21"
                )
            )

            fakeLocalDataSource.characters = listOf(
                createEntity(
                    id = 21,
                    name = "Character 21"
                )
            )

            repository.getCharactersPage(
                page = 3,
                pageSize = 20
            )

            assertEquals(
                40,
                fakeLocalDataSource.lastOffset
            )

            assertEquals(
                20,
                fakeLocalDataSource.lastLimit
            )
        }


    // ============================================================
    // 7. FILTERS
    // ============================================================

    @Test
    fun `getCharactersPage passes filters to remote`() =
        runTest {

            fakeRemoteDataSource.characters = listOf(
                createDto(
                    id = 1,
                    name = "Rick Sanchez"
                )
            )

            val result = repository.getCharactersPage(
                page = 1,
                pageSize = 20,
                searchQuery = "Rick",
                status = "Alive",
                species = "Human",
                gender = "Male"
            )

            assertEquals(1, result.size)

            assertEquals(
                "Rick",
                fakeRemoteDataSource.lastName
            )

            assertEquals(
                "Alive",
                fakeRemoteDataSource.lastStatus
            )

            assertEquals(
                "Human",
                fakeRemoteDataSource.lastSpecies
            )

            assertEquals(
                "Male",
                fakeRemoteDataSource.lastGender
            )
        }


    // ============================================================
    // 8. FAVORITE
    // ============================================================

    @Test
    fun `toggleFavorite changes favorite state`() =
        runTest {

            fakeLocalDataSource.favoriteState[1] = false

            repository.toggleFavorite(1)

            assertTrue(
                fakeLocalDataSource.favoriteState[1] == true
            )
        }


    // ============================================================
    // 9. IS FAVORITE
    // ============================================================

    @Test
    fun `isFavorite returns current favorite state`() =
        runTest {

            fakeLocalDataSource.favoriteState[1] = true

            val result =
                repository.isFavorite(1)

            assertTrue(result)
        }


    // ============================================================
    // HELPERS
    // ============================================================

    private fun createDto(
        id: Int,
        name: String
    ): CharacterDto {

        return CharacterDto(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",

            origin = LocationDto(
                name = "Earth",
                url = "https://example.com/origin"
            ),

            location = LocationDto(
                name = "Earth",
                url = "https://example.com/location"
            ),

            image = "https://example.com/image.jpg",

            episode = listOf(
                "episode1",
                "episode2"
            ),

            url = "https://example.com/character/$id",

            created = "2020-01-01"
        )
    }

    private fun createEntity(
        id: Int,
        name: String
    ): CharacterEntity {

        return CharacterEntity(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",

            originName = "Earth",
            originUrl = "https://example.com/origin",

            locationName = "Earth",
            locationUrl = "https://example.com/location",

            image = "https://example.com/image.jpg",

            episode = "episode1,episode2",

            url = "https://example.com/character/$id",

            created = "2020-01-01",

            isFavorite = false
        )
    }


    // ============================================================
    // FAKE LOCAL DATA SOURCE
    // ============================================================

    private class FakeLocalDataSource :
        CharacterLocalDataSourceContract {

        var characters: List<CharacterEntity> =
            emptyList()

        val insertedCharacters =
            mutableListOf<CharacterEntity>()

        val favoriteState =
            mutableMapOf<Int, Boolean>()

        var lastOffset: Int = -1
        var lastLimit: Int = -1

        override suspend fun getCharactersPage(
            offset: Int,
            limit: Int
        ): List<CharacterEntity> {

            lastOffset = offset
            lastLimit = limit

            return characters
        }

        override suspend fun getCharacterById(
            id: Int
        ): CharacterEntity? {

            return characters.firstOrNull {
                it.id == id
            }
        }

        override suspend fun getFavorites():
                List<CharacterEntity> {

            return characters.filter {
                favoriteState[it.id] == true ||
                        it.isFavorite
            }
        }

        override suspend fun isFavorite(
            id: Int
        ): Boolean {

            return favoriteState[id] ?: false
        }

        override suspend fun updateFavoriteStatus(
            id: Int,
            isFavorite: Boolean
        ) {

            favoriteState[id] = isFavorite
        }

        override suspend fun insertAll(
            characters: List<CharacterEntity>
        ) {

            insertedCharacters.clear()
            insertedCharacters.addAll(characters)

            this.characters = characters
        }
    }


    // ============================================================
    // FAKE REMOTE DATA SOURCE
    // ============================================================

    private class FakeRemoteDataSource :
        CharacterRemoteDataSourceContract {

        var characters: List<CharacterDto> =
            emptyList()

        var exception: Exception? = null

        var lastRequestedPage: Int = -1

        var lastName: String? = null
        var lastStatus: String? = null
        var lastSpecies: String? = null
        var lastGender: String? = null

        override suspend fun getCharacters(
            page: Int,
            name: String?,
            status: String?,
            species: String?,
            gender: String?
        ): List<CharacterDto> {

            exception?.let {
                throw it
            }

            lastRequestedPage = page

            lastName = name
            lastStatus = status
            lastSpecies = species
            lastGender = gender

            return characters
        }
    }
}