package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import android.util.Log

class CharacterDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        val btnGoToMain: ImageView = findViewById<ImageView>(R.id.btnGoToMain)

        btnGoToMain.setOnClickListener {
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
        }

        //проверка что получение объекта корректное
        val characterId = intent.getIntExtra("CHARACTER_ID", -1)
        Log.d("DETAIL", "Received ID: $characterId")
        if (characterId == -1) {
           finish()
            return
        }

        val character = CharacterRepository.getCharacterById(characterId)
        if (character == null) {
            Log.e("DETAIL", "Character not found for ID: $characterId")
            findViewById<TextView>(R.id.textViewDetailName).text = "Character not found"
            return
        }

        findViewById<TextView>(R.id.textViewDetailName).text = character.name
        findViewById<TextView>(R.id.textViewDetailAge).text = "Age: ${character.age}"
        findViewById<TextView>(R.id.textViewDetailDescription).text = character.description

        Glide.with(this)
            .load(character.imageUrl)
            .into(findViewById(R.id.imageViewDetail))
            //.placeholder(R.drawable.placeholder_avatar) ВТОРАЯ СТРОКА

    }
}