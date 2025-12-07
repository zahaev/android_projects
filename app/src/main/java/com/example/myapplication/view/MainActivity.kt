package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Character
import com.example.myapplication.viewmodel.MainViewModel
//временно
import android.widget.Toast
import android.util.Log


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CharacterAdapter
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge (контент под статус-баром и навигацией)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        viewModel=ViewModelProvider(this)[MainViewModel::class.java]

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CharacterAdapter(emptyList()) { character ->
            Log.d("CLICK", "Opening character: ${character.name}, ID: ${character.id}")//лишний лог
            Toast.makeText(this, "Opening ${character.name}", Toast.LENGTH_SHORT).show()//лишний тост
            // Передаём весь объект (благодаря Parcelable)
            val intent = Intent(this, CharacterDetailActivity::class.java).apply {
                putExtra("CHARACTER_ID", character.id)// только ID
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        //добавим метод updateCharacters() в адаптер
        viewModel.characters.observe(this){characters ->
            adapter.updateCharacters(characters)
        }
        // Обработка системных инсетов (отступы под статус-бар и т.д.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}