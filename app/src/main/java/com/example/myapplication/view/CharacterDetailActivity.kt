package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.viewmodel.CharacterDetailViewModel
//временно

import android.util.Log
class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CharacterDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        //иниц viewModel
        viewModel = ViewModelProvider(this)[CharacterDetailViewModel::class.java]
        //иконка возврата на главную
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


        viewModel.loadCharacter(characterId)

        viewModel.character.observe(this) { character ->
            if (character != null){
                findViewById<TextView>(R.id.textViewDetailName).text = character.name
                findViewById<TextView>(R.id.textViewDetailAge).text = "Age: ${character.age}"
                findViewById<TextView>(R.id.textViewDetailDescription).text = character.description

                Glide.with(this)
                    .load(character.imageUrl.trim())
                    .into(findViewById(R.id.imageViewDetail))
                //.placeholder(R.drawable.placeholder_avatar) ВТОРАЯ СТРОКА
            }else{
                findViewById<TextView>(R.id.textViewDetailName).text="Character not found"}
        }

    }
}