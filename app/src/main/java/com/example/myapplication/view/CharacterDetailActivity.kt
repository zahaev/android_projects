// com/example/myapplication/view/CharacterDetailActivity.kt
package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.repository.CharacterRepository
import com.example.myapplication.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.launch

class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CharacterDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        CharacterRepository.initialize(applicationContext)

        viewModel = ViewModelProvider(this)[CharacterDetailViewModel::class.java]

        findViewById<ImageView>(R.id.btnGoToMain).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val characterId = intent.getIntExtra("CHARACTER_ID", -1)
        if (characterId == -1) {
            finish()
            return
        }

        viewModel.loadCharacter(characterId)

        viewModel.character.observe(this) { character ->
            if (character != null) {
                findViewById<TextView>(R.id.textViewDetailName).text = character.name
                findViewById<TextView>(R.id.textViewDetailAge).text = "Age: ${character.age}"
                findViewById<TextView>(R.id.textViewDetailDescription).text = character.description

                Glide.with(this)
                    .load(character.imageUrl.trim().takeIf { it.isNotEmpty() })
                    .placeholder(R.drawable.placeholder_avatar) // ← должен существовать
                    .error(R.drawable.error_avatar)           // ← должен существовать
                    .into(findViewById(R.id.imageViewDetail))
            } else {
                findViewById<TextView>(R.id.textViewDetailName).text = "Character not found"
                findViewById<TextView>(R.id.textViewDetailDescription).text = "Sorry, this character could not be loaded."
            }
        }
    }
}
