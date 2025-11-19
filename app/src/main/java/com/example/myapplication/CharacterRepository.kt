// CharacterRepository.kt
package com.example.myapplication

object CharacterRepository {
    val characters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            age = "70",
            imageUrl = "https://rickandmorty-friends.vercel.app/_next/image?url=https%3A%2F%2Frickandmortyapi.com%2Fapi%2Fcharacter%2Favatar%2F1.jpeg&w=1920&q=75",
            description = "Гениальный, но пьяный учёный из измерения C-137. Создатель портал-пистолета. Дедушка Морти."
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            age = "14",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            description = "Добродушный, но наивный подросток. Часто попадает в межпространственные приключения со своим дедом Риком."
        ),
        Character(
            id = 3,
            name = "Summer Smith",
            age = "17",
            imageUrl = "https://rickandmorty-friends.vercel.app/_next/image?url=https%3A%2F%2Frickandmortyapi.com%2Fapi%2Fcharacter%2Favatar%2F3.jpeg&w=1920&q=75",
            description = "Старшая сестра Морти. Амбициозная, социальная, иногда участвует в приключениях семьи."
        ),
        Character(
            id = 4,
            name = "Beth Smith",
            age = "34",
            imageUrl = "https://rickandmorty-friends.vercel.app/_next/image?url=https%3A%2F%2Frickandmortyapi.com%2Fapi%2Fcharacter%2Favatar%2F4.jpeg&w=1920&q=75",
            description = "Хирург-ветеринар, дочь Рика. Сложные отношения с мужем Джерри и отцом Риком."
        ),
        Character(
            id = 5,
            name = "Jerry Smith",
            age = "35",
            imageUrl = "https://rickandmorty-friends.vercel.app/_next/image?url=https%3A%2F%2Frickandmortyapi.com%2Fapi%2Fcharacter%2Favatar%2F5.jpeg&w=1920&q=75",
            description = "Отец Морти и Саммер. Нерешительный, консервативный, часто чувствует себя неуверенно рядом с Риком."
        )
    )

    fun getCharacterById(id: Int): Character? {
        return characters.find { it.id == id }
    }
}