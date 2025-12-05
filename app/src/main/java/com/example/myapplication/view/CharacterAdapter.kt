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
    private val characters: List<Character>,
    private val onItemClick: (Character) -> Unit //  обработчик клика
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewAvatar)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val ageTextView: TextView = view.findViewById(R.id.textViewAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characters[position]
        holder.nameTextView.text = character.name
        holder.ageTextView.text = "Age: ${character.age}"

        // Загрузка изображения (пример с Glide)
        Glide.with(holder.imageView.context)
            .load(character.imageUrl)
            //.placeholder(R.drawable.placeholder_avatar) // опционально
            .into(holder.imageView)

        //  Динамическое описание для доступности
        holder.imageView.contentDescription = "${character.name} avatar"
        // Обработка клика
        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
    }

    override fun getItemCount() = characters.size
}