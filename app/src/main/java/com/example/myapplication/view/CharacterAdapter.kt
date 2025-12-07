package com.example.myapplication.view
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // или Picasso
import com.example.myapplication.R
import com.example.myapplication.model.Character

class CharacterAdapter(
    characters: List<Character>, // можно оставить val в конструкторе
    private val onItemClick: (Character) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    // Внутреннее изменяемое свойство
    private var charactersList: MutableList<Character> = characters.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewAvatar)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val ageTextView: TextView = view.findViewById(R.id.textViewAge)
    }

    fun updateCharacters(newCharacters: List<Character>) {
        charactersList.clear()
        charactersList.addAll(newCharacters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = charactersList[position] // ← используем charactersList
        holder.nameTextView.text = character.name
        holder.ageTextView.text = "Age: ${character.age}"

        Glide.with(holder.imageView.context)
            .load(character.imageUrl.trim()) // ← добавил .trim() на случай пробелов
            .into(holder.imageView)

        holder.imageView.contentDescription = "${character.name} avatar"

        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    override fun getItemCount() = charactersList.size // ← и здесь charactersList
}