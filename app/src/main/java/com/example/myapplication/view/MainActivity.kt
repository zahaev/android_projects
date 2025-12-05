package com.example.myapplication.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
//временно
import android.widget.Toast
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.model.CharacterRepository

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge (контент под статус-баром и навигацией)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Список данных из characters
            val characters = CharacterRepository.characters


        adapter = CharacterAdapter(characters) { character ->
            Log.d("CLICK", "Opening character: ${character.name}, ID: ${character.id}")//лишний лог
            Toast.makeText(this, "Opening ${character.name}", Toast.LENGTH_SHORT).show()//лишний тост
            // Передаём весь объект (благодаря Parcelable)
            val intent = Intent(this, CharacterDetailActivity::class.java).apply {
                putExtra("CHARACTER_ID", character.id)// только ID
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Обработка системных инсетов (отступы под статус-бар и т.д.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}