// com/example/myapplication/view/MainActivity.kt
package com.example.myapplication.view

import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.coroutines.launch
import com.example.myapplication.model.domain.repository.CharacterRepository
import com.example.myapplication.viewmodel.MainViewModelFactory
import com.example.myapplication.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CharacterAdapter
    private lateinit var viewModel: MainViewModel
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Log.d("MAIN", "Initializing repository...")
        //CharacterRepository.initialize(applicationContext)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(applicationContext)
        )[MainViewModel::class.java]
        Log.d("MAIN", "Repository initialized")

        setupRecyclerView()
        setupObservers()

        // Загружаем первую страницу
        viewModel.loadFirstPage()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CharacterAdapter(emptyList()) { character ->
            Log.d("CLICK", "Opening character: ${character.name}, ID: ${character.id}")
            Toast.makeText(this, "Opening ${character.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CharacterDetailActivity::class.java).apply {
                putExtra("CHARACTER_ID", character.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Долгое нажатие → меню с выбором (Favorites / Delete)
        adapter.setOnItemLongClickListener { character ->
            lifecycleScope.launch {
                val isFavorite = viewModel.isFavoriteSync(character.id)

                AlertDialog.Builder(this@MainActivity)
                    .setTitle(character.name)
                    .setMessage(if (isFavorite) "Remove from favorites?" else "Add to favorites?")
                    .setPositiveButton(if (isFavorite) "Remove" else "Add to Favorites") { _, _ ->
                        viewModel.toggleFavorite(character)
                    }
                    .setNeutralButton("Delete") { _, _ ->
                        viewModel.deleteCharacter(character.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            true
        }

        // Пагинация
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    isLoading = true
                    viewModel.loadNextPage {
                        isLoading = false
                        }
                    }
                }
            }
        )

        // FAB для добавления
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showAddCharacterDialog()
        }
    }

    private fun setupObservers() {
        viewModel.charactersUi.observe(this) { charactersUi ->
            adapter.updateCharacters(charactersUi)
        }
    }

    private fun showAddCharacterDialog() {
        val context = this
        val nameInput = EditText(context)
        val ageInput = EditText(context)
        val urlInput = EditText(context)
        val descInput = EditText(context)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(context).apply { text = "Name:" })
            addView(nameInput)
            addView(TextView(context).apply { text = "Age:" })
            addView(ageInput)
            addView(TextView(context).apply { text = "Image URL:" })
            addView(urlInput)
            addView(TextView(context).apply { text = "Description:" })
            addView(descInput)
            setPadding(50, 0, 50, 0)
        }

        AlertDialog.Builder(context)
            .setTitle("Add New Character")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().takeIf { it.isNotBlank() } ?: return@setPositiveButton
                val age = ageInput.text.toString().takeIf { it.isNotBlank() } ?: "Unknown"
                val url = urlInput.text.toString().takeIf { it.isNotBlank() } ?: ""
                val desc = descInput.text.toString().takeIf { it.isNotBlank() } ?: "No description"

                viewModel.addCharacter(
                    name = name,
                    status = age,        // временно используем age как status
                    species = "Human",   // default значение
                    gender = "Unknown",  // default значение
                    imageUrl = url
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
