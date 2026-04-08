package com.example.myapplication.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.R
import com.example.myapplication.model.domain.model.Character

class CharacterAdapter(
    characters: List<Character>,
    private val onItemClick: (Character) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    private var charactersList: MutableList<Character> = characters.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewAvatar)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val ageTextView: TextView = view.findViewById(R.id.textViewAge)
        val favoriteImageView: ImageView = view.findViewById(R.id.imageViewFavorite)
    }

    fun updateCharacters(newCharacters: List<Character>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = charactersList.size
            override fun getNewListSize() = newCharacters.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Исправлено: доступ через .id
                return charactersList[oldItemPosition].id ==
                        newCharacters[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return charactersList[oldItemPosition] ==
                        newCharacters[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        charactersList.clear()
        charactersList.addAll(newCharacters)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ViewHolder(view)
    }

    private var onItemLongClick: ((Character) -> Boolean)? = null

    fun setOnItemLongClickListener(listener: (Character) -> Boolean) {
        this.onItemLongClick = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = charactersList[position]
        //val character = characterUi.character  // ← Получаем доменную модель

        holder.nameTextView.text = character.name
        holder.ageTextView.text = "${character.status} • ${character.species}"

        // Показываем/скрываем звёздочку
        // Исправлено: убираем лишнюю проверку типа
        holder.favoriteImageView.visibility =
            if (character.isFavorite) View.VISIBLE else View.GONE

        // Загрузка изображения
        val url = character.image.trim().takeIf { it.isNotEmpty() }
        Glide.with(holder.imageView.context)
            .load(url)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.error_avatar)
            .into(holder.imageView)

        holder.imageView.contentDescription = "${character.name} avatar"

        holder.itemView.setOnClickListener {
            onItemClick(character)  // ← Передаём Character, а не CharacterUi
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(character) ?: false  // ← То же самое
        }
    }

    override fun getItemCount() = charactersList.size
}