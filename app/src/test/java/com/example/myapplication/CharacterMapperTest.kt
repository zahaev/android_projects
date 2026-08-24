package com.example.myapplication

import com.example.myapplication.model.data.local.entity.CharacterEntity
import com.example.myapplication.model.data.mapper.mapCharacterDtoToEntity
import com.example.myapplication.model.data.mapper.toDomain
import com.example.myapplication.model.data.remote.dto.CharacterDto
import com.example.myapplication.model.data.remote.dto.LocationDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterMapperTest {

    // ============================================================
    // DTO -> ENTITY
    // ============================================================

    @Test
    fun `mapCharacterDtoToEntity maps all fields correctly`() {
        val dto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",

            origin = LocationDto(
                name = "Earth",
                url = "https://rickandmortyapi.com/api/location/1"
            ),

            location = LocationDto(
                name = "Citadel of Ricks",
                url = "https://rickandmortyapi.com/api/location/3"
            ),

            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",

            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
            ),

            url = "https://rickandmortyapi.com/api/character/1",

            created = "2017-11-04T18:48:46.250Z"
        )

        val entity = mapCharacterDtoToEntity(dto)

        assertEquals(1, entity.id)
        assertEquals("Rick Sanchez", entity.name)
        assertEquals("Alive", entity.status)
        assertEquals("Human", entity.species)
        assertEquals("", entity.type)
        assertEquals("Male", entity.gender)

        assertEquals("Earth", entity.originName)
        assertEquals(
            "https://rickandmortyapi.com/api/location/1",
            entity.originUrl
        )

        assertEquals("Citadel of Ricks", entity.locationName)
        assertEquals(
            "https://rickandmortyapi.com/api/location/3",
            entity.locationUrl
        )

        assertEquals(
            "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            entity.image
        )

        assertEquals(
            "https://rickandmortyapi.com/api/episode/1," +
                    "https://rickandmortyapi.com/api/episode/2",
            entity.episode
        )

        assertEquals(
            "https://rickandmortyapi.com/api/character/1",
            entity.url
        )

        assertEquals(
            "2017-11-04T18:48:46.250Z",
            entity.created
        )

        // При преобразовании DTO -> Entity
        // персонаж не должен автоматически становиться избранным.
        assertFalse(entity.isFavorite)
    }


    // ============================================================
    // NULLABLE ORIGIN / LOCATION
    // ============================================================

    @Test
    fun `mapCharacterDtoToEntity handles null origin and location`() {
        val dto = CharacterDto(
            id = 2,
            name = "Unknown Character",
            status = "unknown",
            species = "Unknown",
            type = "",
            gender = "Unknown",

            origin = null,
            location = null,

            image = "",

            episode = emptyList(),

            url = "",

            created = ""
        )

        val entity = mapCharacterDtoToEntity(dto)

        assertEquals("", entity.originName)
        assertEquals("", entity.originUrl)

        assertEquals("", entity.locationName)
        assertEquals("", entity.locationUrl)
    }


    // ============================================================
    // STATUS / SPECIES / GENDER
    // ============================================================

    @Test
    fun `mapCharacterDtoToEntity maps status species and gender`() {
        val dto = CharacterDto(
            id = 3,
            name = "Test Character",
            status = "Dead",
            species = "Alien",
            type = "Parasite",
            gender = "Female",

            origin = LocationDto(
                name = "Alien Planet",
                url = "https://example.com/origin"
            ),

            location = LocationDto(
                name = "Space Station",
                url = "https://example.com/location"
            ),

            image = "https://example.com/image.jpg",

            episode = listOf(
                "episode1"
            ),

            url = "https://example.com/character/3",

            created = "2020-01-01"
        )

        val entity = mapCharacterDtoToEntity(dto)

        assertEquals("Dead", entity.status)
        assertEquals("Alien", entity.species)
        assertEquals("Parasite", entity.type)
        assertEquals("Female", entity.gender)
    }


    // ============================================================
    // ENTITY -> DOMAIN
    // ============================================================

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = CharacterEntity(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",

            originName = "Earth",
            originUrl = "https://rickandmortyapi.com/api/location/1",

            locationName = "Citadel of Ricks",
            locationUrl = "https://rickandmortyapi.com/api/location/3",

            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",

            episode =
                "https://rickandmortyapi.com/api/episode/1," +
                        "https://rickandmortyapi.com/api/episode/2",

            url = "https://rickandmortyapi.com/api/character/1",

            created = "2017-11-04T18:48:46.250Z",

            isFavorite = true
        )

        val character = entity.toDomain()

        assertEquals(1, character.id)
        assertEquals("Rick Sanchez", character.name)
        assertEquals("Alive", character.status)
        assertEquals("Human", character.species)
        assertEquals("", character.type)
        assertEquals("Male", character.gender)

        assertEquals("Earth", character.origin.name)
        assertEquals(
            "https://rickandmortyapi.com/api/location/1",
            character.origin.url
        )

        assertEquals("Citadel of Ricks", character.location.name)
        assertEquals(
            "https://rickandmortyapi.com/api/location/3",
            character.location.url
        )

        assertEquals(
            "https://rickandmortyapi.com/api/episode/1",
            character.episode[0]
        )

        assertEquals(
            "https://rickandmortyapi.com/api/episode/2",
            character.episode[1]
        )

        assertEquals(2, character.episode.size)

        assertEquals(
            "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            character.image
        )

        assertEquals(
            "https://rickandmortyapi.com/api/character/1",
            character.url
        )

        assertEquals(
            "2017-11-04T18:48:46.250Z",
            character.created
        )

        // Проверяем сохранение favorite-флага.
        assertTrue(character.isFavorite)
    }


    // ============================================================
    // EMPTY EPISODE
    // ============================================================

    @Test
    fun `toDomain returns empty episode list when episode is blank`() {
        val entity = CharacterEntity(
            id = 4,
            name = "Test Character",
            status = "unknown",
            species = "Unknown",
            type = "",
            gender = "Unknown",

            originName = "",
            originUrl = "",

            locationName = "",
            locationUrl = "",

            image = "",

            episode = "",

            url = "",

            created = "",

            isFavorite = false
        )

        val character = entity.toDomain()

        assertTrue(character.episode.isEmpty())
    }


    // ============================================================
    // ENTITY -> DOMAIN WITH NULL-LIKE EMPTY LOCATION DATA
    // ============================================================

    @Test
    fun `toDomain correctly maps empty origin and location`() {
        val entity = CharacterEntity(
            id = 5,
            name = "Unknown",
            status = "unknown",
            species = "Unknown",
            type = "",
            gender = "Unknown",

            originName = "",
            originUrl = "",

            locationName = "",
            locationUrl = "",

            image = "",

            episode = "episode1",

            url = "",

            created = "",

            isFavorite = false
        )

        val character = entity.toDomain()

        assertEquals("", character.origin.name)
        assertEquals("", character.origin.url)

        assertEquals("", character.location.name)
        assertEquals("", character.location.url)

        assertEquals(
            listOf("episode1"),
            character.episode
        )

        assertFalse(character.isFavorite)
    }
}