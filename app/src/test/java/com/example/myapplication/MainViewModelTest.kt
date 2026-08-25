package com.example.myapplication


import com.example.myapplication.model.domain.model.Character
import com.example.myapplication.model.domain.model.Location
import com.example.myapplication.model.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeCharacterRepository
    private lateinit var viewModel: MainViewModel


    // ============================================================
    // SETUP / TEARDOWN
    // ============================================================

    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)

        repository = FakeCharacterRepository()

        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }


    // ============================================================
    // INITIAL STATE
    // ============================================================

    @Test
    fun `initial state is empty`() {

        val state = viewModel.uiState.value

        assertTrue(state.characters.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertFalse(state.endReached)

        assertEquals("", state.searchQuery)
        assertNull(state.selectedStatus)
        assertNull(state.selectedSpecies)
        assertNull(state.selectedGender)
    }


    // ============================================================
    // INITIAL LOAD
    // ============================================================

    @Test
    fun `loadFirstPage loads characters`() = runTest {

        repository.charactersToReturn =
            listOf(
                createCharacter(1),
                createCharacter(2)
            )

        viewModel.loadFirstPage()

        // loadNextPage() сразу устанавливает loading = true
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(2, state.characters.size)
        assertEquals(1, state.characters[0].id)
        assertEquals(2, state.characters[1].id)

        assertFalse(state.endReached)

        assertEquals(1, repository.calls.size)
        assertEquals(1, repository.calls[0].page)
    }


    // ============================================================
    // LOADING STATE
    // ============================================================

    @Test
    fun `loadNextPage sets loading state`() = runTest {

        repository.delayMillis = 1000L

        viewModel.loadFirstPage()

        // До завершения coroutine
        // ViewModel должна находиться в loading state.
        assertTrue(viewModel.uiState.value.isLoading)

        advanceTimeBy(1000)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }


    // ============================================================
    // ERROR STATE
    // ============================================================

    @Test
    fun `loadFirstPage sets error state when repository fails`() =
        runTest {

            repository.exception =
                RuntimeException("Network error")

            viewModel.loadFirstPage()

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)

            assertEquals(
                "Network error",
                state.errorMessage
            )

            assertTrue(state.characters.isEmpty())
            assertFalse(state.endReached)
        }


    // ============================================================
    // RETRY
    // ============================================================

    @Test
    fun `loadNextPage retries after error`() = runTest {

        repository.exception =
            RuntimeException("Network error")

        viewModel.loadFirstPage()

        advanceUntilIdle()

        assertEquals(
            "Network error",
            viewModel.uiState.value.errorMessage
        )

        // Убираем ошибку.
        repository.exception = null

        repository.charactersToReturn =
            listOf(
                createCharacter(1)
            )

        // Повторная попытка.
        viewModel.loadNextPage()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)

        assertEquals(1, state.characters.size)
        assertEquals(1, state.characters[0].id)

        // Первый запрос + retry
        assertEquals(2, repository.calls.size)
    }


    // ============================================================
    // SEARCH
    // ============================================================

    @Test
    fun `search updates query and loads filtered characters`() =
        runTest {

            repository.charactersToReturn =
                listOf(
                    createCharacter(
                        id = 10,
                        name = "Rick Sanchez"
                    )
                )

            viewModel.onSearchQueryChange("Rick")

            // Query меняется сразу.
            assertEquals(
                "Rick",
                viewModel.uiState.value.searchQuery
            )

            // Но запрос ещё не выполняется:
            // debounce = 300 ms.
            runCurrent()

            assertEquals(
                0,
                repository.calls.size
            )

            // Проходит debounce.
            advanceTimeBy(300)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "Rick",
                state.searchQuery
            )

            assertEquals(
                1,
                state.characters.size
            )

            assertEquals(
                "Rick Sanchez",
                state.characters[0].name
            )

            assertEquals(
                "Rick",
                repository.calls[0].searchQuery
            )
        }


    // ============================================================
    // STATUS FILTER
    // ============================================================

    @Test
    fun `status filter updates state and repository request`() =
        runTest {

            repository.charactersToReturn =
                listOf(
                    createCharacter(1)
                )

            viewModel.onStatusChange("Alive")

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "Alive",
                state.selectedStatus
            )

            assertEquals(
                1,
                state.characters.size
            )

            assertEquals(
                "Alive",
                repository.calls[0].status
            )
        }


    // ============================================================
    // SPECIES FILTER
    // ============================================================

    @Test
    fun `species filter updates state and repository request`() =
        runTest {

            repository.charactersToReturn =
                listOf(
                    createCharacter(1)
                )

            viewModel.onSpeciesChange("Human")

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "Human",
                state.selectedSpecies
            )

            assertEquals(
                1,
                state.characters.size
            )

            assertEquals(
                "Human",
                repository.calls[0].species
            )
        }


    // ============================================================
    // GENDER FILTER
    // ============================================================

    @Test
    fun `gender filter updates state and repository request`() =
        runTest {

            repository.charactersToReturn =
                listOf(
                    createCharacter(1)
                )

            viewModel.onGenderChange("Male")

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "Male",
                state.selectedGender
            )

            assertEquals(
                1,
                state.characters.size
            )

            assertEquals(
                "Male",
                repository.calls[0].gender
            )
        }


    // ============================================================
    // END OF LIST
    // ============================================================

    @Test
    fun `empty repository result marks end reached`() = runTest {

        repository.charactersToReturn = emptyList()

        viewModel.loadFirstPage()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.endReached)
        assertTrue(state.characters.isEmpty())
    }


    // ============================================================
    // PAGINATION
    // ============================================================

    @Test
    fun `loadNextPage loads subsequent pages`() = runTest {

        repository.responses = mutableListOf(
            listOf(
                createCharacter(1),
                createCharacter(2)
            ),
            listOf(
                createCharacter(3),
                createCharacter(4)
            )
        )

        viewModel.loadFirstPage()

        advanceUntilIdle()

        assertEquals(
            2,
            viewModel.uiState.value.characters.size
        )

        viewModel.loadNextPage()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(
            4,
            state.characters.size
        )

        assertEquals(
            listOf(1, 2, 3, 4),
            state.characters.map { it.id }
        )

        assertEquals(
            2,
            repository.calls.size
        )

        assertEquals(
            1,
            repository.calls[0].page
        )

        assertEquals(
            2,
            repository.calls[1].page
        )
    }


    // ============================================================
    // DUPLICATES
    // ============================================================

    @Test
    fun `loadNextPage removes duplicate characters`() = runTest {

        repository.responses = mutableListOf(
            listOf(
                createCharacter(1),
                createCharacter(2)
            ),
            listOf(
                createCharacter(2),
                createCharacter(3)
            )
        )

        viewModel.loadFirstPage()

        advanceUntilIdle()

        viewModel.loadNextPage()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(
            listOf(1, 2, 3),
            state.characters.map { it.id }
        )
    }


    // ============================================================
    // FILTER + SEARCH
    // ============================================================

    @Test
    fun `search and filters are passed together to repository`() =
        runTest {

            repository.charactersToReturn =
                listOf(
                    createCharacter(1)
                )

            viewModel.onSearchQueryChange("Rick")

            advanceTimeBy(300)

            advanceUntilIdle()

            // Теперь устанавливаем фильтр.
            viewModel.onStatusChange("Alive")

            advanceUntilIdle()

            val lastCall =
                repository.calls.last()

            assertEquals(
                "Rick",
                lastCall.searchQuery
            )

            assertEquals(
                "Alive",
                lastCall.status
            )
        }


    // ============================================================
    // FAVORITE
    // ============================================================

    @Test
    fun `toggleFavorite changes favorite state`() = runTest {

        repository.charactersToReturn =
            listOf(
                createCharacter(
                    id = 1,
                    isFavorite = false
                )
            )

        viewModel.loadFirstPage()

        advanceUntilIdle()

        assertFalse(
            viewModel.uiState.value.characters[0].isFavorite
        )

        viewModel.toggleFavorite(1)

        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.characters[0].isFavorite
        )

        assertEquals(
            1,
            repository.toggleFavoriteCalls
        )
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private fun createCharacter(
        id: Int,
        name: String = "Character $id",
        isFavorite: Boolean = false
    ): Character {

        return Character(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",

            origin = Location(
                name = "Earth",
                url = "https://example.com/origin"
            ),

            location = Location(
                name = "Earth",
                url = "https://example.com/location"
            ),

            image = "https://example.com/image.jpg",

            episode = listOf(
                "episode1"
            ),

            url = "https://example.com/character/$id",

            created = "2020-01-01",

            isFavorite = isFavorite
        )
    }


    // ============================================================
    // FAKE REPOSITORY
    // ============================================================

    private class FakeCharacterRepository :
        CharacterRepository {

        data class Call(
            val page: Int,
            val pageSize: Int,
            val searchQuery: String,
            val status: String?,
            val species: String?,
            val gender: String?
        )

        val calls =
            mutableListOf<Call>()

        var charactersToReturn =
            emptyList<Character>()

        var responses =
            mutableListOf<List<Character>>()

        var exception: Exception? = null

        var delayMillis: Long = 0L

        var toggleFavoriteCalls = 0


        override suspend fun getCharactersPage(
            page: Int,
            pageSize: Int,
            searchQuery: String,
            status: String?,
            species: String?,
            gender: String?
        ): List<Character> {

            calls += Call(
                page = page,
                pageSize = pageSize,
                searchQuery = searchQuery,
                status = status,
                species = species,
                gender = gender
            )

            if (delayMillis > 0) {
                kotlinx.coroutines.delay(delayMillis)
            }

            exception?.let {
                throw it
            }

            return if (responses.isNotEmpty()) {
                responses.removeAt(0)
            } else {
                charactersToReturn
            }
        }


        override suspend fun getCharacterById(
            id: Int
        ): Character? {

            return charactersToReturn
                .firstOrNull { it.id == id }
        }


        override suspend fun getFavorites(): List<Character> {

            return charactersToReturn
                .filter { it.isFavorite }
        }


        override suspend fun toggleFavorite(
            characterId: Int
        ) {

            toggleFavoriteCalls++
        }


        override suspend fun isFavorite(
            id: Int
        ): Boolean {

            return charactersToReturn
                .firstOrNull { it.id == id }
                ?.isFavorite
                ?: false
        }
    }
}